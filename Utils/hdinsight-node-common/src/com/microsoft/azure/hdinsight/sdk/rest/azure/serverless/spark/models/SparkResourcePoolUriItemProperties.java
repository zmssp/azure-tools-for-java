/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.hdinsight.sdk.rest.azure.serverless.spark.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Lake Analytics Spark Resource Pool URI Collection.
 */
public class SparkResourcePoolUriItemProperties {
    /**
     * Livy Server Url.
     */
    @JsonProperty(value = "livyServerUrl", access = JsonProperty.Access.WRITE_ONLY)
    private String livyServerUrl;

    /**
     * Spark History server Web UI URL.
     */
    @JsonProperty(value = "sparkHistoryWebUiUrl", access = JsonProperty.Access.WRITE_ONLY)
    private String sparkHistoryWebUiUrl;

    /**
     * Spark Master Web UI URL.
     */
    @JsonProperty(value = "sparkMasterWebUiUrl", access = JsonProperty.Access.WRITE_ONLY)
    private String sparkMasterWebUiUrl;

    /**
     * Get the livyServerUrl value.
     *
     * @return the livyServerUrl value
     */
    public String livyServerUrl() {
        return this.livyServerUrl;
    }

    /**
     * Get the sparkHistoryWebUiUrl value.
     *
     * @return the sparkHistoryWebUiUrl value
     */
    public String sparkHistoryWebUiUrl() {
        return this.sparkHistoryWebUiUrl;
    }

    /**
     * Get the sparkMasterWebUiUrl value.
     *
     * @return the sparkMasterWebUiUrl value
     */
    public String sparkMasterWebUiUrl() {
        return this.sparkMasterWebUiUrl;
    }

}
