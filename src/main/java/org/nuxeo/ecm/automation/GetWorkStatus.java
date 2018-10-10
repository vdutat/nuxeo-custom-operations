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
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.ecm.core.work.api.Work.State;
import org.nuxeo.ecm.core.work.api.WorkManager;

/**
 *
 */
@Operation(id=GetWorkStatus.ID, category=Constants.CAT_SERVICES, label="GetWorkStatus", description="")
public class GetWorkStatus {

    public static final String ID = "Services.GetWorkStatus";

    private static final Log LOGGER = LogFactory.getLog(GetWorkStatus.class);

    @Context
    protected OperationContext ctx;

    @Context
    protected WorkManager wm;

    @Param(name = "workId", required = true)
    protected String workId;

    @OperationMethod
    public Object run() {
        Work work = wm.find(workId, null);
        return work.getStatus();
    }

}
