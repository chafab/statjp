package com.nekonex.statjp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class TableCategory implements ITableInfoParser {
    private String _categoryName;
    private String _categoryId;
    private HashMap<String, TableClass> mapTableClassByTableClassId = new HashMap<>();
    private boolean hasAgeFilter = false;
    private boolean hasFilter = false;

    public TableCategory(JsonObject object)
    {
        _categoryId = object.getAsJsonObject().get("@id").getAsString();
        _categoryName = object.getAsJsonObject().get("@name").getAsString();

        if (object.get("CLASS").isJsonArray()) {
            JsonArray classElementArray = object.get("CLASS").getAsJsonArray();
            for (int j = 0; j < classElementArray.size(); ++j) {
                TableClass tabClass = TableClass.parse(classElementArray.get(j).getAsJsonObject());
                hasAgeFilter = hasAgeFilter || tabClass.hasAgeFilter();
                hasFilter = hasFilter || tabClass.hasFilter();
                mapTableClassByTableClassId.put(tabClass.get_code(), tabClass);
            }
        } else {
            TableClass tabClass = TableClass.parse( object.get("CLASS").getAsJsonObject());
            hasAgeFilter = hasAgeFilter || tabClass.hasAgeFilter();
            hasFilter = hasFilter || tabClass.hasFilter();
            mapTableClassByTableClassId.put(tabClass.get_code(), tabClass);
        }
    }


    public String get_categoryName()
    {
        return _categoryName;
    }

    public String get_categoryId()
    {
        return _categoryId;
    }
    public void buildCsv(StringBuffer buffer)
    {
        if (buffer.length() > 0)
        {
            buffer.append(",\""+_categoryName+"\"");
        }
        else
        {
            buffer.append("\""+_categoryName+"\"");
        }
        if (hasFilter)
        {
            buffer.append(",\""+_categoryName+" filter"+"\"");
        }
        if (hasAgeFilter)
        {
            buffer.append(",\""+_categoryName+" minAge"+"\""+",\""+_categoryName+" maxAge"+"\"");
        }
    }

    @Override
    public void parse(JsonObject tableInfo, StringBuffer buffer)
    {
        String str = tableInfo.get("@"+_categoryId).getAsString();
        mapTableClassByTableClassId.get(str).buildCsv(buffer, hasFilter,hasAgeFilter);
    }
}
