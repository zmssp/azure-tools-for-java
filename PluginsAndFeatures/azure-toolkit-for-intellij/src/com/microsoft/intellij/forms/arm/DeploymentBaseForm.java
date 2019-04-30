package com.microsoft.intellij.forms.arm;

import com.intellij.openapi.project.Project;
import com.microsoft.intellij.ui.components.AzureDialogWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class DeploymentBaseForm extends AzureDialogWrapper {

    public static final String TEMPLATE_URL = "https://github.com/Azure/azure-quickstart-templates";
    public static final String ARM_DOC = "https://docs.microsoft.com/en-us/azure/azure-resource-manager/"
        + "resource-group-overview#template-deployment";

    protected DeploymentBaseForm(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
    }
}
