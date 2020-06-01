package com.nekonex.statjp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CachedJSonDataProvider implements IJSonDataProvider {
    final static Logger logger = LogManager.getLogger(CachedJSonDataProvider.class);

    public JsonObject getJSonObject(String path) throws Exception {
        if (path.isEmpty())
            throw new Exception("The path provided is empty");
        String cachedData;
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), Charset.forName("UTF8"));
            StringBuffer sBuf = new StringBuffer();
            for (String str : lines) {
                sBuf.append(str);
            }
            cachedData = sBuf.toString();
        }
        catch (Exception e)
        {
            logger.error("Cannot parse Data "+path);
            logger.error(ExceptionUtils.getStackTrace(e));
            throw  e;
        }
        JsonParser parser = new JsonParser();
        return parser.parse(cachedData).getAsJsonObject();
    }
}
