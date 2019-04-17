/**
 * Copyright (c) Microsoft Corporation
 *
 *
 * All rights reserved.
 *
 *
 * MIT License
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.microsoft.azure.hdinsight.spark.ui

import com.intellij.ui.DocumentAdapter
import com.microsoft.azure.hdinsight.common.ClusterManagerEx
import com.microsoft.azure.hdinsight.common.logger.ILogger
import com.microsoft.azure.hdinsight.sdk.cluster.IClusterDetail
import com.microsoft.azure.hdinsight.sdk.common.AzureSparkClusterManager
import com.microsoft.azure.hdinsight.sdk.common.azure.serverless.AzureSparkCosmosCluster
import com.microsoft.azure.hdinsight.sdk.storage.*
import com.microsoft.azure.hdinsight.spark.common.SparkSubmitJobUploadStorageModel
import com.microsoft.azure.hdinsight.spark.common.SparkSubmitStorageType
import com.microsoft.azure.storage.blob.BlobRequestOptions
import com.microsoft.azuretools.authmanage.AuthMethodManager
import com.microsoft.tooling.msservices.helpers.azure.sdk.StorageClientSDKManager
import com.microsoft.tooling.msservices.model.storage.BlobContainer
import com.microsoft.tooling.msservices.model.storage.ClientStorageAccount
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import rx.Observable
import rx.schedulers.Schedulers
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.ItemEvent
import java.util.stream.Collectors
import javax.swing.DefaultComboBoxModel
import javax.swing.event.DocumentEvent

class SparkSubmissionJobUploadStorageCtrl(val view: SparkSubmissionJobUploadStorageWithUploadPathPanel) :
        SparkSubmissionJobUploadStorageWithUploadPathPanel.Control, ILogger {
    override val isCheckPassed
        get() = StringUtils.isEmpty(resultMessage)
    override val resultMessage
        get() = view.storagePanel.errorMessage

    //storage check event for storageCheckSubject in panel
    open class StorageCheckEvent(val message: String)
    class StorageCheckSelectedClusterEvent(val cluster: IClusterDetail,val preClusterName: String?) : StorageCheckEvent(
            "Selected cluster ${cluster.name} with preClusterName $preClusterName")
    class StorageCheckSignInOutEvent() : StorageCheckEvent("After user clicked sign in/off in ADLS Gen 1 storage type")
    class StorageCheckPathFocusLostEvent(val rootPathType: String) : StorageCheckEvent("$rootPathType root path focus lost")
    class StorageCheckSelectedStorageTypeEvent(val storageType: String) : StorageCheckEvent("Selected storage type: $storageType")

    init {
        // refresh containers after account and key focus lost
        arrayOf(view.storagePanel.azureBlobCard.storageAccountField, view.storagePanel.azureBlobCard.storageKeyField).forEach {
            // Each time user changed storage account or key, we set the containers to empty
            it.document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    view.storagePanel.azureBlobCard.storageContainerUI.comboBox.model = DefaultComboBoxModel()
                }
            })
            it.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent?) {
                    if (view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled) {
                        view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled = false
                        refreshContainers()
                            .doOnEach { view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled = true }
                            .subscribe(
                                { },
                                { err -> log().warn(ExceptionUtils.getStackTrace(err)) })
                    }
                }})
        }
        // refresh containers after refresh button is clicked
        view.storagePanel.azureBlobCard.storageContainerUI.button.addActionListener {
            if (view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled) {
                view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled = false
                refreshContainers()
                    .doOnEach { view.storagePanel.azureBlobCard.storageContainerUI.button.isEnabled = true }
                    .subscribe(
                        { },
                        { err -> log().warn(ExceptionUtils.getStackTrace(err)) })
            }
        }
        // after container is selected, update upload path
        view.storagePanel.azureBlobCard.storageContainerUI.comboBox.addItemListener { itemEvent ->
            if (itemEvent?.stateChange == ItemEvent.SELECTED) {
                updateStorageAfterContainerSelected().subscribe(
                        { },
                        { err -> log().warn(ExceptionUtils.getStackTrace(err)) })
            }
        }

        //refresh subscriptions after refresh button is clicked
        view.storagePanel.adlsCard.subscriptionsComboBox.button.addActionListener{
            if (view.storagePanel.adlsCard.subscriptionsComboBox.button.isEnabled) {
                view.storagePanel.adlsCard.subscriptionsComboBox.button.isEnabled = false
                refreshSubscriptions()
                        .doOnEach { view.storagePanel.adlsCard.subscriptionsComboBox.button.isEnabled = true }
                        .subscribe(
                                { },
                                { err -> log().warn(ExceptionUtils.getStackTrace(err)) })
            }
        }

        //save access key after access key text change
        view.storagePanel.adlsGen2Card.storageKeyField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                saveAccesKey().subscribe(
                        { model -> log().info("save new access key for account" + model.gen2Account) },
                        {}
                )
            }
        })
    }

    private fun saveAccesKey(): Observable<SparkSubmitJobUploadStorageModel> {
        return Observable.just(SparkSubmitJobUploadStorageModel())
                .doOnNext(view::getData)
                .map { model ->
                    model.apply {
                        if (!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(gen2Account)) {
                            val credentialAccount = getCredentialAccount(gen2Account, SparkSubmitStorageType.ADLS_GEN2)
                            credentialAccount?.let {
                                view.secureStore?.savePassword(credentialAccount, gen2Account, accessKey)
                            }
                        }
                    }
                }
                .doOnNext(view::setData)
    }

    private fun refreshSubscriptions(): Observable<SparkSubmitJobUploadStorageModel> {
        return Observable.just(SparkSubmitJobUploadStorageModel())
                .doOnNext(view::getData)
                // set error message to prevent user from applying the change when refreshing is not completed
                .map { it.apply { errorMsg = "refreshing subscriptions is not completed" } }
                .doOnNext(view::setData)
                .observeOn(Schedulers.io())
                .map { toUpdate ->
                    toUpdate.apply {
                        if (!AzureSparkClusterManager.getInstance().isSignedIn) {
                            errorMsg = "ADLS Gen 1 storage type requires user to sign in first"
                        } else {
                            try {
                                val subscriptionManager = AuthMethodManager.getInstance().azureManager.subscriptionManager
                                val subscriptionNameList = subscriptionManager.selectedSubscriptionDetails
                                        .stream()
                                        .map { subDetail -> subDetail.subscriptionName }
                                        .collect(Collectors.toList<String>())

                                if (subscriptionNameList.size > 0) {
                                    subscriptionsModel = DefaultComboBoxModel(subscriptionNameList.toTypedArray())
                                    subscriptionsModel.selectedItem = subscriptionsModel.getElementAt(0)
                                    selectedSubscription = subscriptionsModel.getElementAt(0)
                                    errorMsg = null
                                } else {
                                    errorMsg = "No subscriptions found in this storage account"
                                }
                            } catch (ex: Exception) {
                                log().info("Refresh subscriptions error. " + ExceptionUtils.getStackTrace(ex))
                                errorMsg = "Can't get subscriptions, check if subscriptions selected"
                            }
                        }
                    }
                }
                .doOnNext { data ->
                    if (data.errorMsg != null) {
                        log().info("Refresh subscriptions error: " + data.errorMsg)
                    }
                    view.setData(data)
                }
    }

    private fun refreshContainers(): Observable<SparkSubmitJobUploadStorageModel> {
        return Observable.just(SparkSubmitJobUploadStorageModel())
            .doOnNext(view::getData)
            // set error message to prevent user from applying the change when refreshing is not completed
            .map { it.apply { errorMsg = "refreshing storage containers is not completed" } }
            .doOnNext(view::setData)
            .observeOn(Schedulers.io())
            .map { toUpdate ->
                    toUpdate.apply {
                        if (StringUtils.isEmpty(toUpdate.storageAccount) || StringUtils.isEmpty(toUpdate.storageKey)) {
                            errorMsg = "Storage account and key can't be empty"
                        } else {
                            try {
                                val clientStorageAccount = ClientStorageAccount(toUpdate.storageAccount)
                                        .apply { primaryKey = toUpdate.storageKey }
                                val credentialAccount = getCredentialAccount(toUpdate.storageAccount,  SparkSubmitStorageType.BLOB)
                                credentialAccount?.let {
                                    view.secureStore?.savePassword(credentialAccount, storageAccount, storageKey) }

                                // Add Timeout for list containers operation to avoid getting stuck
                                // when storage account or key is invalid
                                val requestOptions = BlobRequestOptions().apply { maximumExecutionTimeInMs = 5000 }
                                val containers = StorageClientSDKManager
                                        .getManager()
                                        .getBlobContainers(clientStorageAccount.connectionString, requestOptions)
                                        .map(BlobContainer::getName)
                                        .toTypedArray()
                                if (containers.isNotEmpty()) {
                                    containersModel = DefaultComboBoxModel(containers)
                                    containersModel.selectedItem = containersModel.getElementAt(0)
                                    selectedContainer = containersModel.getElementAt(0)
                                    uploadPath = getAzureBlobStoragePath(ClusterManagerEx.getInstance().getBlobFullName(storageAccount), selectedContainer, HDStorageAccount.DefaultScheme)

                                errorMsg = null
                            } else {
                                errorMsg = "No container found in this storage account"
                            }
                        } catch (ex: Exception) {
                            log().info("Refresh Azure Blob contains error. " + ExceptionUtils.getStackTrace(ex))
                            errorMsg = "Can't get storage containers, check if account and key matches"
                        }
                    }
                }
            }
            .doOnNext { data ->
                if (data.errorMsg != null) {
                    log().info("Refresh Azure Blob containers error: " + data.errorMsg)
                }
                view.setData(data)
            }
    }

    private fun updateStorageAfterContainerSelected(): Observable<SparkSubmitJobUploadStorageModel> {
        return Observable.just(SparkSubmitJobUploadStorageModel())
            .doOnNext(view::getData)
            // set error message to prevent user from applying the change when updating is not completed
            .map { it.apply { "updating upload path is not completed" } }
            .doOnNext(view::setData)
            .observeOn(Schedulers.io())
            .map { toUpdate ->
                if (toUpdate.containersModel.size == 0) {
                    toUpdate.apply { errorMsg = "Storage account has no containers" }
                } else {
                    toUpdate.apply {
                        val selectedContainer = toUpdate.containersModel.selectedItem as String
                        uploadPath = getAzureBlobStoragePath(ClusterManagerEx.getInstance().getBlobFullName(storageAccount), selectedContainer, HDStorageAccount.DefaultScheme)
                        errorMsg = null
                    }
                }
            }
            .doOnNext { data ->
                if (data.errorMsg != null) {
                    log().info("Update storage info after container selected error: " + data.errorMsg)
                }
                view.setData(data)
            }
    }

    override fun getAzureBlobStoragePath(fullStorageBlobName: String?, container: String?, scheme: String): String? {
        return if (StringUtils.isBlank(fullStorageBlobName) || StringUtils.isBlank(container) || scheme.isNullOrBlank())
            throw IllegalArgumentException("Blob Name ,container and scheme name cannot be empty")
        else if (scheme!!.startsWith(ADLSGen2StorageAccount.DefaultScheme)) "https://$fullStorageBlobName/$container/SparkSubmission/"
        else "$scheme://$container@$fullStorageBlobName/SparkSubmission/"
    }

    override fun getUploadPath(account: IHDIStorageAccount): String? =
            when (account) {
                is HDStorageAccount -> getAzureBlobStoragePath(account.fullStorageBlobName, account.defaultContainer, account.scheme)
                is ADLSStorageAccount ->
                    if (StringUtils.isBlank(account.name) || StringUtils.isBlank(account.defaultContainerOrRootPath)) null
                    else "adl://${account.name}.azuredatalakestore.net${account.defaultContainerOrRootPath}SparkSubmission/"
                is AzureSparkCosmosCluster.StorageAccount -> account.defaultContainerOrRootPath?.let { "${it}SparkSubmission/" }
                else -> null
            }
}