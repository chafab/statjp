package com.nekonex.statjp;

import com.google.gson.JsonObject;

public interface ITableInfoParser {
    void parse(JsonObject tableInfo, StringBuffer buffer);
    void buildCsv(StringBuffer buffer);
}
