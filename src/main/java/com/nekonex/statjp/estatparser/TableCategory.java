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
package com.nekonex.statjp.estatparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TableCategory implements ITableInfoParser {
    final static Logger logger = LogManager.getLogger(StatsListParser.class);

    private String _categoryName;
    private String _categoryId;
    private HashMap<String, TableClass> mapTableClassByTableClassId = new HashMap<>();
    private boolean hasAgeFilter = false;
    private boolean hasFilter = false;
    private boolean hasUnit = false;

    public TableCategory(JsonObject object)
    {
        _categoryId = object.getAsJsonObject().get("@id").getAsString();
        if (object.getAsJsonObject().get("@name") == null)
        {
            _categoryName = _categoryId;
            logger.info("name missing for category "+_categoryId);
        }
        else {
            _categoryName = object.getAsJsonObject().get("@name").getAsString();
        }

        if (object.get("CLASS").isJsonArray()) {
            JsonArray classElementArray = object.get("CLASS").getAsJsonArray();
            for (int j = 0; j < classElementArray.size(); ++j) {
                TableClass tabClass = TableClass.parse(classElementArray.get(j).getAsJsonObject());
                hasAgeFilter = hasAgeFilter || tabClass.hasAgeFilter();
                hasFilter = hasFilter || tabClass.hasFilter();
                hasUnit =  hasUnit || tabClass.hasUnit();
                mapTableClassByTableClassId.put(tabClass.get_code(), tabClass);
            }
        } else {
            TableClass tabClass = TableClass.parse( object.get("CLASS").getAsJsonObject());
            hasAgeFilter = hasAgeFilter || tabClass.hasAgeFilter();
            hasFilter = hasFilter || tabClass.hasFilter();
            hasUnit =  hasUnit || tabClass.hasUnit();
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
        if (hasUnit)
        {
            buffer.append(",\""+_categoryName+" unit"+"\"");
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
        mapTableClassByTableClassId.get(str).buildCsv(buffer, hasUnit, hasFilter,hasAgeFilter);
    }
}
