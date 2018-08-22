package org.nuxeo.ecm.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 */
@Operation(id=NullPointerException.ID, category=Constants.CAT_DOCUMENT, label="NullPointerException", description="Describe here what your operation does.")
public class NullPointerException {

    public static final String ID = "Document.NullPointerException";

    @Context
    protected CoreSession session;

    @OperationMethod
    public DocumentModel run() {
        throw new java.lang.NullPointerException();
    }
}
