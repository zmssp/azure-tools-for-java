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

import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azuretools.azurecommons.helpers.AzureCmdException;
import com.microsoft.azuretools.core.mvp.model.ResourceEx;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.serviceexplorer.AzureRefreshableNode;
import com.microsoft.tooling.msservices.serviceexplorer.Node;
import java.util.List;

public class ResourceManagementModule extends AzureRefreshableNode implements ResourceManagementModuleView {

    private static final String RESOURCE_MANAGEMENT_MODULE_ID = ResourceManagementModule.class.getName();
    private static final String ICON_PATH = "WebApp_16.png";
    private static final String BASE_MODULE_NAME = "Resource Management";
    private final ResourceManagementModulePresenter rmModulePresenter;

    public ResourceManagementModule(Node parent) {
        super(RESOURCE_MANAGEMENT_MODULE_ID, BASE_MODULE_NAME, parent, ICON_PATH);
        rmModulePresenter = new ResourceManagementModulePresenter();
        rmModulePresenter.onAttachView(ResourceManagementModule.this);
    }

    @Override
    protected void refreshItems() throws AzureCmdException {
        try {
            rmModulePresenter.onModuleRefresh();
        } catch (Exception e) {
            DefaultLoader.getUIHelper()
                .showException("An error occurred while attempting to refresh the resource manage module ",
                    e, "Azure Services Explorer - Error Refresh resource manage module", false, true);
        }

    }

    @Override
    public void removeNode(String sid, String id, Node node) {
        try {
            rmModulePresenter.onDeleteResourceGroup(sid, node.getName());
            removeDirectChildNode(node);
        } catch (Exception e) {
            DefaultLoader.getUIHelper()
                .showException("An error occurred while attempting to delete the resource group ",
                    e, "Azure Services Explorer - Error Deleting Resource Group", false, true);
        }
    }

    @Override
    public void renderChildren(List<ResourceEx<ResourceGroup>> resourceExes) {
        for (final ResourceEx<ResourceGroup> resourceEx : resourceExes) {
            final ResourceGroup rg = resourceEx.getResource();
            final ResourceManagementNode node = new ResourceManagementNode(this, resourceEx.getSubscriptionId(), rg);
            addChildNode(node);
        }
    }
}
