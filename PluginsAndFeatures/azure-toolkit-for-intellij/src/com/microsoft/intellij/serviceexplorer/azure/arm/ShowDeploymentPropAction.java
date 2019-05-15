package com.microsoft.intellij.serviceexplorer.azure.arm;

import com.intellij.openapi.project.Project;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.azurecommons.helpers.AzureCmdException;
import com.microsoft.azuretools.ijidea.actions.AzureSignInAction;
import com.microsoft.intellij.AzurePlugin;
import com.microsoft.intellij.forms.arm.UpdateDeploymentForm;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;

public class ShowDeploymentPropAction extends NodeActionListener {

    private final DeploymentNode deploymentNode;

    public ShowDeploymentPropAction(DeploymentNode deploymentNode) {
        this.deploymentNode = deploymentNode;
    }

    @Override
    protected void actionPerformed(NodeActionEvent nodeActionEvent) throws AzureCmdException {
        try {
            Project project = (Project) deploymentNode.getProject();
            if (AzureSignInAction.doSignIn(AuthMethodManager.getInstance(), project)) {
                UpdateDeploymentForm updateDeploymentForm = new UpdateDeploymentForm(project, deploymentNode);
                updateDeploymentForm.show();
            }
        } catch (Exception ex) {
            AzurePlugin.log("Error Update Resource Template", ex);
            DefaultLoader.getUIHelper()
                .showException("Error Update Resource Template", ex, "Error Update Resource Template", false, true);
        }
    }
}
