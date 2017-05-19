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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * https://jira.nuxeo.com/browse/SUPNXP-19899
 *
 * @author vdutat
 */
@Operation(id=GetService.ID, category=Constants.CAT_SERVICES, label="Services.GetService", description="Describe here what your operation does.")
public class GetService {

    public static final String ID = "Services.GetService";

    private Log log = LogFactory.getLog(GetService.class);

    @Context
    protected CoreSession session;

    @Param(name = "class", required = true)
    protected String className;

    @OperationMethod
    public Object run() {
        try {
            return Framework.getService(Class.forName(className));
        } catch (ClassNotFoundException e) {
            log.error(e, e);
        }
        return null;
    }
}
