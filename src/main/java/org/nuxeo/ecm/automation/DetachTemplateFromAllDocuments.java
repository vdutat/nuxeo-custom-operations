/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     vdutat
 *
 */

package org.nuxeo.ecm.automation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.nuxeo.template.api.TemplateProcessorService;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;
import static org.nuxeo.ecm.core.query.sql.NXQL.ECM_UUID;
import static org.nuxeo.ecm.core.query.sql.NXQL.NXQL;

/**
 * https://jira.nuxeo.com/browse/SUPNXP-17687
 *
 * @author vdutat
 */
@Operation(id=DetachTemplateFromAllDocuments.ID, category=Constants.CAT_SERVICES, label="DetachTemplateFromAllDocuments", description="Detach the current Template from all the documents. Return the number of documents in a context variable named: nrDocsDetached")
public class DetachTemplateFromAllDocuments {

    public static final String ID = "DetachTemplateFromAllDocuments";
    private static final int BATCH_SIZE = 50;
    private Log log = LogFactory.getLog(DetachTemplateFromAllDocuments.class);

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @Param(name = "batchSize", required = false)
    protected Integer batchSize = BATCH_SIZE;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {
        // input needs to be a Document Template:
        String docsWithTemplateQuery =
//                "SELECT *"
                "SELECT ecm:uuid"
                + " FROM Document WHERE ecm:mixinType = 'TemplateBased' AND ecm:currentLifeCycleState != 'deleted' AND nxts:bindings/*/templateId IN %s";
        //query the repository
        List<String> versionIds = getTemplateAndVersionsUUIDs(input);
		String query = NXQLQueryBuilder.replaceStringList(docsWithTemplateQuery, versionIds, true, false, "%s");
        log.warn("<DetachTemplateFromAllDocuments> " + query);
//		DocumentModelList docsWithTemplateList = session.query(query);
		List<String> docsWithTemplateList = getDocumentsWithTemplate(session, query);
        // number of docs to detach
        log.warn("<DetachTemplateFromAllDocuments> " + docsWithTemplateList.size() + " documents found");
        int nrDocsDetached = 0;
        TemplateProcessorService tps = Framework.getLocalService(TemplateProcessorService.class);
        if (tps != null) {
//            for(DocumentModel doc: docsWithTemplateList) {
            for(String id: docsWithTemplateList) {
                DocumentModel doc = session.getDocument(new IdRef(id));
                try {
                    if (doc.isVersion()) {
                      //allow modify document version:
                        doc.putContextData(CoreSession.ALLOW_VERSION_WRITE, Boolean.TRUE);

                        log.error("doc name : " + doc.getPropertyValue("dc:title").toString() + " (" + doc.getId() +  ") and my version is: -- " + doc.getVersionLabel().toString());
                    }
                    // detach the template for the current document
                    List<String> versionTitles = versionIds.stream().map(IdRef::new).map(ref -> session.getDocument(ref)).map(dm -> dm.getAdapter(TemplateSourceDocument.class).getName())
                    		//.distinct()
                    		.collect(Collectors.toList());
                    for (String templateName : versionTitles) {
                    	DocumentModel detachedDocument = tps.detachTemplateBasedDocument(doc, templateName, true);
                    	nrDocsDetached++;
                    	log.error(nrDocsDetached + "_____ The document detached is: " + detachedDocument.getPropertyValue("dc:title").toString() + " (" + doc.getId() + ")---" + templateName);
                    }
                } catch (Exception e) {
                    log.error("The template could not be detached from the document: ", e);
                    log.error(doc);
                }
                // commit transaction
                if (batchSize != 0 && nrDocsDetached % batchSize == 0) {
                    log.warn("Committing transaction ...");
                    session.save();
                    if (TransactionHelper.isTransactionActive()) {
                        TransactionHelper.commitOrRollbackTransaction();
                        TransactionHelper.startTransaction();
                    }
                }

            }
        }
        // adding the number of documents detached to the context
        ctx.put("nrDocsDetached", nrDocsDetached);
        return input;
    }

    protected List<String> getTemplateAndVersionsUUIDs(DocumentModel templateDoc) {
        TemplateSourceDocument template = templateDoc.getAdapter(TemplateSourceDocument.class);
        List<String> uuids = new ArrayList<String>();
        if (template != null) {
            uuids.add(templateDoc.getId());
            for (DocumentModel version : session.getVersions(templateDoc.getRef())) {
            	uuids.add(version.getId());
            }
        }
        return uuids;
    }

    protected List<String> getDocumentsWithTemplate(CoreSession session, String query) {
        List<String> ids = new ArrayList<String>();
        IterableQueryResult it = null;
        try {
            it = session.queryAndFetch(query, NXQL);
            Spliterator<Map<String, Serializable>> spliterator = Spliterators.spliteratorUnknownSize(it.iterator(), Spliterator.NONNULL);
            ids = StreamSupport.stream(spliterator, false).map(map -> (String) map.get(ECM_UUID)).collect(Collectors.toList());
        } finally {
            if (it != null) {
                it.close();
            }
        }
        return ids;
    }

}
