package com.microsoft.intellij.serviceexplorer.azure.arm;

import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.helpers.Name;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionEvent;
import com.microsoft.tooling.msservices.serviceexplorer.NodeActionListener;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;
import java.io.File;
import rx.schedulers.Schedulers;

@Name("Export Resource Template")
public class ExportTemplateAction extends NodeActionListener {

    private final DeploymentNode deploymentNode;
    private static final String FILE_SELECTOR_TITLE = "Choose Where You Want to Save the Azure Resource Manager "
        + "Template.";
    private static final String EXPORT_TEMPLATE_FAIL = "MS Services - Error Export resource manager template";

    public ExportTemplateAction(DeploymentNode deploymentNode) {
        this.deploymentNode = deploymentNode;
    }

    @Override
    protected void actionPerformed(NodeActionEvent nodeActionEvent) {
        File file = DefaultLoader.getUIHelper().showFileChooser(FILE_SELECTOR_TITLE);
        if (file != null) {
            deploymentNode.getDeployment().exportTemplateAsync().subscribeOn(Schedulers.io()).subscribe(
                res -> DefaultLoader.getIdeHelper()
                    .invokeLater(() -> deploymentNode.getDeploymentNodePresenter()
                        .onGetExportTemplateRes(res.templateAsJson(), file)),
                ex -> DefaultLoader.getUIHelper()
                    .showException(ex.getMessage(), ex, EXPORT_TEMPLATE_FAIL, false, true));
        }
    }

}