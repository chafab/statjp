package com.nekonex.statjp;

import com.google.gson.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {
    final static Logger logger = Logger.getLogger(Program.class);
    static String _appId;
    private StatsListParser _parser;
    private String _jpStatListFilename;
    private String _enStatListFilename;

    Program(StatsListParser parser, String jpStatListFileName, String enStatListFileName)
    {
        _parser = parser;
        _jpStatListFilename = jpStatListFileName;
        _enStatListFilename = enStatListFileName;
    }

    private ArrayList<String> readCSV(String fileName) throws Exception
    {
        logger.info("loading csv file " +fileName);
        ArrayList<String> result = new ArrayList<>();
        if (Files.exists(Paths.get(fileName))) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF8"));
                int numLines = 0;
                for (String str : lines) {
                    ++numLines;
                    if (numLines == 1)
                        continue;
                    String[] array = str.split(",");
                    if (array.length != 3)
                    {
                        logger.error("invalid number of columns at line "+numLines);
                    }
                    result.add(array[0]);
                }
            }
            catch (Exception e)
            {
                logger.error("Cannot read CSV " +fileName);
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return result;
    }

    public void start(ClassPathXmlApplicationContext context) throws Exception
    {
        logger.info("Start");
        ArrayList<String> statsList = readCSV(_jpStatListFilename);
        for (String str : statsList)
        {
            _parser.GenerateData(str,"");
        }
        statsList = readCSV(_enStatListFilename);
        for (String str : statsList) {
            _parser.GenerateData(str, "&lang=E");
        }
    }

    public static void main(String[] args) throws Exception  {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "file:config/applicationContext.xml");
        Program app = context.getBean(Program.class);
        app.start(context);
    }
}
