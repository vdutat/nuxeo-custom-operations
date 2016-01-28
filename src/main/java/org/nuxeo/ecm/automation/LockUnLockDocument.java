/*
 * (C) Copyright ${year} Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     vdutat
 */

package org.nuxeo.ecm.automation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 *
 */
@Operation(id = LockUnLockDocument.ID, category = Constants.CAT_DOCUMENT, label = "LockUnLockDocument", description = "")
public class LockUnLockDocument {

    public static final String ID = "LockUnLockDocument";

    private static final Log LOGGER = LogFactory.getLog(LockUnLockDocument.class);

    @Context
    protected CoreSession opSession;

    @Context
    protected OperationContext ctx;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) throws ClientException {
        final String id = input.getId();
        new UnrestrictedSessionRunner(opSession) {
            @Override
            public void run() {
                DocumentModel doc;
                try {
                    doc = session.getDocument(new IdRef(id));
                    if (!doc.isLocked()) {
                        doc.setLock();
                    } else {
                        doc.removeLock();
                    }
                } catch (ClientException e) {
                    LOGGER.error(e, e);
                }
            }
        }.runUnrestricted();
        return input;
    }

}
