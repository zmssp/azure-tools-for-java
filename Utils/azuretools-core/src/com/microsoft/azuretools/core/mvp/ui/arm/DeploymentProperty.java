package com.microsoft.azuretools.core.mvp.ui.arm;

import com.microsoft.azure.management.resources.Deployment;
import java.util.List;

public class DeploymentProperty {

    private Deployment deployment;
    private List<String> parameters;
    private List<String> variables;
    private List<String> resources;

    public DeploymentProperty(Deployment deployment, List<String> parameters,
        List<String> variables, List<String> resources) {
        this.deployment = deployment;
        this.parameters = parameters;
        this.variables = variables;
        this.resources = resources;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<String> getVariables() {
        return variables;
    }

    public List<String> getResources() {
        return resources;
    }
}
