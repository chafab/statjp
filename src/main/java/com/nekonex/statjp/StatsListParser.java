package com.nekonex.statjp;

import com.google.gson.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Component
public class StatsListParser {
    final static Logger logger = Logger.getLogger(StatsListParser.class);

    ApplicationConfiguration _appConfig;
    TableParser _parser;

    private String _id;
    private String _additionalParameter;
    class StatsTableEntry
    {
        String entry;
        String date;
    }

    private IJSonDataProvider _provider;
    private static HashMap<String, StatsTableEntry> _mapUpdateDateById = new HashMap<>();

    public StatsListParser(ApplicationConfiguration config, TableParser parser, IJSonDataProvider provider)
    {
        _appConfig = config;
        _parser = parser;
        _provider = provider;
    }

    private static String fmtCsvString(String str, boolean commaAppend)
    {
        str = "\""+str.replace(',',' ');
        if (commaAppend)
            str +="\",";
        return str;
    }

    private void readCSV(String fileName) throws Exception
    {
        //15
        if (Files.exists(Paths.get(fileName))) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF8"));
                for (String str : lines) {
                    String[] array = str.split(",");
                    StatsTableEntry entry = new StatsTableEntry();
                    entry.date = array[15];
                    entry.entry = str;
                    _mapUpdateDateById.put(array[0], entry);
                }
            }
            catch (Exception e)
            {
                logger.error("Cannot read CSV " +fileName);
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void saveFile(String prefix) throws Exception
    {
        try (FileOutputStream fileOutputStream2 = new FileOutputStream("F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix + "\\summary_csv\\summary.csv")) {
            String str = "ID,TITLE,TABLE_CATEGORY,TABLE_NAME,TABLE_EXPLANATION,STAT_NAME,GOV_ORG,STATISTICS_NAME,TABULATION_CATEGORY,TABULATION_SUB_CATEGORY1,TABULATION_SUB_CATEGORY2,MAIN_CATEGORY,SUB_CATEGORY,SURVEY_DATE,OPEN_DATE,UPDATED_DATE\n";
            fileOutputStream2.write(str.getBytes());
            for (StatsTableEntry tabEntry : _mapUpdateDateById.values()) {
                fileOutputStream2.write(tabEntry.entry.getBytes());
            }
        }
    }

    public void GenerateData(String statid, String additinalParameter) throws Exception {
        logger.info("Generating StatLis Data id=["+statid+"] params=["+additinalParameter+"]");
        _id = statid;
        _additionalParameter = additinalParameter;

        JsonObject json = _provider.getJSonObject("https://api.e-stat.go.jp/rest/3.0/app/json/getStatsList?appId=" + _appConfig.getAppID() + "&statsCode=" + _id + _additionalParameter);
        String prefix = "";
        json = json.getAsJsonObject("GET_STATS_LIST");
        if (json.getAsJsonObject("PARAMETER").getAsJsonPrimitive("LANG") != null &&
                json.getAsJsonObject("PARAMETER").getAsJsonPrimitive("LANG").getAsString().equals("E")) {
            prefix = "EN";
        } else {
            prefix = "JP";
        }
        String fileName = _appConfig.getOutputPath() + prefix + "\\.raw.utf8.txt";
        readCSV(_appConfig.getOutputPath() + prefix + "\\summary_csv\\summary.csv");
        try {
            json = json.getAsJsonObject("DATALIST_INF");
            JsonArray array = json.getAsJsonArray("TABLE_INF");
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < array.size(); ++i) {
                buffer.delete(0, buffer.length());
                JsonObject obj = array.get(i).getAsJsonObject();
                String id = obj.get("@id").getAsString();
                buffer.append(fmtCsvString(id, true));
                JsonObject tmp = obj.get("TITLE").isJsonObject() ? obj.getAsJsonObject("TITLE") : null;
                if (tmp == null) {
                    if (obj.get("TITLE") == null)
                        buffer.append(fmtCsvString("", true));
                    else {
                        buffer.append(fmtCsvString(obj.get("TITLE").getAsString(), true));
                    }
                } else {
                    buffer.append(fmtCsvString(tmp.get("$").getAsString(), true));
                }

                tmp = obj.getAsJsonObject("TITLE_SPEC");
                if (tmp.get("TABLE_CATEGORY") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABLE_CATEGORY").getAsString(), true));
                if (tmp.get("TABLE_NAME") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABLE_NAME").getAsString(), true));
                if (tmp.get("TABLE_EXPLANATION") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABLE_EXPLANATION").getAsString(), true));
                tmp = obj.getAsJsonObject("STAT_NAME");
                buffer.append(fmtCsvString(tmp.get("$").getAsString(), true));
                tmp = obj.getAsJsonObject("GOV_ORG");
                buffer.append(fmtCsvString(tmp.get("$").getAsString(), true));
                buffer.append(fmtCsvString(obj.getAsJsonPrimitive("STATISTICS_NAME").getAsString(), true));
                tmp = obj.getAsJsonObject("STATISTICS_NAME_SPEC");
                if (tmp.get("TABULATION_CATEGORY") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABULATION_CATEGORY").getAsString(), true));
                if (tmp.get("TABULATION_SUB_CATEGORY1") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABULATION_SUB_CATEGORY1").getAsString(), true));
                if (tmp.get("TABULATION_SUB_CATEGORY2") == null)
                    buffer.append(fmtCsvString("", true));
                else
                    buffer.append(fmtCsvString(tmp.get("TABULATION_SUB_CATEGORY2").getAsString(), true));
                tmp = obj.getAsJsonObject("MAIN_CATEGORY");
                buffer.append(fmtCsvString(tmp.get("$").getAsString(), true));
                tmp = obj.getAsJsonObject("SUB_CATEGORY");
                buffer.append(fmtCsvString(tmp.get("$").getAsString(), true));
                buffer.append(fmtCsvString(obj.getAsJsonPrimitive("SURVEY_DATE").getAsString(), true));
                buffer.append(fmtCsvString(obj.getAsJsonPrimitive("OPEN_DATE").getAsString(), true));
                String updateDate = obj.getAsJsonPrimitive("UPDATED_DATE").getAsString();
                buffer.append(fmtCsvString(updateDate, false));
                buffer.append("\n");
                if (_mapUpdateDateById.containsKey(id) == false || _mapUpdateDateById.get(id).date != updateDate) {

                    try {
                        _parser.genData(id, _additionalParameter);
                        StatsTableEntry entry = new StatsTableEntry();
                        entry.entry = buffer.toString();
                        entry.date = updateDate;
                        _mapUpdateDateById.put(id, entry);
                    } catch (Exception e) {
                        logger.error("Cannot Generate Data id=["+statid+"] params=["+additinalParameter+"]");
                        logger.error(ExceptionUtils.getStackTrace(e));
                    } finally {
                        Thread.sleep(5000);
                    }
                }
            }
            saveFile(prefix);
        } catch (Exception e) {
            logger.error("Cannot Generate Data id=["+statid+"] params=["+additinalParameter+"]");
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
