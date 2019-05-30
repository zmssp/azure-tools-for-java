package com.microsoft.intellij.serviceexplorer.azure.arm;

import com.microsoft.intellij.helpers.arm.ExportTemplate;
import com.microsoft.tooling.msservices.helpers.Name;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;

@Name("Export Resource Template")
public class ExportTemplateAction extends NodeActionListener {

    private final DeploymentNode deploymentNode;
    public ExportTemplateAction(DeploymentNode deploymentNode) {
        this.deploymentNode = deploymentNode;
    }

    @Override
    protected void actionPerformed(NodeActionEvent nodeActionEvent) {
        new ExportTemplate(deploymentNode).doExport();
    }

}