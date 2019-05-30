package com.microsoft.intellij.forms.arm;


import static com.microsoft.intellij.serviceexplorer.azure.arm.CreateDeploymentAction.NOTIFY_CREATE_DEPLOYMENT_FAIL;
import static com.microsoft.intellij.serviceexplorer.azure.arm.CreateDeploymentAction.NOTIFY_CREATE_DEPLOYMENT_SUCCESS;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.HyperlinkLabel;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.Deployment.DefinitionStages.WithTemplate;
import com.microsoft.azure.management.resources.DeploymentMode;
import com.microsoft.azure.management.resources.Location;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azuretools.authmanage.AuthMethodManager;
import com.microsoft.azuretools.authmanage.models.SubscriptionDetail;
import com.microsoft.azuretools.utils.*;
import com.microsoft.intellij.AzurePlugin;
import com.microsoft.intellij.ui.util.UIUtils;
import com.microsoft.intellij.ui.util.UIUtils.ElementWrapper;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.ResourceManagementNode;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private TextFieldWithBrowseButton templateTextField;
    private HyperlinkLabel lblTemplateHover;
    private Project project;
    private StatusBar statusBar;
    private String rgName;
    private String deploymentName;

    public CreateDeploymentForm(Project project) {
        super(project, false);
        this.project = project;
        statusBar = WindowManager.getInstance().getStatusBar(project);
        setModal(true);
        setTitle("Create Deployment");

        final ButtonGroup resourceGroup = new ButtonGroup();
        resourceGroup.add(createNewRgButton);
        resourceGroup.add(useExistingRgButton);
        useExistingRgButton.setSelected(true);
        createNewRgButton.addItemListener((e) -> radioRgLogic());
        useExistingRgButton.addItemListener((e) -> radioRgLogic());

        rgNameCb.addActionListener((l) -> {
            if (rgNameCb.getSelectedItem() != null) {
                ResourceGroup rg = ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue();
                usingExistRgRegionDetailLabel.setText(rg.region().label());
            }
        });
        subscriptionCb.addActionListener((l) -> {
            fillResourceGroup();
            fillRegion();
        });

        lblTemplateHover.setHyperlinkText("Browse for samples");
        lblTemplateHover.setHyperlinkTarget(ARM_DOC);

        initTemplateComponent();
        radioRgLogic();
        initCache();
        fill();
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
        deploymentName = deploymentNameTextField.getText();
        ProgressManager.getInstance().run(new Task.Backgroundable(project,
            "Deploying your azure resource " + deploymentName + "...", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    SubscriptionDetail subs = (SubscriptionDetail) subscriptionCb.getSelectedItem();
                    Azure azure = AuthMethodManager.getInstance().getAzureClient(subs.getSubscriptionId());
                    WithTemplate template;
                    if (createNewRgButton.isSelected()) {
                        rgName = rgNameTextFiled.getText();
                        template = azure
                            .deployments().define(deploymentName)
                            .withNewResourceGroup(rgNameTextFiled.getText(),
                                ((ElementWrapper<Region>) regionCb.getSelectedItem()).getValue());
                    } else {
                        ResourceGroup rg = ((ElementWrapper<ResourceGroup>) rgNameCb.getSelectedItem()).getValue();
                        rgName = rg.name();
                        template = azure.deployments().define(deploymentName).withExistingResourceGroup(rg);
                    }

                    String fileText = templateTextField.getText();
                    String content = IOUtils.toString(new FileReader(fileText));
                    template.withTemplate(content)
                            .withParameters("{}")
                            .withMode(DeploymentMode.INCREMENTAL)
                            .create();

                    UIUtils.showNotification(statusBar, NOTIFY_CREATE_DEPLOYMENT_SUCCESS, MessageType.INFO);
                } catch (Exception e) {
                    UIUtils.showNotification(statusBar, NOTIFY_CREATE_DEPLOYMENT_FAIL + ", " + e.getMessage(),
                        MessageType.ERROR);
                } finally {
                    updateUI();
                }
            }
        });
        close(DialogWrapper.OK_EXIT_CODE, true);
    }

    private void updateUI() {
        AzureUIRefreshCore.execute(new AzureUIRefreshEvent(AzureUIRefreshEvent.EventType.REFRESH, rgName));
    }

    protected void initTemplateComponent() {
        templateTextField.addActionListener(
            UIUtils.createFileChooserListener(templateTextField, project,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor()));
    }

    private void fill() {
        Map<SubscriptionDetail, List<ResourceGroup>> srgMap = AzureModel.
            getInstance().getSubscriptionToResourceGroupMap();

        for (SubscriptionDetail sd : srgMap.keySet()) {
            subscriptionCb.addItem(sd);
        }
        if (subscriptionCb.getItemCount() > 0) {
            subscriptionCb.setSelectedIndex(0);
        }

        fillRegion();
        fillResourceGroup();
        deploymentNameTextField.setText("deployment" + System.currentTimeMillis());
        rgNameTextFiled.setText("resouregroup" + System.currentTimeMillis());
    }

    private void initCache() {
        Map<SubscriptionDetail, List<Location>> subscription2Location = AzureModel.getInstance().getSubscriptionToLocationMap();
        if (subscription2Location == null) {
            ProgressManager.getInstance().run(new Task.Modal(project,"Loading Available Locations...", false) {
                @Override
                public void run(ProgressIndicator indicator) {
                    try {
                        AzureModelController.updateSubscriptionMaps(null);
                    } catch (Exception ex) {
                        AzurePlugin.log("Error loading locations", ex);
                    }
                }
            });
        }
    }

    private void fillRegion() {
        List<Location> locations = AzureModel.getInstance().getSubscriptionToLocationMap()
            .get(subscriptionCb.getSelectedItem()).stream().sorted(Comparator.comparing(Location::displayName)).
                collect(Collectors.toList());
        regionCb.removeAllItems();
        for (Location location : locations) {
            Region region = location.region();
            regionCb.addItem(new ElementWrapper<>(region.label(), region));
            if (region == Region.US_WEST2) {
                regionCb.setSelectedIndex(regionCb.getItemCount() - 1);
            }
        }
    }

    private void fillResourceGroup() {
        Map<SubscriptionDetail, List<ResourceGroup>> srgMap = AzureModel
            .getInstance().getSubscriptionToResourceGroupMap();
        rgNameCb.removeAllItems();
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

    public void filleSubsAndRg(ResourceManagementNode node) {
        selectSubs(node.getSid());
        fillResourceGroup();
        UIUtils.selectByText(rgNameCb, node.getRgName());
        radioRgLogic();
    }

    private void selectSubs(String targetSid) {
        for (int i = 0; i < subscriptionCb.getItemCount(); i++) {
            if (((SubscriptionDetail)subscriptionCb.getItemAt(i)).getSubscriptionId().equals(targetSid)) {
                subscriptionCb.setSelectedIndex(i);
                break;
            }
        }
    }
}
