package com.microsoft.intellij.helpers.arm;

import com.microsoft.azuretools.azurecommons.util.Utils;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;
import rx.schedulers.Schedulers;

import java.io.File;

public class ExportTemplate {

    private final DeploymentNode deploymentNode;
    private static final String FILE_SELECTOR_TITLE = "Choose Where You Want to Save the Azure Resource Manager "
            + "Template.";
    private static final String EXPORT_TEMPLATE_FAIL = "MS Services - Error Export resource manager template";

    public ExportTemplate(DeploymentNode deploymentNode) {
        this.deploymentNode = deploymentNode;
    }

    public void doExport() {
        File file = DefaultLoader.getUIHelper().showFileSaver(FILE_SELECTOR_TITLE, deploymentNode.getName() + ".json");
        if (file != null) {
            deploymentNode.getDeployment().exportTemplateAsync().subscribeOn(Schedulers.io()).subscribe(
                    res -> DefaultLoader.getIdeHelper()
                            .invokeLater(() -> deploymentNode.getDeploymentNodePresenter()
                                    .onGetExportTemplateRes(Utils.getPrettyJson(res.templateAsJson()), file)),
                    ex -> DefaultLoader.getUIHelper().showError(ex.getMessage(), EXPORT_TEMPLATE_FAIL));
        }
    }

    public void doExport(String template) {
        File file = DefaultLoader.getUIHelper().showFileChooser(FILE_SELECTOR_TITLE);
        if (file != null) {
            deploymentNode.getDeploymentNodePresenter().onGetExportTemplateRes(Utils.getPrettyJson(template), file);
        }
    }

}
