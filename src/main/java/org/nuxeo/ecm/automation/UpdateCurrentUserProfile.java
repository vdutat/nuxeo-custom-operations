/*
 *
 * Contributors:
 *     vdutat
 */

package org.nuxeo.ecm.automation;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 *
 */
@Operation(id=UpdateCurrentUserProfile.ID, category=Constants.CAT_SERVICES, label="Update current user profile", description="Allows current user to change some properties of his or her profile")
public class UpdateCurrentUserProfile {

    public static final String ID = "Services.UpdateCurrentUserProfile";

    @Context
    protected UserManager um;

    @Context
    protected OperationContext ctx;

    @Param(name = "firstName", required = false)
    protected String firstName;

    @Param(name = "lastName", required = false)
    protected String lastName;

    @Param(name = "company", required = false)
    protected String company;

    @Param(name = "email", required = false)
    protected String email;

    @OperationMethod
    public void run() {
        new UnrestrictedSessionRunner(ctx.getCoreSession()) {

            @Override
            public void run() {
                boolean changed = false;
                DocumentModel userModel = um.getUserModel(ctx.getPrincipal().getName());
                if (StringUtils.isNotBlank(firstName)) {
                    userModel.setProperty(um.getUserSchemaName(), "firstName", firstName);
                    changed = true;
                }
                if (StringUtils.isNotBlank(lastName)) {
                    userModel.setProperty(um.getUserSchemaName(), "lastName", lastName);
                    changed = true;
                }
                if (StringUtils.isNotBlank(company)) {
                    userModel.setProperty(um.getUserSchemaName(), "company", company);
                    changed = true;
                }
                if (StringUtils.isNotBlank(email)) {
                    userModel.setProperty(um.getUserSchemaName(), "email", email);
                    changed = true;
                }
                if (changed) {
                    um.updateUser(userModel);
                }
            }
        }.runUnrestricted();
    }

}
