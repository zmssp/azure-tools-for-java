package com.microsoft.intellij.helpers.arm;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.microsoft.azure.management.resources.DeploymentMode;
import com.microsoft.azuretools.azurecommons.util.Utils;
import com.microsoft.intellij.helpers.base.BaseEditor;
import com.microsoft.intellij.language.arm.ARMLanguage;
import com.microsoft.intellij.ui.util.UIUtils;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments.DeploymentNode;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import static com.microsoft.intellij.serviceexplorer.azure.arm.UpdateDeploymentAction.NOTIFY_UPDATE_DEPLOYMENT_FAIL;
import static com.microsoft.intellij.serviceexplorer.azure.arm.UpdateDeploymentAction.NOTIFY_UPDATE_DEPLOYMENT_SUCCESS;

public class ResourceTemplateView extends BaseEditor {

    public static final String ID = "com.microsoft.intellij.helpers.arm.ResourceTemplateView";
    private JButton saveAsTemplateButton;
    private JButton updateDeploymentButton;
    private JPanel contentPane;
    private JPanel editorPanel;
    private DeploymentNode node;
    private Project project;
    private static final String PROMPT_MESSAGE_SAVE_TEMPALTE = "Would you like to save the template file before you exit";
    private static final String PROMPT_MESSAGE_UPDATE_DEPLOYMENT = "Are you sure you want to update the deployment with the modified template";

    public void loadTemplate(DeploymentNode node, String template) {
        this.node = node;
        this.project = (Project) node.getProject();
        final String prettyTemplate = Utils.getPrettyJson(template);
        FileEditor editor = createEditor(prettyTemplate);
        GridConstraints constraints = new GridConstraints();
        constraints.setFill(GridConstraints.FILL_NONE);
        constraints.setAnchor(GridConstraints.ANCHOR_WEST);
        editorPanel.add(editor.getComponent(), constraints);

        project.getMessageBus().connect(editor).
                subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER, new FileEditorManagerListener.Before() {
            @Override
            public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                if (file.getFileType().getName().equals(ResourceTemplateViewProvider.TYPE) &&
                        file.getName().equals(node.getName())) {
                    String editorText = ((PsiAwareTextEditorImpl) editor).getEditor().getDocument().getText();
                    if (editorText.equals(prettyTemplate)) {
                        return;
                    }
                    if (DefaultLoader.getUIHelper().showConfirmation(PROMPT_MESSAGE_SAVE_TEMPALTE, "Azure Explorer",
                            new String[]{"Yes", "No"}, null)) {
                        new ExportTemplate(node).doExport(editorText);
                    }
                }
            }
        });

        saveAsTemplateButton.addActionListener((e) ->
                new ExportTemplate(node).doExport(((PsiAwareTextEditorImpl) editor).
                        getEditor().getDocument().getText())
        );

        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        updateDeploymentButton.addActionListener((e) -> {
            try {
                if (DefaultLoader.getUIHelper().showConfirmation(PROMPT_MESSAGE_UPDATE_DEPLOYMENT, "Azure Explorer",
                        new String[]{"Yes", "No"}, null)) {
                    ProgressManager.getInstance().run(new Task.Backgroundable(project,
                            "Update your azure resource " + node.getDeployment().name() + "...", false) {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            try {
                                String template = ((PsiAwareTextEditorImpl) editor).getEditor().getDocument().getText();
                                node.getDeployment().update().
                                        withTemplate(template)
                                        .withParameters("{}")
                                        .withMode(DeploymentMode.INCREMENTAL).apply();
                                UIUtils.showNotification(statusBar, NOTIFY_UPDATE_DEPLOYMENT_SUCCESS, MessageType.INFO);
                            } catch (Exception e) {
                                UIUtils.showNotification(statusBar,
                                        NOTIFY_UPDATE_DEPLOYMENT_FAIL + ", " + e.getMessage(), MessageType.ERROR);
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                UIUtils.showNotification(statusBar, NOTIFY_UPDATE_DEPLOYMENT_FAIL + ", " + ex.getMessage(),
                        MessageType.ERROR);
            }
        });
    }

    private FileEditor createEditor(String template) {
        FileEditor fileEditor = PsiAwareTextEditorProvider.getInstance()
                .createEditor(project, new LightVirtualFile(node.getName() + ".json", ARMLanguage.INSTANCE, template));
        return fileEditor;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return contentPane;
    }

    @NotNull
    @Override
    public String getName() {
        return ID;
    }

    @Override
    public void dispose() {

    }
}
