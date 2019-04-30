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

package com.microsoft.tooling.msservices.serviceexplorer.azure.arm;

import com.microsoft.azure.management.resources.Deployment;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azuretools.azurecommons.helpers.AzureCmdException;
import com.microsoft.azuretools.core.mvp.model.ResourceEx;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.RefreshableNode;
import com.microsoft.tooling.msservices.serviceexplorer.azure.AzureNodeActionPromptListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;
import java.io.IOException;
import java.util.List;

public class ResourceManagementNode extends RefreshableNode implements ResourceManagementNodeView {

    private static final String ICON_RESOURCE_MANAGEMENT = "WebApp_16.png";
    private static final String ACTION_DELETE = "Delete";
    private static final String DELETE_RESOURCE_GROUP_PROMPT_MESSAGE = "This operation will delete the Resource "
        + "Group: %s. Are you sure you want to continue?";
    private static final String DELETE_RESOURCE_GROUP_PROGRESS_MESSAGE = "Deleting Resource Group";
    private final ResourceManagementNodePresenter rmNodePresenter;
    private final String sid;

    public ResourceManagementNode(ResourceManagementModule parent, String subscriptionId, ResourceGroup resourceGroup) {
        super(resourceGroup.id(), resourceGroup.name(), parent, ICON_RESOURCE_MANAGEMENT, true);
        rmNodePresenter = new ResourceManagementNodePresenter();
        rmNodePresenter.onAttachView(this);
        sid = subscriptionId;
        loadActions();
    }

    @Override
    protected void refreshItems() throws AzureCmdException {
        try {
            rmNodePresenter.onModuleRefresh(sid, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadActions() {
        addAction(ACTION_DELETE, new DeleteResourceGroupAction());
        super.loadActions();
    }

    @Override
    public void renderChildren(List<ResourceEx<Deployment>> resourceExes) {
        for (final ResourceEx<Deployment> resourceEx : resourceExes) {
            final Deployment deployment = resourceEx.getResource();
            final DeploymentNode node = new DeploymentNode(this, resourceEx.getSubscriptionId(), deployment);
            addChildNode(node);
        }
    }

    private class DeleteResourceGroupAction extends AzureNodeActionPromptListener {
        DeleteResourceGroupAction() {
            super(ResourceManagementNode.this, String.format(DELETE_RESOURCE_GROUP_PROMPT_MESSAGE, name),
                DELETE_RESOURCE_GROUP_PROGRESS_MESSAGE);
        }

        @Override
        protected void azureNodeAction(NodeActionEvent e) {
            getParent().removeNode(sid, name, ResourceManagementNode.this);
        }

        @Override
        protected void onSubscriptionsChanged(NodeActionEvent e) {
        }
    }

}