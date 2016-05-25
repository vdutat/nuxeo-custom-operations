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
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.core.work.api.Work;

/**
 *
 */
@Operation(id=GetWorkInfo.ID, category=Constants.CAT_SERVICES, label="GetWorkInfo", description="")
public class GetWorkInfo {

    public static final String ID = "Services.GetWorkInfo";

    private static final Log LOGGER = LogFactory.getLog(GetWorkInfo.class);

    @Context
    protected OperationContext ctx;

    @Context
    protected WorkManager wm;

    @Param(name = "workId", required = true)
    protected String workId;

    @OperationMethod
    public Object run() {
        Work work = wm.find(workId, null);
        if (work == null) {
            return "Work [" + workId + "] not found.";
        }
        return work.toString();
    }

}
