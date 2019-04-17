/*
 * Copyright (c) Microsoft Corporation
 *   <p/>
 *  All rights reserved.
 *   <p/>
 *  MIT License
 *   <p/>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 *  to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *  <p/>
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 *  the Software.
 *   <p/>
 *  THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 *  THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.microsoft.azuretools.utils;


import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.DeploymentSlot;
import com.microsoft.azure.management.appservice.JavaVersion;
import com.microsoft.azure.management.appservice.OperatingSystem;
import com.microsoft.azure.management.appservice.PublishingProfile;
import com.microsoft.azure.management.appservice.RuntimeStack;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.appservice.WebAppBase;
import com.microsoft.azure.management.appservice.WebContainer;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azuretools.Constants;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.authmanage.models.SubscriptionDetail;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import com.microsoft.azuretools.sdkmanage.AzureManager;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class WebAppUtils {

    public static final String TYPE_WAR = "war";
    public static final String TYPE_JAR = "jar";

    private static final String ftpRootPath = "/site/wwwroot/";
    private static final String ftpWebAppsPath = ftpRootPath + "webapps/";
    private static String webConfigFilename = "web.config";
    private static final String NO_TARGET_FILE = "Cannot find target file: %s.";
    private static final String ROOT = "ROOT";
    private static final String JAVASE_ROOT= "app";
    private static final int FTP_MAX_TRY = 3;
    private static final int SLEEP_TIME = 5000; // milliseconds
    private static final String DEFAULT_VALUE_WHEN_VERSION_INVALID = "";

    @NotNull
    public static FTPClient getFtpConnection(PublishingProfile pp) throws IOException {

        FTPClient ftp = new FTPClient();

        System.out.println("\t\t" + pp.ftpUrl());
        System.out.println("\t\t" + pp.ftpUsername());
        System.out.println("\t\t" + pp.ftpPassword());

        URI uri = URI.create("ftp://" + pp.ftpUrl());
        ftp.connect(uri.getHost(), 21);
        final int replyCode = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftp.disconnect();
            throw new ConnectException("Unable to connect to FTP server");
        }

        if (!ftp.login(pp.ftpUsername(), pp.ftpPassword())) {
            throw new ConnectException("Unable to login to FTP server");
        }

        ftp.setControlKeepAliveTimeout(Constants.connection_read_timeout_ms);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();//Switch to passive mode

        return ftp;
    }

    public static int deployArtifact(String artifactName, String artifactPath, PublishingProfile pp, boolean toRoot, IProgressIndicator indicator) throws IOException {
        File file = new File(artifactPath);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format(NO_TARGET_FILE, artifactPath));
        }
        FTPClient ftp = null;
        InputStream input = null;
        int uploadingTryCount = 0;
        try {
            if (indicator != null) indicator.setText("Connecting to FTP server...");

            ftp = getFtpConnection(pp);
            ensureWebAppsFolderExist(ftp);
            if (indicator != null) indicator.setText("Uploading the application...");
            input = new FileInputStream(artifactPath);
            int indexOfDot = artifactPath.lastIndexOf(".");
            String fileType = artifactPath.substring(indexOfDot + 1);

            switch (fileType) {
                case TYPE_WAR:
                    if (toRoot) {
                        WebAppUtils.removeFtpDirectory(ftp, ftpWebAppsPath + ROOT, indicator);
                        ftp.deleteFile(ftpWebAppsPath + ROOT + "." + TYPE_WAR);
                        uploadingTryCount = uploadFileToFtp(ftp, ftpWebAppsPath + ROOT + "." + TYPE_WAR, input, indicator);
                    } else {
                        WebAppUtils.removeFtpDirectory(ftp, ftpWebAppsPath + artifactName, indicator);
                        ftp.deleteFile(artifactName + "." + TYPE_WAR);
                        uploadingTryCount = uploadFileToFtp(ftp, ftpWebAppsPath + artifactName + "." + TYPE_WAR, input, indicator);
                    }
                    break;
                case TYPE_JAR:
                    uploadingTryCount = uploadFileToFtp(ftp, ftpRootPath + ROOT + "." + TYPE_JAR, input, indicator);
                    break;
                default:
                    break;
            }
            if (indicator != null) indicator.setText("Logging out of FTP server...");
            ftp.logout();
        } finally {
            if (input != null)
                input.close();
            if (ftp != null && ftp.isConnected()) {
                ftp.disconnect();
            }
        }
        return uploadingTryCount;
    }

    public static int deployArtifactForJavaSE(String artifactPath, PublishingProfile pp, IProgressIndicator indicator) throws IOException {
        File file = new File(artifactPath);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format(NO_TARGET_FILE, artifactPath));
        }
        FTPClient ftp = null;
        int uploadingTryCount;
        try (InputStream input = new FileInputStream(artifactPath)){
            if (indicator != null) {
                indicator.setText("Connecting to FTP server...");
            }
            ftp = getFtpConnection(pp);
            if (indicator != null) {
                indicator.setText("Uploading the application...");
            }
            uploadingTryCount = uploadFileToFtp(ftp, ftpRootPath + JAVASE_ROOT + "." + TYPE_JAR, input, indicator);
            if (indicator != null) {
                indicator.setText("Logging out of FTP server...");
            }
            ftp.logout();
        } finally {
            if (ftp != null && ftp.isConnected()) {
                ftp.disconnect();
            }
        }
        return uploadingTryCount;
    }

    private static void ensureWebAppsFolderExist(FTPClient ftp) throws IOException {
        int count = 0;
        while (count++ < FTP_MAX_TRY) {
            try {
                ftp.getStatus(ftpWebAppsPath);
                if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    return;
                }
                if (ftp.makeDirectory(ftpWebAppsPath)){
                    return;
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignore) {}
            } catch (Exception e) {
                if (count == FTP_MAX_TRY) {
                    throw new IOException("FTP client can't make directory, reply code: " + ftp.getReplyCode(), e);
                }
            }
        }

        throw new IOException("FTP client can't make directory, reply code: " + ftp.getReplyCode());
    }

    public static void removeFtpDirectory(FTPClient ftpClient, String path, IProgressIndicator pi) throws IOException {
        String prefix = "Removing from FTP server: ";
        FTPFile[] subFiles = ftpClient.listFiles(path);
        if (subFiles.length > 0) {
            for (FTPFile ftpFile : subFiles) {
                if (pi != null && pi.isCanceled()) break;
                String currentFileName = ftpFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    continue; // skip
                }

                String path1 = path + "/" + currentFileName;
                if (ftpFile.isDirectory()) {
                    // remove the sub directory
                    removeFtpDirectory(ftpClient, path1, pi);
                } else {
                    // delete the file
                    if (pi != null) pi.setText2(prefix + path1);
                    ftpClient.deleteFile(path1);
                }
            }
        }

        if (pi != null) pi.setText2(prefix + path);
        ftpClient.removeDirectory(path);
        if (pi != null) pi.setText2("");
    }

    public static boolean doesRemoteFileExist(FTPClient ftp, String path, String fileName) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        for (FTPFile file : files) {
            if (file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesRemoteFolderExist(FTPClient ftp, String path, String folderName) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        for (FTPFile file : files) {
            if (file.isDirectory() && file.getName().equalsIgnoreCase(folderName)) {
                return true;
            }
        }
        return false;
    }

    public static int sendGet(String sitePath) throws IOException {
        URL url = new URL(sitePath);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setReadTimeout(Constants.connection_read_timeout_ms);
        return con.getResponseCode();
        //con.setRequestProperty("User-Agent", "AzureTools for Intellij");
    }

    public static boolean isUrlAccessible(String url) throws IOException {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("HEAD");
        con.setReadTimeout(Constants.connection_read_timeout_ms);
        try {
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
        } catch (IOException ex) {
            return false;
        } finally {
            con.disconnect();
        }
        return true;
    }

    public static class WebAppException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1352713295336034845L;

        WebAppException(String message) {
            super(message);
        }
    }

    public enum WebContainerMod {
        Newest_Tomcat_85("Newest Tomcat 8.5", "tomcat 8.5"),
        Newest_Tomcat_80("Newest Tomcat 8.0", "tomcat 8.0"),
        Newest_Tomcat_70("Newest Tomcat 7.0", "tomcat 7.0"),
        Newest_Jetty_93("Newest Jetty 9.3", "jetty 9.3"),
        Newest_Jetty_91("Newest Jetty 9.1", "jetty 9.1");

        private String displayName;
        private String value;

        WebContainerMod(String displayName, String value ) {
            this.displayName = displayName;
            this.value = value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getValue() {
            return value;
        }

        public WebContainer toWebContainer() {
            return WebContainer.fromString(getValue());
        }

        @Override
        public String toString() {
            return getDisplayName();
        }

    }

    public static String generateWebContainerPath(WebContainer webContainer) throws IOException {
        if (webContainer.toString().equals(WebContainerMod.Newest_Tomcat_70.getValue())) {
            return "%AZURE_TOMCAT7_HOME%";
        } else if (webContainer.toString().equals(WebContainerMod.Newest_Tomcat_80.getValue())) {
            return "%AZURE_TOMCAT8_HOME%";
        } else if (webContainer.toString().equals(WebContainerMod.Newest_Tomcat_85.getValue())) {
            return "%AZURE_TOMCAT85_HOME%";
        } else if (webContainer.toString().equals(WebContainerMod.Newest_Jetty_91.getValue())) {
            return "%AZURE_JETTY9_HOME%";
        } else if (webContainer.toString().equals(WebContainerMod.Newest_Jetty_93.getValue())) {
            return "%AZURE_JETTY93_HOME%";
        }

        throw new IOException("Unknown web container: " + webContainer.toString());
    }

    public static void deleteAppService(WebAppDetails webAppDetails) throws IOException {
        AzureManager azureManager = AuthMethodManager.getInstance().getAzureManager();
        Azure azure = azureManager.getAzure(webAppDetails.subscriptionDetail.getSubscriptionId());
        azure.webApps().deleteById(webAppDetails.webApp.id());
        // check asp still exists
        AppServicePlan asp = azure.appServices().appServicePlans().getById(webAppDetails.appServicePlan.id());
        System.out.println("asp is " + (asp == null ? "null -> removing form cache" : asp.name()));
        // update cache
        AzureModelController.removeWebAppFromResourceGroup(webAppDetails.resourceGroup, webAppDetails.webApp);
        if (asp == null) {
            AzureModelController.removeAppServicePlanFromResourceGroup(webAppDetails.appServicePlanResourceGroup, webAppDetails.appServicePlan);
        }
    }

    public static void uploadWebConfig(WebApp webApp, InputStream fileStream, IProgressIndicator indicator) throws IOException {
        FTPClient ftp = null;
        try {
            if(indicator != null) indicator.setText("Stopping the service...");
            webApp.stop();

            PublishingProfile pp = webApp.getPublishingProfile();
            ftp = getFtpConnection(pp);

            if(indicator != null) indicator.setText("Uploading " + webConfigFilename + "...");
            uploadFileToFtp(ftp, ftpRootPath + webConfigFilename, fileStream, indicator);

            if(indicator != null) indicator.setText("Starting the service...");
            webApp.start();
        } finally {
            if (ftp != null && ftp.isConnected()) {
                ftp.disconnect();
            }
        }
    }

    public static int uploadToRemoteServer(WebAppBase webApp, String fileName, InputStream ins,
        IProgressIndicator indicator, String targetPath) throws IOException {
        FTPClient ftp = null;
        try {
            PublishingProfile pp = webApp.getPublishingProfile();
            ftp = getFtpConnection(pp);
            if (indicator != null) {
                indicator.setText(String.format("Uploading %s ...", fileName));
            }
            return uploadFileToFtp(ftp, targetPath, ins, indicator);
        } finally {
            if (ftp != null && ftp.isConnected()) {
                ftp.disconnect();
            }
        }
    }

    private static int uploadFileToFtp(FTPClient ftp, String path, InputStream stream, IProgressIndicator indicator) throws IOException {
        boolean success;
        int count = 0;
        int rc = 0;
        while (count++ < FTP_MAX_TRY) {
            try {
                success = ftp.storeFile(path, stream);
                if (success) {
                    if (indicator != null) {
                        indicator.setText("Uploading successfully...");
                    }
                    return count;
                }
                rc = ftp.getReplyCode();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignore) {}
            } catch (Exception e) {
                if (count == FTP_MAX_TRY) {
                    throw new IOException("FTP client can't store the artifact, reply code: " + rc, e);
                }
            }
        }
        throw new IOException("FTP client can't store the artifact, reply code: " + rc);
    }

    public static class WebAppDetails {
        public SubscriptionDetail subscriptionDetail;
        public ResourceGroup resourceGroup;
        public AppServicePlan appServicePlan;
        public ResourceGroup appServicePlanResourceGroup;
        public WebApp webApp;

        public WebAppDetails() {}

        public WebAppDetails(ResourceGroup resourceGroup, WebApp webApp,
                             AppServicePlan appServicePlan, ResourceGroup appServicePlanResourceGroup,
                             SubscriptionDetail subscriptionDetail) {
            this.resourceGroup = resourceGroup;
            this.webApp = webApp;
            this.appServicePlan = appServicePlan;
            this.appServicePlanResourceGroup = appServicePlanResourceGroup;
            this.subscriptionDetail = subscriptionDetail;
        }
    }

    public static class AspDetails {
        private AppServicePlan asp;
        private ResourceGroup rg;
        public AspDetails(AppServicePlan asp, ResourceGroup rg) {
            this.asp = asp;
            this.rg = rg;
        }
        public AppServicePlan getAsp() {
            return asp;
        }
        public ResourceGroup getRg() {
            return rg;
        }
    }

    /**
     * Check if the web app is a Windows or Linux Java configured web app.
     * Docker web apps are not included.
     */
    public static boolean isJavaWebApp(@NotNull WebApp webApp) {
        return (webApp.operatingSystem() == OperatingSystem.WINDOWS && webApp.javaVersion() != JavaVersion.OFF)
         || (webApp.operatingSystem() == OperatingSystem.LINUX && StringUtils.containsIgnoreCase(webApp.linuxFxVersion(), "jre8"));
    }

    /**
     * For a Windows web app, APIs are separated to get jdk information and web container information.
     * For a Linux web app, API app.linuxFxVersion() returns a combined information, like
     * "Tomcat|8.5-jre8" if it is a Linux web app with the web container Tomcat.
     * We return a combined and refactored information as Java Runtime.
     */
    public static String getJavaRuntime(@NotNull final WebApp webApp) {
        String webContainer;
        switch (webApp.operatingSystem()) {
            case WINDOWS:
                webContainer = webApp.javaContainer() == null ? null : webApp.javaContainer().toLowerCase();
                return String.format("%s %s (Java%s)",
                    StringUtils.capitalize(webContainer), webApp.javaContainerVersion(), webApp.javaVersion().toString());
            case LINUX:
                final String linuxVersion = webApp.linuxFxVersion();
                if (linuxVersion == null) {
                    return DEFAULT_VALUE_WHEN_VERSION_INVALID;
                }

                final String[] versions = linuxVersion.split("\\||-");
                if (versions == null && versions.length != 3) {
                    return linuxVersion;
                }

                webContainer = versions[0].toLowerCase();
                final String webContainerVersion = versions[1];
                final String jreVersion = versions[2];
                final boolean isJavaLinuxRuntimeWithWebContainer = getAllJavaLinuxRuntimeStacks()
                    .stream()
                    .map(r -> r.stack())
                    .filter(w -> !w.equalsIgnoreCase("java"))
                    .anyMatch(w -> w.equalsIgnoreCase(webContainer));
                if (isJavaLinuxRuntimeWithWebContainer) {
                    // TOMCAT|8.5-jre8 -> Tomcat 8.5 (JRE8)
                    return String.format("%s %s (%s)", StringUtils.capitalize(webContainer), webContainerVersion, jreVersion.toUpperCase());
                } else {
                    // JAVA|8-jre8 -> JRE8
                    return jreVersion.toUpperCase();
                }
            default:
                return DEFAULT_VALUE_WHEN_VERSION_INVALID;
        }
    }

    public static List<DeploymentSlot> getDeployments(WebApp webApp) {
        if (webApp == null || webApp.deploymentSlots() == null || webApp.deploymentSlots().list() == null) {
            return new ArrayList<>();
        }
        List<DeploymentSlot> result = new ArrayList<>();
        for (DeploymentSlot deploymentSlot : webApp.deploymentSlots().list()) {
            result.add(deploymentSlot);
        }
        return result;
    }

    public static List<RuntimeStack> getAllJavaLinuxRuntimeStacks() {
        return Arrays.asList(new RuntimeStack[]{
            RuntimeStack.TOMCAT_8_5_JRE8,
            RuntimeStack.TOMCAT_9_0_JRE8,
            RuntimeStack.WILDFLY_14_JRE8,
            RuntimeStack.JAVA_8_JRE8});
    }

    public static String getFileType(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        String fileType = "";
        int index = fileName.lastIndexOf(".");
        if (index >= 0 && (index + 1) < fileName.length()) {
            fileType = fileName.substring(index + 1);
        }
        return fileType;
    }
}
