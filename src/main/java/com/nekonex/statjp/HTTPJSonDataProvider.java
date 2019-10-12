package com.nekonex.statjp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class HTTPJSonDataProvider implements IJSonDataProvider{
    final static Logger logger = Logger.getLogger(HTTPJSonDataProvider.class);

    private static byte allBuffer[] = new byte[1000000000];
    ApplicationConfiguration _appConfig;
    private static byte[] appendData(byte[] array1, byte[] array2, int index1, int index2)
    {
        byte[] result = array1;
        if (array1.length < index1+index2)
        {
            result = new byte[array1.length*2];
            System.arraycopy(array1,0,result,0,index1);
        }
        System.arraycopy(array2,0,result,index1,index2);
        return result;
    }

    public HTTPJSonDataProvider(ApplicationConfiguration config)
    {
        _appConfig = config;
    }


    public JsonObject getJSonObject(String path) throws Exception{
        logger.info("Downloading data : "+path);
        JsonObject json = null;
        String Filename2 = _appConfig.getOutputPath();
        if (path.contains("lang=E"))
            Filename2 += "\\EN\\";
        else
            Filename2 += "\\JP\\";
        Filename2 += "\\raw\\";
        if (path.contains("/getStatsData?") || path.contains("/getStatsList?")) {

            String str = path.substring(path.indexOf("?") + 1, path.length());
            Filename2 += str;
        }
        Filename2 += ".raw.utf8.txt";
        String Filename = Filename2.replace(".uf8", "");
        if (!Files.exists(Paths.get(Filename2.replace(".uf8", ""))))
        {
            Files.createDirectories(Paths.get(Filename2.replace(".uf8", "").substring(0,Filename2.lastIndexOf("\\"))));
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(path).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(Filename2.replace(".utf8", ""))) {
            int index = 0;
            int bytesRead;

            byte dataBuffer[] = new byte[4096];
            while ((bytesRead = in.read(dataBuffer, 0, 4096)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                allBuffer = appendData(allBuffer, dataBuffer, index, bytesRead);
                index += bytesRead;
            }

            String str = new String(Arrays.copyOfRange(allBuffer, 0, index), "UTF8");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();
            json = parser.parse(str).getAsJsonObject();
            str = gson.toJson(json);
            try (FileOutputStream fileOutputStream2 = new FileOutputStream(Filename2)) {
                fileOutputStream2.write(str.getBytes());
            } catch (IOException e) {
                logger.error("Cannot parse Data "+path);
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (IOException e) {
            logger.error("Cannot parse Data "+path);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return json;
    }
}