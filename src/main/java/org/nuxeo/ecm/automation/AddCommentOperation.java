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
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.platform.comment.api.CommentManager;
import org.nuxeo.ecm.platform.comment.service.CommentServiceHelper;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 */
@Operation(id=AddCommentOperation.ID, category=Constants.CAT_SERVICES, label="Add comment", description="Add provided comment to input document, Returns input document.")
public class AddCommentOperation {

    public static final String ID = "AddCommentOperation";

    private static final Log LOGGER = LogFactory.getLog(AddCommentOperation.class);

    @Context
    protected OperationContext ctx;

    @Param(name = "comment", required = true)
    protected String comment;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {
        CommentManager cm = CommentServiceHelper.getCommentService().getCommentManager();
        DocumentModel commentDoc = cm.createComment(input, comment, ctx.getPrincipal().getName());
        return input;
    }

}
