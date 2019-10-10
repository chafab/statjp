package com.nekonex.statjp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

public class ApplicationConfiguration {
    @Value("${app_id}")
    private String _appId;

    @Value("${output_path}")
    private String _output_path;
            ;
    public String getAppID()
    {
        return _appId;
    }

    public String getOutputPath()
    {
        return _output_path;
    }

    public ApplicationConfiguration(String appId, String output_path)
    {
        _appId = appId;
        _output_path = output_path;
    }
}

