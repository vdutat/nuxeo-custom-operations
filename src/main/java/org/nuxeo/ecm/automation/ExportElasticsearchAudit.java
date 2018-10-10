package org.nuxeo.ecm.automation;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.elasticsearch.api.ElasticSearchAdmin;
import org.nuxeo.elasticsearch.audit.io.AuditEntryJSONReader;

/**
 * SUPNXP-24083 
 */
@Operation(id=ExportElasticsearchAudit.ID, category=Constants.CAT_DOCUMENT, label="Document.ExportElasticsearchAudit", description="Describe here what your operation does.")
public class ExportElasticsearchAudit {

    public static final String ID = "Document.ExportElasticsearchAudit";

    @Context
    protected CoreSession session;

    @Context ElasticSearchAdmin esa;

    private static final Log log = LogFactory.getLog(ExportElasticsearchAudit.class);

    @OperationMethod
    public DocumentModel run(DocumentModel input) throws IOException {
        String auditIndexName = "nuxeo-audit";
        int esSize = 20;
        
        SearchRequest request = new SearchRequest(auditIndexName);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                .must(QueryBuilders.termQuery("extended.tenantId", tenantId))
//                .must(QueryBuilders.termsQuery("eventId", Arrays.asList(eventIds.values())))
//                .must(QueryBuilders.termsQuery("docType", Arrays.asList(docTypes.values())))
//                .mustNot(QueryBuilders.termQuery("comment", "OWNER-ACL"))
//                .must(QueryBuilders.rangeQuery("eventDate").lte(endDate));
                .must(QueryBuilders.termsQuery("docUUID", input.getId()));
        builder.query(queryBuilder);
        builder.size(esSize ).sort("eventDate",SortOrder.DESC);
        request.source(builder);
        request.scroll(TimeValue.timeValueMinutes(1L));
        SearchResponse response = esa.getClient().search(request);

        String scrollId = response.getScrollId();
        SearchHits hits = response.getHits();

        ArrayList<LogEntry> exportLogList = null;
        //File zipFolder = null;
        if (hits.getHits().length == 0) {
//            responseMessage = "Result count is zero";
//            statusCode = Status.NO_CONTENT;
        } else {
            // Actual size of search results
            // Retrieve all the search hits by calling the Search Scroll api in a loop until
            // no documents are returned
            while (hits.getHits().length > 0) {
                exportLogList = new ArrayList<LogEntry>();
                for (SearchHit hit : hits) {
                    log.warn("<ExportElasticsearchAudit> Hit: " + hit.getSourceAsString());
                    LogEntry entry = AuditEntryJSONReader.read(hit.getSourceAsString());
                    exportLogList.add(entry);
                }
                //zipFolder = generateCsvFile(exportLogList, userProfileLanguage);
                // Create a new SearchScrollRequest holding the last returned scroll identifier
                // and the scroll interval
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                // Scroll interval as a TimeValue
                scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
                // Synchronous Execution
                response = esa.getClient().searchScroll(scrollRequest);
                // Scroll Id is unique for each call
                scrollId = response.getScrollId();
                hits = response.getHits();
            }
        }
        return input;
    }
}

