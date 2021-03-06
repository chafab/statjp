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

import com.google.gson.JsonObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableClass {
    private String _code = null;
    private String _name = null;
    private String _level = null;
    private String _unit = null;
    private String _minAge = null;
    private String _maxAge = null;
    private String _filter = null;
    private String _parentCode = null;

    private TableClass(String code, String name, String level, String unit, String minAge, String maxAge, String filter, String parentCode)
    {
        _code = code;
        _name = name;
        _level = level;
        _unit = unit;
        _minAge = minAge;
        _maxAge = maxAge;
        _filter = filter;
        _parentCode = parentCode;
    }

    public String get_code()
    {
        return _code;
    }

    public String get_name()
    {
        return _name;
    }

    public String get_level()
    {
        return _level;
    }

    public String get_unit()
    {
        return _unit;
    }

    public String get_parentCode() { return _parentCode; }

    public boolean hasAgeFilter()
    {
        return !StringUtils.isEmpty(_minAge);
    }

    public boolean hasFilter()
    {
        return !StringUtils.isEmpty(_filter);
    }
    public boolean hasUnit()
    {
        return !StringUtils.isEmpty(_unit);
    }


    public static TableClass parse(JsonObject obj)
    {
        String name = obj.get("@name").getAsString();
        String code = obj.get("@code").getAsString();
        if (name.contains(code))
            name = name.replace(code+"_","");
        String pattern ="\\(.*\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(name);
        String filter = "";
        String minAge = "";
        String maxAge = "";
        if (m.find())
        {
            name  = name.substring(0, m.start());
            String nameFilter = m.group(0);
            nameFilter = nameFilter.replace("(","").replace(")","");
            String[] filters =  nameFilter.split(",");
            ArrayList<String> arrayFilters = new ArrayList<>();
            for (String strFilter : filters) {
                pattern = "[0-9]+-[0-9]+";
                r = Pattern.compile(pattern);
                m = r.matcher(strFilter);
                if (m.find()) {
                    minAge = m.group(0).split("-")[0];
                    maxAge = m.group(0).split("-")[1];
                } else {
                    arrayFilters.add(strFilter);
                }
            }
            Collections.sort(arrayFilters);
            for (String strFilter : arrayFilters) {
                if (filter.length() != 0) {
                    filter += "," + strFilter;
                } else {
                    filter = strFilter;
                }
            }
        }
        String level = obj.get("@level").getAsString();
        String unit = "";
        if (obj.get("@unit") != null)
             unit = obj.get("@unit").getAsString();
        String parentCode="";
        if (obj.get("@parentCode") != null)
            parentCode = obj.get("@parentCode").getAsString();
        return new TableClass(code, name, level, unit, minAge, maxAge, filter, parentCode);
    }


    public void buildCsv(StringBuffer buffer, boolean printUnit, boolean printFilter, boolean printAge)
    {
        if (buffer.length() > 0)
        {
            buffer.append(",\""+_name+"\"");
        }
        else
        {
            buffer.append("\""+_name+"\"");
        }
        if (printUnit)
        {
            buffer.append(",\""+_unit+"\"");
        }
        if (printFilter)
        {
            buffer.append(",\""+_filter+"\"");
        }
        if (printAge)
        {
            buffer.append(",\""+_minAge+"\""+",\""+_maxAge+"\"");
        }
    }
}
