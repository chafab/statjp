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
