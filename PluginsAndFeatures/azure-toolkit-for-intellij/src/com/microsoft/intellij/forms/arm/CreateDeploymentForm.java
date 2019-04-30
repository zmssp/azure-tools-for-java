package com.microsoft.intellij.forms.arm;


import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.DeploymentMode;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.authmanage.models.SubscriptionDetail;
import com.microsoft.azuretools.utils.AzureModel;
import com.microsoft.intellij.ui.components.AzureDialogWrapper;
import com.microsoft.intellij.ui.util.UIUtils;
import com.microsoft.intellij.ui.util.UIUtils.ElementWrapper;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateDeploymentForm extends DeploymentBaseForm {

    private JPanel contentPane;
    private JPanel resourceGroupPane;
    private JTextField rgNameTextFiled;
    private JComboBox rgNameCb;
    private JRadioButton createNewRgButton;
    private JRadioButton useExistingRgButton;
    private JRadioButton templateFileButton;
    private JRadioButton templateURLButton;
    private TextFieldWithBrowseButton templateTextField;
    private JTextField templateURLTextField;
    private JTextField deploymentNameTextField;
    private JComboBox regionCb;
    private JLabel usingExistRgRegionLabel;
    private JLabel usingExistRgRegionDetailLabel;
    private JLabel createNewRgRegionLabel;
    private JComboBox subscriptionCb;
    private JLabel templateURLLabel;
    private Project project;

    public CreateDeploymentForm(Project project) {
        super(project, false);
        this.project = project;
        setModal(true);
        setTitle("Create Resource Template");

        resourceGroupPane.setName("Resource Group");
        final ButtonGroup resourceGroup = new ButtonGroup();
        resourceGroup.add(createNewRgButton);
        resourceGroup.add(useExistingRgButton);
        useExistingRgButton.setSelected(true);
        createNewRgButton.addItemListener((e) -> radioRgLogic());
        useExistingRgButton.addItemListener((e) -> radioRgLogic());

        final ButtonGroup templateGroup = new ButtonGroup();
        templateGroup.add(templateFileButton);
        templateGroup.add(templateURLButton);
        templateFileButton.setSelected(true);
        templateFileButton.addItemListener((e) -> radioTemplateLogic());
        templateURLButton.addItemListener((e) -> radioTemplateLogic());

        templateTextField.addActionListener(
            UIUtils.createFileChooserListener(templateTextField, project,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor()));
        subscriptionCb.addActionListener((l) -> fillResourceGroup());
        rgNameCb.addActionListener((l) -> {
            ResourceGroup rg = ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue();
            usingExistRgRegionDetailLabel.setText(rg.region().label());
        });

        fill();
        radioRgLogic();
        radioTemplateLogic();
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return null;
    }

    @Override
    protected void doOKAction() {
        String deploymentName = deploymentNameTextField.getText();
        ProgressManager.getInstance().run(new Task.Backgroundable(project,
            "Deploying your azure resource " + deploymentName + "...", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    SubscriptionDetail subs = (SubscriptionDetail) subscriptionCb.getSelectedItem();
                    String fileText = templateTextField.getText();
                    String template = IOUtils.toString(new FileReader(fileText));
                    Azure azure = AuthMethodManager.getInstance().getAzureClient(subs.getSubscriptionId());
                    if (createNewRgButton.isSelected()) {
                        azure.deployments().define(deploymentName)
                            .withNewResourceGroup(rgNameTextFiled.getText(),
                                ((ElementWrapper<Region>) regionCb.getSelectedItem()).getValue())
                            .withTemplate(template)
                            .withParameters("{}")
                            .withMode(DeploymentMode.INCREMENTAL)
                            .create();
                    } else {
                        azure.deployments().define(deploymentName)
                            .withExistingResourceGroup(
                                ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue())
                            .withTemplate(template)
                            .withParameters("{}")
                            .withMode(DeploymentMode.INCREMENTAL)
                            .create();
                    }
                } catch (Exception e) {
                    DefaultLoader.getIdeHelper().invokeAndWait(() -> DefaultLoader.getUIHelper().
                        showException("Deploy Azure resource Failed", e, "Deploy Azure resource Failed", false, true));
                }
            }
        });
        close(DialogWrapper.OK_EXIT_CODE, true);
    }

    private void fill() {
        Map<SubscriptionDetail, List<ResourceGroup>> srgMap = AzureModel
            .getInstance().getSubscriptionToResourceGroupMap();
        for (SubscriptionDetail sd : srgMap.keySet()) {
            subscriptionCb.addItem(sd);
        }
        if (subscriptionCb.getItemCount() > 0) {
            subscriptionCb.setSelectedIndex(0);
        }
        for (Region region : Region.values()) {
            regionCb.addItem(new ElementWrapper<>(region.label(), region));
        }
        fillResourceGroup();
        deploymentNameTextField.setText("deploynment" + System.currentTimeMillis());
        rgNameTextFiled.setText("resouregroup" + System.currentTimeMillis());
    }

    private void fillResourceGroup() {
        Map<SubscriptionDetail, List<ResourceGroup>> srgMap = AzureModel
            .getInstance().getSubscriptionToResourceGroupMap();

        for (SubscriptionDetail sd : srgMap.keySet()) {
            if (sd == subscriptionCb.getSelectedItem()) {
                for (ResourceGroup rg : srgMap.get(sd)) {
                    rgNameCb.addItem(new ElementWrapper<>(rg.name(), rg));
                }
                break;
            }
        }
    }

    private void radioRgLogic() {
        boolean isCreateNewRg = createNewRgButton.isSelected();
        rgNameTextFiled.setEnabled(isCreateNewRg);
        regionCb.setEnabled(isCreateNewRg);
        createNewRgRegionLabel.setEnabled(isCreateNewRg);

        rgNameCb.setEnabled(!isCreateNewRg);
        usingExistRgRegionLabel.setEnabled(!isCreateNewRg);
        usingExistRgRegionDetailLabel.setEnabled(!isCreateNewRg);
    }

    private void radioTemplateLogic() {
        boolean isFile = templateFileButton.isSelected();
        templateTextField.setEnabled(isFile);
        templateURLTextField.setEnabled(!isFile);
        templateURLLabel.setEnabled(!isFile);
    }

    private void createUIComponents() {
        templateURLLabel = new JLabel(AllIcons.General.Information);
        templateURLLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse(TEMPLATE_URL);
            }
        });
    }
}
