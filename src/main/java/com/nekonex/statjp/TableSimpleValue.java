package com.nekonex.statjp;

import com.google.gson.JsonObject;

public class TableSimpleValue implements ITableInfoParser {
    private String _key;
    private String _alias;
    public TableSimpleValue(String key, String alias)
    {
        _key = key;
        _alias = alias;
    }

    @Override
    public void buildCsv(StringBuffer buffer)
    {
        if (buffer.length() > 0)
        {
            buffer.append(",\""+_alias+"\"");
        }
        else
        {
            buffer.append("\""+_alias+"\"");
        }
    }

    @Override
    public void parse(JsonObject tableInfo, StringBuffer buffer) {
        if (buffer.length() > 0) {
            buffer.append(",\"" + tableInfo.get(_key).getAsString() + "\"");
        } else {
            buffer.append("\"" + tableInfo.get( _key).getAsString() + "\"");
        }
    }
}
