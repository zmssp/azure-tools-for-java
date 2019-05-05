package com.microsoft.intellij.forms.arm;


import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.HyperlinkLabel;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.Deployment.DefinitionStages.WithTemplate;
import com.microsoft.azure.management.resources.DeploymentMode;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.authmanage.models.SubscriptionDetail;
import com.microsoft.azuretools.utils.AzureModel;
import com.microsoft.intellij.ui.util.UIUtils;
import com.microsoft.intellij.ui.util.UIUtils.ElementWrapper;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
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
    private JTextField rgNameTextFiled;
    private JComboBox rgNameCb;
    private JRadioButton createNewRgButton;
    private JRadioButton useExistingRgButton;
    private JTextField deploymentNameTextField;
    private JComboBox regionCb;
    private JLabel usingExistRgRegionLabel;
    private JLabel usingExistRgRegionDetailLabel;
    private JLabel createNewRgRegionLabel;
    private JComboBox subscriptionCb;
    private JRadioButton templateFileRadioButton;
    private JRadioButton templateURLRadioButton;
    private JTextField templateURLTextField;
    private TextFieldWithBrowseButton templateTextField;
    private HyperlinkLabel templateURLLabel;
    private Project project;

    public CreateDeploymentForm(Project project) {
        super(project, false);
        this.project = project;
        setModal(true);
        setTitle("Create Resource Template");

        final ButtonGroup resourceGroup = new ButtonGroup();
        resourceGroup.add(createNewRgButton);
        resourceGroup.add(useExistingRgButton);
        useExistingRgButton.setSelected(true);
        createNewRgButton.addItemListener((e) -> radioRgLogic());
        useExistingRgButton.addItemListener((e) -> radioRgLogic());

        rgNameCb.addActionListener((l) -> {
            ResourceGroup rg = ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue();
            usingExistRgRegionDetailLabel.setText(rg.region().label());
        });
        subscriptionCb.addActionListener((l) -> fillResourceGroup());

        initTemplateComponent();
        fill();
        radioRgLogic();
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
                    Azure azure = AuthMethodManager.getInstance().getAzureClient(subs.getSubscriptionId());
                    WithTemplate template;
                    if (createNewRgButton.isSelected()) {
                        template = azure
                            .deployments().define(deploymentName)
                            .withNewResourceGroup(rgNameTextFiled.getText(),
                                ((ElementWrapper<Region>) regionCb.getSelectedItem()).getValue());
                    } else {
                        template = azure.deployments().define(deploymentName)
                            .withExistingResourceGroup(
                                ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue());
                    }

                    if (templateFileRadioButton.isSelected()) {
                        String fileText = templateTextField.getText();
                        String content = IOUtils.toString(new FileReader(fileText));
                        template.withTemplate(content)
                            .withParameters("{}")
                            .withMode(DeploymentMode.INCREMENTAL)
                            .create();
                    } else {
                        template.withTemplateLink(templateURLTextField.getText(), "1.0.0.0")
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

    protected void initTemplateComponent() {
        final ButtonGroup templateGroup = new ButtonGroup();
        templateGroup.add(templateFileRadioButton);
        templateGroup.add(templateURLRadioButton);
        templateFileRadioButton.setSelected(true);
        templateFileRadioButton.addItemListener((e) -> radioTemplateLogic());
        templateURLRadioButton.addItemListener((e) -> radioTemplateLogic());

        templateTextField.addActionListener(
            UIUtils.createFileChooserListener(templateTextField, project,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor()));
        templateURLLabel.setHyperlinkText("Browse for samples");
        templateURLLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse(TEMPLATE_URL);
            }
        });

        radioTemplateLogic();
    }

    private void radioTemplateLogic() {
        boolean isFile = templateFileRadioButton.isSelected();
        templateTextField.setVisible(isFile);
        templateURLTextField.setVisible(!isFile);
        templateURLLabel.setVisible(!isFile);
        pack();
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
        rgNameTextFiled.setVisible(isCreateNewRg);
        regionCb.setVisible(isCreateNewRg);
        createNewRgRegionLabel.setVisible(isCreateNewRg);

        rgNameCb.setVisible(!isCreateNewRg);
        usingExistRgRegionLabel.setVisible(!isCreateNewRg);
        usingExistRgRegionDetailLabel.setVisible(!isCreateNewRg);
        pack();
    }

}
