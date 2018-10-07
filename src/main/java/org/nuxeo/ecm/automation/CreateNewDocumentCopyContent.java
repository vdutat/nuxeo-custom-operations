package org.nuxeo.ecm.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelFactory;
import org.nuxeo.ecm.core.api.validation.DocumentValidationService;
import org.nuxeo.ecm.core.api.validation.DocumentValidationService.Forcing;

/**
 * SUPNXP-23382
 * node nuxeo_operation.js /default-domain/workspaces/SUPNXP-23382/SUPNXP-23382-1  Document.CreateNewDocumentCopyContent -p "\"path\":\"/default-domain/workspaces/SUPNXP-23382/Copies\"" -v
 */
@Operation(id=CreateNewDocumentCopyContent.ID, category=Constants.CAT_DOCUMENT, label="Document.CreateNewDocumentCopyContent", description="Describe here what your operation does.")
public class CreateNewDocumentCopyContent {

    public static final String ID = "Document.CreateNewDocumentCopyContent";

    @Context
    protected CoreSession session;

    @Param(name = "path", required = true)
    protected String path;

    @OperationMethod
    public DocumentModel run(DocumentModel inDoc) {
        DocumentModel newDoc = null;
        //Create a Doc Reference inside '/default-domain/workspaces/Work'
        newDoc = DocumentModelFactory.createDocumentModel(inDoc.getType());
        newDoc.copyContent(inDoc);
        newDoc.copyContextData(inDoc);
        newDoc.setPathInfo(path, inDoc.getName());
        newDoc.putContextData(DocumentValidationService.CTX_MAP_KEY, Forcing.TURN_OFF);
        // The following fails because a user present in one of the document's properties
        // was deleted (if validation is not forcibly turned off with the previous line of code).
        return session.createDocument(newDoc);
    }
}
