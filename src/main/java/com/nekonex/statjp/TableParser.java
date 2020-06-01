package com.nekonex.statjp;

import com.google.gson.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TableParser {
    ApplicationConfiguration _appConfig;
    final static Logger logger = LogManager.getLogger(StatsListParser.class);

    private String _id;
    private String _additionalParameter;
    private IJSonDataProvider _provider;

    public TableParser(ApplicationConfiguration config, IJSonDataProvider provider)
    {
        _appConfig = config;
        _provider = provider;
    }

    private long parse(JsonObject obj, boolean genHeadersAndOverwrite, String csvFileName) throws Exception{
        obj = obj.getAsJsonObject("GET_STATS_DATA");
        obj = obj.getAsJsonObject("STATISTICAL_DATA");
        long next = -1;
        JsonObject resInfo = obj.getAsJsonObject("RESULT_INF");
        if (resInfo.getAsJsonPrimitive("NEXT_KEY") != null)
            next = resInfo.getAsJsonPrimitive("NEXT_KEY").getAsLong();
        //Unused
        //JsonObject tableInf = obj.getAsJsonObject("TABLE_INF");
        JsonObject classes = obj.getAsJsonObject("CLASS_INF");
        JsonArray classArray = classes.getAsJsonArray("CLASS_OBJ");
        ArrayList<ITableInfoParser> categories = new ArrayList<>();
        for (int i = 0; i < classArray.size(); ++i) {
            TableCategory category = new TableCategory(classArray.get(i).getAsJsonObject());
            categories.add(category);
        }
        categories.add(new TableSimpleValue("@unit","unit"));
        categories.add(new TableSimpleValue("$","value"));

        JsonObject dataInf = obj.getAsJsonObject("DATA_INF");

        if (!Files.exists(Paths.get(csvFileName)))
        {
            Files.createDirectories(Paths.get(csvFileName.substring(0,csvFileName.lastIndexOf("\\"))));
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(csvFileName, !genHeadersAndOverwrite)) {
            StringBuffer buffer = new StringBuffer();
            if (genHeadersAndOverwrite) {
                for (ITableInfoParser parser : categories)
                    parser.buildCsv(buffer);
                buffer.append("\n");
                fileOutputStream.write(buffer.toString().getBytes());
            }
            boolean isUnitMissing = false;
            if (dataInf.get("VALUE").isJsonArray() == true)
            {
                JsonArray values = dataInf.getAsJsonArray("VALUE");
                for (int i = 0; i < values.size(); ++i) {
                    buffer.delete(0, buffer.length());
                    JsonObject val = values.get(i).getAsJsonObject();
                    if (val.get("@unit") == null)
                    {
                        val.addProperty("@unit","");
                        isUnitMissing = true;
                    }
                    if (val.keySet().size() != categories.size())
                        throw new Exception("KeySet and Categories doesn't match");
                    for (ITableInfoParser parser : categories)
                    {
                        parser.parse(val, buffer);
                    }
                    buffer.append("\n");
                    fileOutputStream.write(buffer.toString().getBytes());
                }
            }
            else
            {
                JsonObject val = dataInf.getAsJsonObject("VALUE");
                    buffer.delete(0, buffer.length());
                if (val.get("@unit") == null)
                {
                    isUnitMissing = true;
                    val.addProperty("@unit","");
                }
                if (val.keySet().size() != categories.size())
                    throw new Exception("KeySet and Categories doesn't match");
                for (ITableInfoParser parser : categories)
                {
                    parser.parse(val, buffer);
                }
                buffer.append("\n");
                fileOutputStream.write(buffer.toString().getBytes());
            }
            if (isUnitMissing)
            {
                logger.warn("unit is missing");
            }

        }
        return next;
    }
    private long genTableData(long position, boolean genHeadersAndOverwrite) throws Exception
    {
        logger.info("Generating Table Data id=["+_id+"] params=["+_additionalParameter+"]"+" position=["+position+"]");
        JsonObject json = null;
        boolean generated=false;
        try {
            json = _provider.getJSonObject("https://api.e-stat.go.jp/rest/3.0/app/json/getStatsData?statsDataId="+_id+"&appId=" + _appConfig.getAppID()+ _additionalParameter+"&startPosition="+position);
        }
        catch (IOException e){
            logger.error("Cannot Generate Table Data pos=["+position+"] headers=["+genHeadersAndOverwrite+"] id=["+_id+"] params=["+_additionalParameter+"]");
            logger.error(ExceptionUtils.getStackTrace(e));
            // handle exception
        }
        String prefix = "";
        JsonPrimitive lang = json.getAsJsonObject("GET_STATS_DATA").getAsJsonObject("PARAMETER").getAsJsonPrimitive("LANG");
        if (lang!= null &&
                lang.toString().replace("\"","").equals("E"))
        {
            prefix = "EN";
        }
        else
        {
            prefix = "JP";
        }
        String csvFilename = _appConfig.getOutputPath() + prefix + "\\tables_json\\" + _id + ".csv";


        return parse(json, genHeadersAndOverwrite, csvFilename);
    }

    public void genData(String id, String additionalParameter) throws Exception
    {
        logger.info("Generating Table Data id=["+id+"] params=["+additionalParameter+"]");
        _id = id;

        _additionalParameter = additionalParameter;
        boolean genHeaders = true;

        long next = genTableData(0, true);
        while (next > 0)
        {
            next = genTableData(next, false);
        }
    }
}
