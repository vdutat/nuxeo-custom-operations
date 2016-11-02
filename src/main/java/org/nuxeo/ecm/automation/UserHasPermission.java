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
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * https://jira.nuxeo.com/browse/SUPNXP-18288
 * @author vdutat
 */
@Operation(id=UserHasPermission.ID, category=Constants.CAT_DOCUMENT, label="User Has Permission", description="")
public class UserHasPermission {

    public static final String ID = "UserHasPermission";
    private Log log = LogFactory.getLog(UserHasPermission.class);

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @Context
    protected UserManager um;

    @Param(name = "username", required=true)
    protected String username;

    @Param(name = "permission", required=true)
    protected String permission;

    @OperationMethod
    public Blob run(DocumentModel input) {
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() {
                NuxeoPrincipal principal = um.getPrincipal(username);
                boolean hasPerm = session.hasPermission(principal, input.getRef(), permission);
                ctx.put("userHasPermission", hasPerm);
            }
        }.runUnrestricted();
        log.warn("userHasPermission:" + ctx.get("userHasPermission"));
        return Blobs.createBlob(Boolean.toString((boolean) ctx.get("userHasPermission")));
    }

}
