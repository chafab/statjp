/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nekonex.statjp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

public class ApplicationConfiguration {
    private String _appId;

    private String _output_path;

    private boolean _use_cache_data;

    private String _mindate;

    public String getAppID()
    {
        return _appId;
    }

    public String getOutputPath()
    {
        return _output_path;
    }

    public boolean getUseCacheData() { return _use_cache_data;}

    public String getMinDate() { return _mindate; }

    public ApplicationConfiguration(String appId, String output_path, boolean use_cache_data, String mindate)
    {
        _appId = appId;
        _output_path = output_path;
        _use_cache_data = use_cache_data;
        _mindate = mindate;
    }
}

