/**
 * Copyright (c) Microsoft Corporation
 * <p>
 * <p>
 * All rights reserved.
 * <p>
 * <p>
 * MIT License
 * <p>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * <p>
 * <p>
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microsoft.azure.hdinsight.sdk.storage.adlsgen2;

import com.microsoft.azure.hdinsight.sdk.storage.webhdfs.WebHdfsParamsBuilder;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ADLSGen2ParamsBuilder extends WebHdfsParamsBuilder {
    public ADLSGen2ParamsBuilder() {
    }

    public ADLSGen2ParamsBuilder setAction(@NotNull String value) {
        params.add(new BasicNameValuePair("action", value));
        return this;
    }

    public ADLSGen2ParamsBuilder setPosition(@NotNull long value) {
        params.add(new BasicNameValuePair("position", String.valueOf(value)));
        return this;
    }

    public ADLSGen2ParamsBuilder setResource(@NotNull String value) {
        params.add(new BasicNameValuePair("resource", value));
        return this;
    }
}
