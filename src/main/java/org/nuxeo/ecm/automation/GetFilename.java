package org.nuxeo.ecm.automation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * SUPNXP-24325
 */
@Operation(id=GetFilename.ID, category=Constants.CAT_DOCUMENT, label="Document.GetFilename", description="Describe here what your operation does.")
public class GetFilename {

    public static final String ID = "Document.GetFilename";

    @Context
    protected CoreSession session;
    
    private static final Log log = LogFactory.getLog(GetFilename.class);

    @OperationMethod
    public DocumentModel run(DocumentModel input) {
        String filename = input.getProperty("file:content").getValue(Blob.class).getFilename();
        log.warn(ID + ": " + filename);
        return input;
    }
}
