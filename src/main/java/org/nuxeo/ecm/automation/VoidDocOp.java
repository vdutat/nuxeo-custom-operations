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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.event.Event;

/**
 * https://jira.nuxeo.com/browse/SUPNXP-18523
 *
 * @author vdutat
 */
@Operation(id=VoidDocOp.ID, category=Constants.CAT_DOCUMENT, label="Void document operation", description="Describe here what your operation does.")
public class VoidDocOp {

    public static final String ID = "Document.VoidDocOp";
    private Log log = LogFactory.getLog(VoidDocOp.class);

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModel run(DocumentModel input) {
        log.warn("title of document being modified: " + input.getTitle());
        log.warn("title of document being modified: " + input.getPropertyValue("dc:title"));
        log.warn("document title of document in repository: " + session.getDocument(input.getRef()).getPropertyValue("dc:title"));
        log.warn("title of document being modified: " + input.getPropertyValue("dc:title"));
        DocumentModel prev = (DocumentModel) ((Event) ctx.get("Event")).getContext().getProperty(CoreEventConstants.PREVIOUS_DOCUMENT_MODEL);
        log.warn("previous DocumentModel: " + prev);
        log.warn("previous document model's title: " + prev.getPropertyValue("dc:title"));
        return input;
    }
}
