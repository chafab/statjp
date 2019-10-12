package com.nekonex.statjp;

import com.google.gson.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TableParser {
    ApplicationConfiguration _appConfig;
    final static Logger logger = Logger.getLogger(StatsListParser.class);

    private String _id;
    private String _additionalParameter;
    private IJSonDataProvider _provider;

    public TableParser(ApplicationConfiguration config, IJSonDataProvider provider)
    {
        _appConfig = config;
        _provider = provider;
    }

    private void parse(JsonObject obj, boolean genHeadersAndOverwrite, String csvFileName) throws Exception{
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
        JsonArray values = dataInf.getAsJsonArray("VALUE");

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
            for (int i = 0; i < values.size(); ++i) {
                buffer.delete(0, buffer.length());
                JsonObject val = values.get(i).getAsJsonObject();
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
        if (next > 0) {
            Thread.sleep(5000);
            genTableData(next, false);
        }
    }
    private void genTableData(long position, boolean genHeadersAndOverwrite) throws Exception
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
        if (json.getAsJsonObject("GET_STATS_DATA").getAsJsonObject("PARAMETER").getAsJsonPrimitive("LANG") != null &&
                json.getAsJsonObject("GET_STATS_DATA").getAsJsonObject("PARAMETER").getAsJsonPrimitive("LANG").getAsString() == "E")
        {
            prefix = "EN";
        }
        else
        {
            prefix = "JP";
        }
        String csvFilename = _appConfig.getOutputPath() + prefix + "\\tables_json\\" + _id + ".csv";


        parse(json, genHeadersAndOverwrite, csvFilename);
    }

    public void genData(String id, String additionalParameter) throws Exception
    {
        logger.info("Generating Table Data id=["+id+"] params=["+additionalParameter+"]");
        _id = id;

        _additionalParameter = additionalParameter;
        genTableData(0, true);
    }
}
