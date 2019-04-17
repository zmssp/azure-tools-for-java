/*
 * Copyright (c) Microsoft Corporation
 *
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.microsoft.azuretools.telemetry;

public class TelemetryConstants {

    // production name
    public static final String WEBAPP = "webapp";
    public static final String ACR = "acr";
    public static final String DOCKER = "docker";

    // operation value
    public static final String CREATE_WEBAPP = "create-webapp";
    public static final String DELETE_WEBAPP = "delete-webapp";
    public static final String DEPLOY_WEBAPP = "deploy-webapp";
    public static final String DEPLOY_WEBAPP_SLOT = "deploy-webapp-slot";
    public static final String CREATE_WEBAPP_SLOT = "create-webapp-slot";
    public static final String OPEN_CREATEWEBAPP_DIALOG = "open-create-webapp-dialog";
    public static final String REFRESH_METADATA = "refresh";

    // property name
    public static final String WEBAPP_DEPLOY_TO_SLOT = "webappDeployToSlot";
    public static final String RUNTIME = "runtime";
    public static final String CREATE_NEWASP = "createNewAsp";
    public static final String CREATE_NEWWEBAPP = "createNewWebapp";
    public static final String CREATE_NEWRG = "createNewRg";
    public static final String SUBSCRIPTIONID = "subscriptionId";
    public static final String FILETYPE = "fileType";
    public static final String ARTIFACT_UPLOAD_COUNT = "artifactUploadCount";
    public static final String JAVA_APPNAME = "javaAppName";
}
