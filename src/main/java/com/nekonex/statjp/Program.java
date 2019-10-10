package com.nekonex.statjp;

import com.google.gson.*;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Program {
    final static Logger logger = Logger.getLogger(Program.class);
    static String _appId;

    private static String[][] _arraySubjectsCodeEN = new String[][]
            {
                    {"", "", ""},
            };
    private static String[][] _arraySubjectsCodeJP = new String[][]
            {
                    {"00020111","民間企業の勤務条件制度等調査","人事院"},
                    {"00020112","国家公務員死因調査","人事院"},
                    {"00020131","国家公務員災害補償統計","人事院"},
                    {"00100105","青少年のインターネット利用環境実態調査","内閣府"},
                    {"00100402","企業行動に関するアンケート調査","内閣府"},
                    {"00100409","国民経済計算","内閣府"},
                    {"00120001","生産・出荷集中度調査","公正取引委員会"},
                    {"00130001","犯罪統計","警察庁"},
                    {"00130002","道路の交通に関する統計","警察庁"},
                    {"00200211","地方公務員給与実態調査","総務省"},
                    {"00200251","地方財政状況調査","総務省"},
                    {"00200351","通信・放送産業動態調査","総務省"},
                    {"00200356","通信利用動向調査","総務省"},
                    {"00200357","情報通信業基本調査","総務省"},
                    {"00200521","国勢調査","総務省"},
                    {"00200522","住宅・土地統計調査","総務省"},
                    {"00200523","住民基本台帳人口移動報告","総務省"},
                    {"00200524","人口推計","総務省"},
                    {"00200531","労働力調査","総務省"},
                    {"00200532","就業構造基本調査","総務省"},
                    {"00200533","社会生活基本調査","総務省"},
                    {"00200541","個人企業経済調査","総務省"},
                    {"00200543","科学技術研究調査","総務省"},
                    {"00200544","サービス産業動向調査","総務省"},
                    {"00200545","サービス業基本調査","総務省"},
                    {"00200551","事業所・企業統計調査","総務省"},
                    {"00200552","経済センサス－基礎調査","総務省"},
                    {"00200553","経済センサス－活動調査","総務省"},
                    {"00200561","家計調査","総務省"},
                    {"00200563","貯蓄動向調査","総務省"},
                    {"00200564","全国消費実態調査","総務省"},
                    {"00200565","家計消費状況調査","総務省"},
                    {"00200566","全国単身世帯収支実態調査","総務省"},
                    {"00200571","小売物価統計調査","総務省"},
                    {"00200572","全国物価統計調査","総務省"},
                    {"00200573","消費者物価指数","総務省"},
                    {"00200603","産業連関表","総務省"},
                    {"00200604","特別データ公表基準（SDDS）プラス","総務省"},
                    {"00200511","地域メッシュ統計","総務省"},
                    {"00200502","社会・人口統計体系（都道府県・市区町村のすがた）","総務省"},
                    {"00201001","公害苦情調査","公害等調整委員会"},
                    {"00202001","火災統計","消防庁"},
                    {"00202002","消防年報","消防庁"},
                    {"00250001","訟務事件統計","法務省"},
                    {"00250002","登記統計","法務省"},
                    {"00250003","検察統計調査","法務省"},
                    {"00250004","婦人補導統計調査","法務省"},
                    {"00250005","矯正統計調査","法務省"},
                    {"00250006","少年矯正統計調査","法務省"},
                    {"00250007","保護統計調査","法務省"},
                    {"00250008","戸籍統計","法務省"},
                    {"00250009","供託統計","法務省"},
                    {"00250010","人権侵犯事件統計","法務省"},
                    {"00250011","出入国管理統計","法務省"},
                    {"00250012","在留外国人統計（旧登録外国人統計）","法務省"},
                    {"00300100","海外在留邦人数調査統計","外務省"},
                    {"00300400","旅券統計","外務省"},
                    {"00300500","ビザ（査証）発給統計","外務省"},
                    {"00350300","普通貿易統計","財務省"},
                    {"00350310","特殊貿易統計","財務省"},
                    {"00350600","法人企業統計調査","財務省"},
                    {"00350610","法人企業景気予測調査","財務省"},
                    {"00350620","景気予測調査","財務省"},
                    {"00351000","民間給与実態統計調査","国税庁"},
                    {"00351020","会社標本調査","国税庁"},
                    {"00400001","学校基本調査","文部科学省"},
                    {"00400002","学校保健統計調査","文部科学省"},
                    {"00400003","学校教員統計調査","文部科学省"},
                    {"00400004","社会教育調査","文部科学省"},
                    {"00400202","地方教育費調査","文部科学省"},
                    {"00400301","教育職員に係る係争中の争訟事件等の調査","文部科学省"},
                    {"00400302","教職員の組織する職員団体の実態調査","文部科学省"},
                    {"00400306","学校における教育の情報化の実態等に関する調査","文部科学省"},
                    {"00400402","大学・短期大学・高等専門学校及び専修学校卒業予定者の就職内定状況等調査","文部科学省"},
                    {"00400501","民間企業の研究活動に関する調査","文部科学省"},
                    {"00401101","宗教統計調査","文化庁"},
                    {"00402102","体力・運動能力調査","スポーツ庁"},
                    {"00450011","人口動態調査","厚生労働省"},
                    {"00450012","生命表","厚生労働省"},
                    {"00450021","医療施設調査","厚生労働省"},
                    {"00450022","患者調査","厚生労働省"},
                    {"00450061","国民生活基礎調査","厚生労働省"},
                    {"00450071","毎月勤労統計調査","厚生労働省"},
                    {"00450091","賃金構造基本統計調査","厚生労働省"},
                    {"00450099","就労条件総合調査","厚生労働省"},
                    {"00450151","薬事工業生産動態統計調査","厚生労働省"},
                    {"00450152","医薬品・医療機器産業実態調査","厚生労働省"},
                    {"00450171","国民健康・栄養調査","厚生労働省"},
                    {"00450211","定期健康診断結果報告","厚生労働省"},
                    {"00450389","医療給付実態調査","厚生労働省"},
                    {"00450399","「医療費の動向」調査","厚生労働省"},
                    {"00450437","社会保障費用統計","厚生労働省"},
                    {"00452001","賃金事情等総合調査","中央労働委員会"},
                    {"00500000","産業連関構造調査","農林水産省"},
                    {"00500001","農業・食料関連産業の経済計算","農林水産省"},
                    {"00500100","農林水産物輸出入統計","農林水産省"},
                    {"00500201","農業経営統計調査","農林水産省"},
                    {"00500202","林業経営統計調査","農林水産省"},
                    {"00500203","漁業経営調査","農林水産省"},
                    {"00500204","農業物価統計調査","農林水産省"},
                    {"00500206","生産農業所得統計","農林水産省"},
                    {"00500207","林業産出額","農林水産省"},
                    {"00500208","漁業産出額","農林水産省"},
                    {"00500209","農林業センサス","農林水産省"},
                    {"00500210","漁業センサス","農林水産省"},
                    {"00500211","農業構造動態調査","農林水産省"},
                    {"00500213","漁業就業動向調査","農林水産省"},
                    {"00500215","作物統計調査","農林水産省"},
                    {"00500216","海面漁業生産統計調査","農林水産省"},
                    {"00500217","木材統計調査","農林水産省"},
                    {"00500219","木材流通統計調査","農林水産省"},
                    {"00500222","畜産統計調査","農林水産省"},
                    {"00500225","牛乳乳製品統計調査","農林水産省"},
                    {"00500226","青果物卸売市場調査","農林水産省"},
                    {"00500227","畜産物流通調査","農林水産省"},
                    {"00500228","水産物流通調査","農林水産省"},
                    {"00500231","食品循環資源の再生利用等実態調査","農林水産省"},
                    {"00500232","食品流通段階別価格形成調査","農林水産省"},
                    {"00500235","生鮮野菜価格動向調査","農林水産省"},
                    {"00500236","新規就農者調査","農林水産省"},
                    {"00500238","集落営農実態調査","農林水産省"},
                    {"00500244","生産者の米穀在庫等調査","農林水産省"},
                    {"00500246","農道整備状況調査","農林水産省"},
                    {"00500247","６次産業化総合調査","農林水産省"},
                    {"00500248","野生鳥獣資源利用実態調査","農林水産省"},
                    {"00500249","市町村別農業産出額（推計）","農林水産省"},
                    {"00500300","食料需給表","農林水産省"},
                    {"00500301","容器包装利用・製造等実態調査","農林水産省"},
                    {"00500302","食品産業企業設備投資動向調査","農林水産省"},
                    {"00500304","油糧生産実績調査","農林水産省"},
                    {"00500311","食品製造業におけるHACCPの導入状況実態調査","農林水産省"},
                    {"00500500","土壌改良資材の生産量及び輸入量調査","農林水産省"},
                    {"00500501","地域特産野菜生産状況調査","農林水産省"},
                    {"00500508","牛乳乳製品の生産動向","農林水産省"},
                    {"00500509","チーズの需給表","農林水産省"},
                    {"00500600","農業経営改善計画の営農類型別認定状況","農林水産省"},
                    {"00500602","農業協同組合及び同連合会一斉調査","農林水産省"},
                    {"00500603","農業協同組合等現在数統計","農林水産省"},
                    {"00500701","中山間地域等直接支払制度の実施状況","農林水産省"},
                    {"00501000","木材需給表","林野庁"},
                    {"00501004","特用林産物生産統計調査","林野庁"},
                    {"00501005","国有林野事業統計書","林野庁"},
                    {"00501008","木質バイオマスエネルギー利用動向調査","林野庁"},
                    {"00502005","水産物流通調査","水産庁"},
                    {"00550010","工業統計調査","経済産業省"},
                    {"00550020","商業統計調査","経済産業省"},
                    {"00550030","商業動態統計調査","経済産業省"},
                    {"00550040","特定サービス産業実態調査","経済産業省"},
                    {"00550100","経済産業省企業活動基本調査","経済産業省"},
                    {"00550110","外資系企業動向調査","経済産業省"},
                    {"00550200","経済産業省生産動態統計調査","経済産業省"},
                    {"00550210","経済産業省特定業種石油等消費動態統計調査","経済産業省"},
                    {"00550300","鉱工業生産・出荷・在庫指数","経済産業省"},
                    {"00550320","製造工業生産能力・稼働率指数","経済産業省"},
                    {"00550580","機能性化学品動向調査","経済産業省"},
                    {"00550590","バイオ産業創造基礎調査","経済産業省"},
                    {"00551005","エネルギー消費統計調査","資源エネルギー庁"},
                    {"00551020","石油製品需給動態統計調査","資源エネルギー庁"},
                    {"00551040","石油設備調査","資源エネルギー庁"},
                    {"00551110","電力需要調査","資源エネルギー庁"},
                    {"00551130","ガス事業生産動態統計調査","資源エネルギー庁"},
                    {"00552010","知的財産活動調査","特許庁"},
                    {"00553010","中小企業実態基本調査","中小企業庁"},
                    {"00600120","建築着工統計調査","国土交通省"},
                    {"00600130","建設工事統計調査","国土交通省"},
                    {"00600140","建設関連業等の動態統計調査","国土交通省"},
                    {"00600150","建設業活動実態調査","国土交通省"},
                    {"00600280","港湾調査","国土交通省"},
                    {"00600300","造船造機統計調査","国土交通省"},
                    {"00600310","鉄道車両等生産動態統計調査","国土交通省"},
                    {"00600320","船員労働統計調査","国土交通省"},
                    {"00600330","自動車輸送統計調査","国土交通省"},
                    {"00600340","内航船舶輸送統計調査","国土交通省"},
                    {"00600360","航空輸送統計","国土交通省"},
                    {"00600370","自動車燃料消費量調査","国土交通省"},
                    {"00600470","法人土地・建物基本調査","国土交通省"},
                    {"00600480","法人建物調査","国土交通省"},
                    {"00600590","水害統計調査","国土交通省"},
                    {"00600630","住宅市場動向調査","国土交通省"},
                    {"00600640","空家実態調査","国土交通省"},
                    {"00600650","住生活総合調査","国土交通省"},
                    {"00600880","設備工事業に係る受注高調査","国土交通省"},
                    {"00601010","旅行・観光消費動向調査","観光庁"},
                    {"00601020","宿泊旅行統計調査","観光庁"},
                    {"00601030","訪日外国人消費動向調査","観光庁"},
                    {"00601040","観光地域経済調査","観光庁"},
                    {"00650102","産業廃棄物排出・処理状況調査","環境省"},
                    {"00650203","水質汚濁物質排出量総合調査","環境省"},
                    {"00650204","環境経済観測調査","環境省"},
                    {"00650401","家庭からの二酸化炭素排出量の推計に係る実態調査　試験調査","環境省"},
                    {"00650405","食品廃棄物等の発生抑制及び再生利用の促進の取組に係る実態調査","環境省"},
                    {"00700001","駐留軍関係離職者帰すう状況調査","防衛省"},
                    {"","",""}
            };
    private StatsListParser _parser;
    private String _jpStatListFilename;
    private String _enStatListFilename;
    Program(StatsListParser parser, String jpStatListFileName, String enStatListFileName)
    {
        _parser = parser;
        _jpStatListFilename = jpStatListFileName;
        _enStatListFilename = enStatListFileName;
    }

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
    public static void GenerateData(String[][] arraySubjectsCode, String prefix, String additinalParameter)
    {
        for (String[] codes: arraySubjectsCode) {
            String Filename ="F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix+"\\"+codes[0]+".raw.txt";
            byte allBuffer[] = new byte[1000000000];
            if (new File(Filename).exists() == false) {

                try (BufferedInputStream in = new BufferedInputStream(new URL("https://api.e-stat.go.jp/rest/3.0/app/json/getStatsList?appId=" + _appId + "&statsCode="+codes[0]+additinalParameter).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(Filename)){
                    int index = 0;
                    int bytesRead;

                    byte dataBuffer[] = new byte[4096];
                    while ((bytesRead = in.read(dataBuffer, 0, 4096)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        allBuffer = appendData(allBuffer,dataBuffer, index, bytesRead);
                        index+=bytesRead;
                    }
                    String Filename2 ="F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix + "\\" + codes[0]+".raw.utf8.txt";
                    String str = new String(Arrays.copyOfRange(allBuffer,0,index), "UTF8");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(str).getAsJsonObject();
                    str = gson.toJson(json);
                    try (FileOutputStream fileOutputStream2 = new FileOutputStream(Filename2))
                    {
                        fileOutputStream2.write(str.getBytes());
                    }
                    catch (IOException e){
                        // handle exception
                    }
                }
                catch (IOException e){
                    // handle exception
                }
            }
        }
    }

    public static String fmtCsvString(String str, boolean commaAppend)
    {
        str = str.replace(',',' ');
        if (commaAppend)
            str +=",";
        return str;
    }

    public static  void generateTable(String id, String prefix, String additinalParameter)
    {
        String Filename ="F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix+"\\tables_json\\"+id+".raw.txt";
        byte allBuffer[] = new byte[1000000000];
        if (new File(Filename).exists() == false) {

            try (BufferedInputStream in = new BufferedInputStream(new URL("https://api.e-stat.go.jp/rest/3.0/app/json/getStatsData?startPosition=1&statsDataId="+id+"&appId=" + _appId+ additinalParameter).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(Filename)){
                int index = 0;
                int bytesRead;

                byte dataBuffer[] = new byte[4096];
                while ((bytesRead = in.read(dataBuffer, 0, 4096)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    allBuffer = appendData(allBuffer,dataBuffer, index, bytesRead);
                    index+=bytesRead;
                }
                String Filename2 ="F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix + "\\tables_json\\" + "\\" + id+".raw.utf8.txt";
                String str = new String(Arrays.copyOfRange(allBuffer,0,index), "UTF8");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(str).getAsJsonObject();
                str = gson.toJson(json);
                try (FileOutputStream fileOutputStream2 = new FileOutputStream(Filename2))
                {
                    fileOutputStream2.write(str.getBytes());
                }
                catch (IOException e){
                    // handle exception
                }
            }
            catch (IOException e){
                // handle exception
            }
        }
    }

    public static void generateSummaryCsv(String prefix, String additinalParameter)
    {
        String fileName = "F:\\OneDrive\\Projects\\java\\estatjapan\\data\\"+prefix+"\\.raw.utf8.txt";

        try {
            //URI uri = this.getClass().getResource(fileName).toURI();
            List<String> lines = Files.readAllLines(Paths.get(fileName),
                    Charset.forName("UTF8"));
            StringBuffer sBuf =new StringBuffer();
            for (String str: lines) {
                sBuf.append(str);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(sBuf.toString()).getAsJsonObject();
            json = json.getAsJsonObject("GET_STATS_LIST");
            json = json.getAsJsonObject("DATALIST_INF");
            JsonArray array = json.getAsJsonArray("TABLE_INF");
            try (FileOutputStream fileOutputStream2 = new FileOutputStream("F:\\OneDrive\\Projects\\java\\estatjapan\\data\\"+prefix+"\\summary_csv\\summary.csv"))
            {
                String str = "TITLE,TABLE_CATEGORY,TABLE_NAME,TABLE_EXPLANATION,STAT_NAME,GOV_ORG,STATISTICS_NAME,TABULATION_CATEGORY,TABULATION_SUB_CATEGORY1,TABULATION_SUB_CATEGORY2,MAIN_CATEGORY,SUB_CATEGORY,SURVEY_DATE,OPEN_DATE,UPDATED_DATE\n";
                fileOutputStream2.write(str.getBytes());
                for (int i = 0; i < array.size(); ++i) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    //generateTable(obj.get("@id").getAsString(), prefix,  additinalParameter);
                    JsonObject tmp = obj.get("TITLE").isJsonObject() ? obj.getAsJsonObject("TITLE") : null;
                    if (tmp == null) {
                        if (obj.get("TITLE") == null)
                            fileOutputStream2.write(fmtCsvString("", true).getBytes());
                        else {
                            fileOutputStream2.write(fmtCsvString(obj.get("TITLE").getAsString(), true).getBytes());
                        }
                    }
                    else {
                        fileOutputStream2.write(fmtCsvString(tmp.get("$").getAsString(), true).getBytes());
                    }

                    tmp = obj.getAsJsonObject("TITLE_SPEC");
                    if (tmp.get("TABLE_CATEGORY") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABLE_CATEGORY").getAsString(), true).getBytes());
                    if (tmp.get("TABLE_NAME") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABLE_NAME").getAsString(), true).getBytes());
                    if (tmp.get("TABLE_EXPLANATION") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABLE_EXPLANATION").getAsString(), true).getBytes());
                    tmp = obj.getAsJsonObject("STAT_NAME");
                    fileOutputStream2.write(fmtCsvString(tmp.get("$").getAsString(), true).getBytes());
                    tmp = obj.getAsJsonObject("GOV_ORG");
                    fileOutputStream2.write(fmtCsvString(tmp.get("$").getAsString(), true).getBytes());
                    fileOutputStream2.write(fmtCsvString(obj.getAsJsonPrimitive("STATISTICS_NAME").getAsString(), true).getBytes());
                    tmp = obj.getAsJsonObject("STATISTICS_NAME_SPEC");
                    if (tmp.get("TABULATION_CATEGORY") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABULATION_CATEGORY").getAsString(), true).getBytes());
                    if (tmp.get("TABULATION_SUB_CATEGORY1") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABULATION_SUB_CATEGORY1").getAsString(), true).getBytes());
                    if (tmp.get("TABULATION_SUB_CATEGORY2") == null)
                        fileOutputStream2.write(fmtCsvString("", true).getBytes());
                    else
                        fileOutputStream2.write(fmtCsvString(tmp.get("TABULATION_SUB_CATEGORY2").getAsString(), true).getBytes());
                    tmp = obj.getAsJsonObject("MAIN_CATEGORY");
                    fileOutputStream2.write(fmtCsvString(tmp.get("$").getAsString(), true).getBytes());
                    tmp = obj.getAsJsonObject("SUB_CATEGORY");
                    fileOutputStream2.write(fmtCsvString(tmp.get("$").getAsString(), true).getBytes());
                    fileOutputStream2.write(fmtCsvString(obj.getAsJsonPrimitive("SURVEY_DATE").getAsString(), true).getBytes());
                    fileOutputStream2.write(fmtCsvString(obj.getAsJsonPrimitive("OPEN_DATE").getAsString(), true).getBytes());
                    fileOutputStream2.write(fmtCsvString(obj.getAsJsonPrimitive("UPDATED_DATE").getAsString(), false).getBytes());
                    fileOutputStream2.write("\n".getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void GenerateDataCatalog(String prefix, String additinalParameter) {

        String Filename = "F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix + "\\datacatalog_json\\all" + ".raw.txt";
        byte allBuffer[] = new byte[1000000000];
        if (new File(Filename).exists() == false) {

            try (BufferedInputStream in = new BufferedInputStream(new URL("https://api.e-stat.go.jp/rest/3.0/app/json/getDataCatalog?appId=" + _appId + additinalParameter).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(Filename)) {
                int index = 0;
                int bytesRead;

                byte dataBuffer[] = new byte[4096];
                while ((bytesRead = in.read(dataBuffer, 0, 4096)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    allBuffer = appendData(allBuffer, dataBuffer, index, bytesRead);
                    index += bytesRead;
                }
                String Filename2 = "F:\\OneDrive\\Projects\\java\\estatjapan\\data\\" + prefix + "\\datacatalog_json\\all.raw.utf8.txt";
                String str = new String(Arrays.copyOfRange(allBuffer, 0, index), "UTF8");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(str).getAsJsonObject();
                str = gson.toJson(json);
                try (FileOutputStream fileOutputStream2 = new FileOutputStream(Filename2)) {
                    fileOutputStream2.write(str.getBytes());
                } catch (IOException e) {
                    // handle exception
                }
            } catch (IOException e) {
                // handle exception
            }
        }

    }

    public void start(ClassPathXmlApplicationContext context) throws Exception
    {
        logger.info("Start");
        _parser.GenerateData("","&lang=E");
    }




    public static void main(String[] args) throws Exception  {
        //GenerateData(_arraySubjectsCodeJP,"JP","");
        //GenerateData(_arraySubjectsCodeEN, "EN","&lang=E");
        //GenerateDataCatalog("JP","");
        //GenerateDataCatalog("EN", "&lang=E");
        //generateSummaryCsv("JP");
        //generateSummaryCsv("EN", "&lang=E");
        //new TableParser("0000010101", _appId, "&lang=E");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "file:config/applicationContext.xml");
        Program app = context.getBean(Program.class);

        app.start(context);
    }
}
