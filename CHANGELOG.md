# Change Log

All notable changes to "Azure Toolkit for IntelliJ IDEA" will be documented in this file.

- [Change Log](#change-log)

  - [3.20.0](#3200)
  - [3.19.0](#3190)
  - [3.18.0](#3180)
  - [3.17.0](#3170)
  - [3.16.0](#3160)
  - [3.15.0](#3150)
  - [3.14.0](#3140)
  - [3.13.0](#3130)
  - [3.12.0](#3120)
  - [3.11.0](#3110)
  - [3.10.0](#3100)
  - [3.9.0](#390)
  - [3.8.0](#380)
  - [3.7.0](#370)
  - [3.6.0](#360)
  - [3.5.0](#350)
  - [3.4.0](#340)
  - [3.3.0](#330)
  - [3.2.0](#320)
  - [3.1.0](#310)
  - [3.0.12](#3012)
  - [3.0.11](#3011)
  - [3.0.10](#3010)
  - [3.0.9](#309)
  - [3.0.8](#308)
  - [3.0.7](#307)
  - [3.0.6](#306)

## 3.20.0

### Added

- Support Failure Task Local Reproduce for Spark 2.3 on Cosmos
- Support mock file system in Spark local console
- Support ADLS Gen2 storage type to submit job to HDInsight cluster
- Introduce extended properties field when provision a Spark on Cosmos cluster or submit a Spark on Cosmos Serverless job

### Changed

- Use device login as the default login method.
- Change icons for HDInsight cluster and related configuration

### Fixed

- [#2805](https://github.com/Microsoft/azure-tools-for-java/issues/2805) Save password with SecureStore.
- [#2888](https://github.com/Microsoft/azure-tools-for-java/issues/2888), [#2894](https://github.com/Microsoft/azure-tools-for-java/issues/2894), [#2921](https://github.com/Microsoft/azure-tools-for-java/issues/2921) Fix Spark on Cosmos Serverless job run failed related issues
- [#2912](https://github.com/Microsoft/azure-tools-for-java/issues/2912) Check invalid access key for submitting with ADLS Gen2 account
- [#2844](https://github.com/Microsoft/azure-tools-for-java/issues/2844) Refine WebHDFS and ADLS input path hints
- [#2848](https://github.com/Microsoft/azure-tools-for-java/issues/2848) Reset background color for not empty ADLS path input
- [#2749](https://github.com/Microsoft/azure-tools-for-java/issues/2749), [#2936](https://github.com/Microsoft/azure-tools-for-java/issues/2936) Fix Spark run configuration cast issues and classified exception message factory NPE issues

## 3.19.0

### Added

- Support open browser after Web App deployment.
- Support to link SQL Server Big Data cluster and submit Spark jobs.
- Support WebHDFS storage type to submit job to HDInsight cluster with ADLS Gen 1 storage account.

### Changed

- Update UI of Web App creation and deployment
- Subscription ID need to be specified for ADLS Gen 1 storage type

### Fixed

- [#2840](https://github.com/Microsoft/azure-tools-for-java/issues/2840) Submit successfully with invalid path for WebHDFS storage type issue.
- [#2747](https://github.com/Microsoft/azure-tools-for-java/issues/2747),[#2801](https://github.com/Microsoft/azure-tools-for-java/issues/2801) Error loadig HDInsight node issue.
- [#2714](https://github.com/Microsoft/azure-tools-for-java/issues/2714),[#2688](https://github.com/Microsoft/azure-tools-for-java/issues/2688),[#2669](https://github.com/Microsoft/azure-tools-for-java/issues/2669),[#2728](https://github.com/Microsoft/azure-tools-for-java/issues/2728),[#2807](https://github.com/Microsoft/azure-tools-for-java/issues/2807),[#2808](https://github.com/Microsoft/azure-tools-for-java/issues/2808),[#2811](https://github.com/Microsoft/azure-tools-for-java/issues/2811),[#2831](https://github.com/Microsoft/azure-tools-for-java/issues/2831)Spark Run Configuration validation issues.
- [#2810](https://github.com/Microsoft/azure-tools-for-java/issues/2810),[#2760](https://github.com/Microsoft/azure-tools-for-java/issues/2760) Spark Run Configuration issues when created from context menu.

## 3.18.0

### Added

- Supports Cosmos Serverless Spark submission and jobs list.
- Accepts SSL certificates automatically if the bypass option is enabled.

### Changed

- Wording of HDInsight and Spark UX.
- Enhanced Spark Run Configuration validation.

### Fixed

- [#2368](https://github.com/Microsoft/azure-tools-for-java/issues/2368) Device login will write useless error log.
- [#2675](https://github.com/Microsoft/azure-tools-for-java/issues/2675) Error message pops up when refresh HDInsight.

## 3.17.0

### Added

- The menu option for default Spark type to create Run Configuration.
- The menu option for bypassing SSL certificate validation for Spark Cluster.
- The progress bar for Spark cluster refreshing.
- The progress bar for Spark interactive consoles.

### Changed

- SQL Big Data Cluster node of Azure Explorer is changed into a first level root node.
- Link a SQL Big Data Cluster UI is aligned with Azure Data Studio UX.
- Spark for ADL job submission pops up Spark master UI page at the end.

### Fixed

- [#2307](https://github.com/Microsoft/azure-tools-for-java/issues/2307) Spark Run Configuration storage info for artifacts deployment issues
- [#2267](https://github.com/Microsoft/azure-tools-for-java/issues/2267) Spark Run Configuration remote run/debug actions overwrite non-spark codes Line Mark actions issue
- [#2500](https://github.com/Microsoft/azure-tools-for-java/issues/2500),[#2492](https://github.com/Microsoft/azure-tools-for-java/issues/2492),[#2451](https://github.com/Microsoft/azure-tools-for-java/issues/2451),[#2254](https://github.com/Microsoft/azure-tools-for-java/issues/2254) SQL Big Data Cluster link issues
- [#2485](https://github.com/Microsoft/azure-tools-for-java/issues/2485),[#2484](https://github.com/Microsoft/azure-tools-for-java/issues/2484),[#2483](https://github.com/Microsoft/azure-tools-for-java/issues/2483),[#2481](https://github.com/Microsoft/azure-tools-for-java/issues/2481),[#2427](https://github.com/Microsoft/azure-tools-for-java/issues/2427),[#2423](https://github.com/Microsoft/azure-tools-for-java/issues/2423),[#2417](https://github.com/Microsoft/azure-tools-for-java/issues/2417),[#2462](https://github.com/Microsoft/azure-tools-for-java/issues/2462) Spark Run Configuration validation issues
- [#2418](https://github.com/Microsoft/azure-tools-for-java/issues/2418) Spark for ADL provision UX issues
- [#2392](https://github.com/Microsoft/azure-tools-for-java/issues/2392) Azure Explorer HDInsight Spark cluster refreshing errors
- [#2488](https://github.com/Microsoft/azure-tools-for-java/issues/2488) Spark remote debugging SSH password saving regression

## 3.16.0

### Added

- Support both dedicated Azure explorer node and run configuration for Aris linked clusters.
- Support Spark local run classpath modules selection.

### Changed

- Use P1V2 as the default pricing tier for App Service.
- Spark run configuration validate checking is moved from before saving to before running.

### Fixed

- [#2468](https://github.com/Microsoft/azure-tools-for-java/issues/2468) Spark Livy interactive console regression of IDEA183 win process
- [#2424](https://github.com/Microsoft/azure-tools-for-java/issues/2424) Spark Livy interactive console blocking UI issue
- [#2318](https://github.com/Microsoft/azure-tools-for-java/issues/2318), [#2283](https://github.com/Microsoft/azure-tools-for-java/issues/2283) Cosmos Spark provision dialog AU warning issue
- [#2420](https://github.com/Microsoft/azure-tools-for-java/issues/2420) Spark cluster name duplicated issue in the run configuration
- [#2478](https://github.com/Microsoft/azure-tools-for-java/pull/2478) Cosmos Spark submit action can't find the right run configuration issue
- [#2419](https://github.com/Microsoft/azure-tools-for-java/issues/2419) The user can submit Spark job to unstable Cosmos Spark cluster issue
- [#2484](https://github.com/Microsoft/azure-tools-for-java/issues/2484), [#2316](https://github.com/Microsoft/azure-tools-for-java/issues/2316) The uploading storage config issues of Spark run configuration*
- [#2341](https://github.com/Microsoft/azure-tools-for-java/issues/2341) Authentication regression of `InvalidAuthenticationTokenAudience`

## 3.15.0

### Added

- Support new runtime WildFly 14 for Web App on Linux.
- Support to connect Spark Cosmos resource pool with Spark Interactive Console.
- Support to deploy Spark Application JAR artifacts by WebHDFS service (only support Basic authentication method).

### Fixed

- [#2381](https://github.com/Microsoft/azure-tools-for-java/issues/2381) Spark local interactive console jline dependence auto-fix dialog always popped up issue.
- [#2326](https://github.com/Microsoft/azure-tools-for-java/issues/2326) The Spark Run Configuration dialog always popped up issue for correct config.
- [#2116](https://github.com/Microsoft/azure-tools-for-java/issues/2116) [#2345](https://github.com/Microsoft/azure-tools-for-java/issues/2345) [#2339](https://github.com/Microsoft/azure-tools-for-java/issues/2339) User feedback issues.

## 3.14.0

### Added

- Support to show application settings of Deployment Slot.
- Support to delete a Deployment Slot in Azure Explorer.
- Support to config ADLS Gen1 Storage settings for Spark Run Configuration (only for HDInsight ADLS Gen 1 clusters and the interactive sign in mode).
- Support to auto fix Spark local REPL console related dependency.
- Support to classify Spark remotely application running error and provide more clear error messages.
- Support to start a Spark local console without a run configuration.

### Changed

- Change the Deployment Slot area in "Run on Web App" to be hideable.
- Use Azul Zulu JDK in Dockerfile of Web App for Containers.
- Spark linked cluster storage blob access key is saved to the secure store.

### Fixed

- [#2215](https://github.com/Microsoft/azure-tools-for-java/issues/2215) The prompt warning message on deleting web app is not correct issue.
- [#2310](https://github.com/Microsoft/azure-tools-for-java/issues/2310) Discarding of changes on Web App application settings is too slow issue.
- [#2286](https://github.com/Microsoft/azure-tools-for-java/issues/2286) [#2285](https://github.com/Microsoft/azure-tools-for-java/issues/2285) [#2120](https://github.com/Microsoft/azure-tools-for-java/issues/2120) [#2119](https://github.com/Microsoft/azure-tools-for-java/issues/2119) [#2117](https://github.com/Microsoft/azure-tools-for-java/issues/2117) Spark Console related issues.
- [#2203](https://github.com/Microsoft/azure-tools-for-java/issues/2203) Spark Remote Debug SSH password wasn't saved issue.
- [#2288](https://github.com/Microsoft/azure-tools-for-java/issues/2288) [#2287](https://github.com/Microsoft/azure-tools-for-java/issues/2287) HDInsight related icons size issue.
- [#2296](https://github.com/Microsoft/azure-tools-for-java/issues/2296) UI hang issue caused by Spark storage information validation.
- [#2295](https://github.com/Microsoft/azure-tools-for-java/issues/2295) [#2314](https://github.com/Microsoft/azure-tools-for-java/issues/2314) Spark Resource Pool issues.
- [#2303](https://github.com/Microsoft/azure-tools-for-java/issues/2303) [#2272](https://github.com/Microsoft/azure-tools-for-java/issues/2272) [#2200](https://github.com/Microsoft/azure-tools-for-java/issues/2200) [#2198](https://github.com/Microsoft/azure-tools-for-java/issues/2198) [#2161](https://github.com/Microsoft/azure-tools-for-java/issues/2161) [#2151](https://github.com/Microsoft/azure-tools-for-java/issues/2151) [#2109](https://github.com/Microsoft/azure-tools-for-java/issues/2109) [#2087](https://github.com/Microsoft/azure-tools-for-java/issues/2087) [#2058](https://github.com/Microsoft/azure-tools-for-java/issues/2058) Spark Job submission issues.
- [#2158](https://github.com/Microsoft/azure-tools-for-java/issues/2158) [#2085](https://github.com/Microsoft/azure-tools-for-java/issues/2085) HDInsight 4.0 regression issues.

## 3.13.0

### Added

- Support to deploy an application to Deployment Slot.
- Support to show and operate Deployment Slots of a Web App in Azure Explorer.
- Support to link an independent Livy server for Spark cluster.
- Add Spark Local interactive console.
- Add Spark HDInsight cluster interactive console (Only for 2018.2, Scala plugin is needed).

### Changed

- Change the Spark Job context menu submission dialog, to unify with IntelliJ Run Configuration Setting dialog.
- Move the storage information of HDInsight/Livy cluster to linked into Run Configuration settings.

### Fixed

- [#2143](https://github.com/Microsoft/azure-tools-for-java/issues/2143) The element "filter-mapping" is not removed when disabling telemetry with Application Insights.

## 3.12.0

### Added

- Support to deploy applications to Web App (Linux).
- Support to show the Azure Data Lake Spark resource pool provision log outputs.

### Changed

- List Web Apps on both Windows and Linux in Azure Explorer.
- List all app service plans of the selected subscription when creating a new Web App.
- Always upload the web.config file together with the .jar artifact when deploying to Web App (Windows).

### Fixed

- [#1968](https://github.com/Microsoft/azure-tools-for-java/issues/1968) Runtime information is not clear enough for Azure Web Apps
- [#1779](https://github.com/Microsoft/azure-tools-for-java/issues/1779) [#1920](https://github.com/Microsoft/azure-tools-for-java/issues/1920) The issue of Azure Data Lake Spark resource pool `Update` dialog pop up multi times.

## 3.11.0

- Added the main class hint when users choose to submit a Spark job using a local artifact file.
- Added Spark cluster GUID for Spark cluster provision failure investigation.
- Added the "AU not enough" warning message in Azure Data Lake Spark resource pool provision.
- Added the job queue query to check AU consumption in Azure Data Lake Spark resource pool provision.
- Fixed cluster total AU by using systemMaxAU instead of maxAU.
- Refresh node automatically when node is clicked in Azure explorer.
- Updated the Azure SDK to 1.14.0.
- Fixed some bugs.

## 3.10.0

- Supported to fix Spark job configuration in run configuration before Spark job submission.
- Updated Application Insights library to v2.1.2.
- Fixed some bugs.

## 3.9.0

- Added Spark 2.3 support.
- Spark in Azure Data Lake private preview refresh and bug fix.
- Fixed some bugs.

## 3.8.0

- Supported to run Spark jobs in Azure Data Lake cluster (in private preview).
- Fixed some bugs.

## 3.7.0

- Users do not need to login again in interactive login mode, if Azure refresh token is still validated.
- Updated ApplicationInsights version to v2.1.0.
- Fixed some bugs.

## 3.6.0

- Updated ApplicationInsights version to v2.0.2.
- Added Spark 2.2 templates for HDInsight.
- Added SSH password expiration check.
- Fixed some bugs.

## 3.5.0

- Added open Azure Storage Explorer for exploring data in HDInsight cluster (blob or ADLS).
- Improved Spark remote debugging.
- Improved Spark job submission correctness check.
- Fixed an login issue.

## 3.4.0

- Users can use Ambari username/password to submit Spark job to HDInsight cluster, in additional to Azure subscription based authentication. This means users without Azure subscription permission can still use Ambari credentials to submit/debug their Spark jobs in HDInsight clusters.
- The dependency on storage permission is removed and users do not need to provide storage credentials for Spark job submission any more (storage credential is still needed if users want to use storage explorer).

## 3.3.0

- Added support of Enterprise Security Package HDInsight Spark cluster.
- Support submitting Spark jobs using Ambari username/password instead of the Azure subscription credential.
- Updated ApplicationInsights version to v1.0.10.
- Fixed some bugs.

## 3.2.0

- Fixed Spark job submission issue when user right click Spark project and submit Spark job in project explorer.
- Fixed HDInsight wasbs access bug when SSL encrypted access is used.
- Added JxBrowser support for new Spark job UI.
- Fixed winutils.exe not setup issue and updated error message.

## 3.1.0

- Fixed compatibility issue with IntelliJ IDEA 2017.3.
- HDInsight tools UI refactoring: Added toolbar entry and right click context menu entry for Spark job submission and local/in-cluster debugging, which make users submit or debug job easier.
- Fixed some bugs.

## 3.0.12

- Support submitting the script to HDInsight cluster without modification in Spark local run.
- Fixed some bugs.

## 3.0.11

- Support view/edit properties of Azure Web App (Windows/Linux).
- Support interactive login mode for Azure China.
- Support running docker locally for multiple modules in current project (simultaneously).
- Users can now use the same code for both Spark local run and cluster run, which means they can test locally and then submit to cluster without modification.
- HDInsight tools for IntelliJ now generate run/debug configuration automatically to make Spark job run/debug easier for both local and cluster run.
- Fixed some bugs.

## 3.0.10

- Support pushing docker image of the project to Azure Container Registry.
- Support navigating Azure Container Registry in Azure Explorer.
- Support pulling image from Azure Container Registry in Azure Explorer.
- Fixed some bugs.

## 3.0.9

- Fixed "Unexpected token" error when using Run on Web App (Linux). ([#1014](https://github.com/Microsoft/azure-tools-for-java/issues/1014))

## 3.0.8

- Support Spring Boot Project: The Azure Toolkits for IntelliJ now support running your Spring Boot Project (Jar package) on Azure Web App and Azure Web App (Linux).
- Docker Run Locally: You can now docker run your projects locally after adding docker support.
- New Node in Azure Explorer: You can now view the property of your resources in Azure Container Registry.
- Added validation for Spark remote debug SSH authentication.
- Fixed some bugs.

## 3.0.7

- Support Community Edition: The Azure Toolkit for IntelliJ now supports deploying your Maven projects to Azure App Service from IntelliJ IDEA, both Community and Ultimate Edition.
- Improved Web App Workflow: You can now run your web applications on Azure Web App with One-Click experience using Azure Toolkit for IntelliJ.
- New Container Workflow: You can now dockerize and run your web application on Azure Web App (Linux) via Azure Container Registry.
- Spark remote debugging in IntelliJ now support debugging of both driver and executor code depending on where the breakpoint is set.
- Fixed some bugs.

## 3.0.6

- Added the Redis Cache Explorer that allows users to scan/get keys and their values.
- Improved Spark job remote debugging support(show log in console, apply and load debugging config).
- Fixed some bugs.
