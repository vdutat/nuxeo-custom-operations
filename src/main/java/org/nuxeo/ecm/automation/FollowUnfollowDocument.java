/**
 *
 */

package org.nuxeo.ecm.automation;

import java.util.List;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ec.notification.NotificationConstants;
import org.nuxeo.ecm.platform.notification.api.NotificationManager;

/**
 * https://jira.nuxeo.com/browse/SUPNXP-17265
 *
 * @author vdutat
 */
@Operation(id=FollowUnfollowDocument.ID, category=Constants.CAT_DOCUMENT, label="FollowUnfollowDocument", description="")
public class FollowUnfollowDocument {

    public static final String ID = "Document.FollowUnfollow";

    @Context
    protected OperationContext ctx;

    @Context
    protected NotificationManager notificationManager;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {
    	DocumentRef ref = input.getRef();
        NuxeoPrincipal principal = (NuxeoPrincipal) ctx.getPrincipal();
        List<String> userSubscriptions = notificationManager.getSubscriptionsForUserOnDocument( NotificationConstants.USER_PREFIX + principal.getName(), input);
        if (userSubscriptions.isEmpty()) {
            notificationManager.addSubscriptions(NotificationConstants.USER_PREFIX + principal.getName(), input, false, principal);
        } else {
            notificationManager.removeSubscriptions(NotificationConstants.USER_PREFIX + principal.getName(), userSubscriptions, input);
        }
        return ctx.getCoreSession().getDocument(ref);
    }

}
