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
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.template.api.TemplateProcessorService;

/**
 * @author vdutat
 */
@Operation(id=AssociateRenderingTemplate.ID, category=Constants.CAT_SERVICES, label="AssociateRenderingTemplate", description="")
public class AssociateRenderingTemplate {

    public static final String ID = "Services.AssociateRenderingTemplate";
    private Log log = LogFactory.getLog(AssociateRenderingTemplate.class);

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @Context
    protected TemplateProcessorService tps;

    @Param(name = "template", required=true)
    protected DocumentRef templateRef; // the path or the ID

    @Param(name = "save", required=false, description="save document (default: true)")
    protected boolean save = true;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {
        return tps.makeTemplateBasedDocument(input, session.getDocument(templateRef), save);
    }

}
