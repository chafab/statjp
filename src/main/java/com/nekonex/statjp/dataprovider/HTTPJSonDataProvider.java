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
package com.nekonex.statjp.dataprovider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nekonex.statjp.config.ApplicationConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HTTPJSonDataProvider implements IJSonDataProvider{
    final static Logger logger = LogManager.getLogger(HTTPJSonDataProvider.class);

    private static byte allBuffer[] = new byte[100000000];

    private Random rand = new Random();
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

    private void sleep () throws InterruptedException
    {
        // The estat website mentions to not submit to many requests at the same time
        if (rand.nextInt() % 200 == 0) // Sleep for 20s every 200 requests in average
        {
            Thread.sleep((rand.nextInt() % 50000) + 20000);
        } else if (rand.nextInt() % 10 == 0) //Sleeps 5s every 10 requests in average
        {
            Thread.sleep((rand.nextInt() % 5000) + 5000);
        } else // Sleeps at least 2s in other cases
        {
            Thread.sleep((rand.nextInt() % 2000) + 2000);
        }
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
        String Filename = Filename2.replace(".utf8", "");
        if (!Files.exists(Paths.get(Filename2.replace(".utf8", ""))))
        {
            Files.createDirectories(Paths.get(Filename2.replace(".utf8", "").substring(0,Filename2.lastIndexOf("\\"))));
        }
        else
        {
            if (_appConfig.getUseCacheData())
            {
                try {
                    CachedJSonDataProvider provider = new CachedJSonDataProvider();
                    json = provider.getJSonObject(Filename);
                    if (json != null)
                        return json;
                }
                catch (Exception e) // The data is not in the cache redownload it
                {
                }
            }
        }
        sleep();
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
