package com.nekonex.statjp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class DummyJSonDataProvider implements IJSonDataProvider {
    final static Logger logger = Logger.getLogger(DummyJSonDataProvider.class);

    Queue<String> _contentQueue = new ArrayDeque<>();

    public DummyJSonDataProvider(String path) {
        //For test only
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), Charset.forName("UTF8"));
            StringBuffer sBuf = new StringBuffer();
            for (String str : lines) {
                sBuf.append(str);
            }
            this.enqueueContent(sBuf.toString());
        }
        catch (Exception e)
        {
            logger.error("Cannot parse Data "+path);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void enqueueContent(String val) {
        _contentQueue.add(val);
    }
    public JsonObject getJSonObject(String path) throws Exception {
        if (_contentQueue.isEmpty())
            throw new Exception("Queue is empty");
        String content = _contentQueue.poll();
        JsonParser parser = new JsonParser();
        return parser.parse(content).getAsJsonObject();
    }
}
