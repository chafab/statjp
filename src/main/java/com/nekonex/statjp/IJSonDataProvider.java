package com.nekonex.statjp;

import com.google.gson.JsonObject;

public interface IJSonDataProvider {
    JsonObject getJSonObject(String path)  throws Exception;
}
