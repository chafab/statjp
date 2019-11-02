package com.nekonex.statjp;

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

