package org.nuxeo.ecm.automation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * SUPNXP-24283 Remove batched of non-folderish documents from a document path.
 */
@Operation(id=BatchRemoveFolderDocuments.ID, category=Constants.CAT_DOCUMENT, label="Repository.BatchRemoveFolderDocuments", description="Describe here what your operation does.")
public class BatchRemoveFolderDocuments {

    public static final String ID = "Repository.BatchRemoveFolderDocuments";
    
    protected int DEFAULT_BATCH_SIZE = 1000;
    
    private static final Log log = LogFactory.getLog(BatchRemoveFolderDocuments.class);

    @Context
    protected CoreSession session;

    @Param(name = "path", required = true)
    protected String path;

    @Param(name = "batchsize", required = false)
    protected int batchsize = DEFAULT_BATCH_SIZE;

    @OperationMethod
    public DocumentModel run() {
        DocumentModel folder = session.getDocument(new PathRef(path));        
        String query = "SELECT " + NXQL.ECM_UUID + " FROM Document WHERE "
                + NXQL.ECM_PARENTID + Operator.EQ.toString() + NXQL.escapeString(folder.getId())
                + Operator.AND.toString() + " " + NXQL.ECM_MIXINTYPE + Operator.NOTEQ + " " + NXQL.escapeString(FacetNames.FOLDERISH);
        List<String> uuids = getUuids(session, query).stream().collect(Collectors.toList());
        log.info(String.format("Removing %d documents", uuids.size()));
        for (List<String> partition : ListUtils.partition(uuids, batchsize)) {
            List<IdRef> refs = partition.stream().map(IdRef::new).collect(Collectors.toList());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Processing next batch of %d documents, first document ID is %s", refs.size(), refs.get(0).toString()));
            }
            log.warn("Documents to remove: " + refs);
            session.removeDocuments((DocumentRef[]) refs.toArray(new DocumentRef[0]));
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
        }
        return folder;
    }

    protected List<String> getUuids(CoreSession session, String query) {
        List<String> ids = new ArrayList<String>();
        try (IterableQueryResult it = session.queryAndFetch(query, NXQL.NXQL)) {
            Spliterator<Map<String, Serializable>> spliterator = Spliterators.spliteratorUnknownSize(it.iterator(), Spliterator.NONNULL);
            ids = StreamSupport.stream(spliterator, false).map(map -> (String) map.get(NXQL.ECM_UUID)).collect(Collectors.toList());
        }
        return ids;
    }
}
