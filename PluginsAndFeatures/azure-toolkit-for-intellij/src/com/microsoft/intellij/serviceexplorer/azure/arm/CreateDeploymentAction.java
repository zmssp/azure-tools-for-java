package com.microsoft.intellij.serviceexplorer.azure.arm;


import com.intellij.openapi.project.Project;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.ijidea.actions.AzureSignInAction;
import com.microsoft.intellij.AzurePlugin;
import com.microsoft.intellij.forms.arm.CreateDeploymentForm;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.helpers.Name;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.ResourceManagementModule;

@Name("Create Resource Template")
public class CreateDeploymentAction extends NodeActionListener {
    private final Project project;

    public CreateDeploymentAction(ResourceManagementModule module) {
        this.project = (Project) module.getProject();
    }

    @Override
    protected void actionPerformed(NodeActionEvent nodeActionEvent) {
        try {
            if (AzureSignInAction.doSignIn(AuthMethodManager.getInstance(), project)) {
                CreateDeploymentForm createDeploymentForm = new CreateDeploymentForm(project);
                createDeploymentForm.show();
            }
        } catch (Exception ex) {
            AzurePlugin.log("Error creating storage account", ex);
            DefaultLoader
                .getUIHelper().showException("Error creating storage account", ex, "Error Creating Storage Account", false, true);
        }
    }
}
