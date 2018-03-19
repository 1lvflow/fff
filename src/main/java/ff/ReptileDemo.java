package ff;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

/**
 * Created by wangjinliang on 2018/2/2.
 */
public class ReptileDemo {
    private static String webDriver = "webdriver.chrome.driver";
    private static String webDriverPath = "/Users/xmac/Desktop/chromedriver";
    private static String targetPath = "https://mp.weixin.qq.com";
    private static String searchPath = "https://mp.weixin.qq.com/cgi-bin/searchbiz";
    private static String appmsgPath = "https://mp.weixin.qq.com/cgi-bin/appmsg";
    private static Random random = new Random(1);
    private static Gson gson = new Gson();

    private static String sourceName = "渔愉鱼";   // 要爬的公众号名称(准确名称)

    private static String username = null;
    private static String password = null;

    static {
        ResourceBundle rb = ResourceBundle.getBundle("reptile");
        username = rb.getString("reptile.username");
        password = rb.getString("reptile.password");
    }

    public static void main(String[] args) {
        System.setProperty(webDriver, webDriverPath);
        WebDriver driver = null;
        try {
            driver = new ChromeDriver();
            weixinLogin(driver);
            String token = getToken(driver);
            Platform platform = getPlatform(driver, token);
            if (Objects.isNull(platform)) {
                throw new Exception("不存在" + sourceName + "公众号");
            }
            int begin = 510;
            while (true) {
                System.out.println("begin:" + begin);
                InfoResult infoResult = getInfoResult(driver, token, platform.getFakeId(), begin, 5);
                if (infoResult.getAppMsgList() == null || infoResult.getAppMsgList().size() == 0) {
                    break;
                }
                insertData(infoResult);
                Thread.sleep(5000);
                begin = begin + 5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(driver)) {
//                driver.close();
            }
        }
    }

    private static void insertData(InfoResult infoResult) throws Exception {
        List<Info> appMsgList = infoResult.getAppMsgList();
        for (int index = 0; index < appMsgList.size(); index++) {
            Info info = appMsgList.get(index);
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("auth", "P$H^qIwehLqYxze");
            jsonObj.addProperty("cover", info.getCover());
            jsonObj.addProperty("link", info.getLink());
            jsonObj.addProperty("title", info.getTitle());
            HttpUtils.sendPost("http://api.test.yudada.com/pendingInfo", jsonObj.toString());
        }
    }

    private static InfoResult getInfoResult(WebDriver driver, String token, String fakeId,
                                            Integer begin, Integer count) throws Exception {
        Map<String, String> queryInfoParams = new HashMap<>();
        queryInfoParams.put("token", token);
        queryInfoParams.put("lang", "zh_CN");
        queryInfoParams.put("f", "json");
        queryInfoParams.put("ajax", "1");
        queryInfoParams.put("random", random.nextDouble() + "");
        queryInfoParams.put("action", "list_ex");
        queryInfoParams.put("query", "");
        queryInfoParams.put("type", "9");
        queryInfoParams.put("fakeid", fakeId);

        queryInfoParams.put("begin", begin + "");
        queryInfoParams.put("count", count + "");

        String ss = setParams(appmsgPath, queryInfoParams);
        driver.get(ss);
        Document infoDocument = Jsoup.parse(driver.getPageSource());
        Elements infoList = infoDocument.select("pre");
        if (Objects.isNull(infoList)) {
            throw new Exception("获取公众号文章错误");
        }
        return gson.fromJson(infoList.text(), InfoResult.class);
    }

    private static Platform getPlatform(WebDriver driver, String token) throws Exception {
        Map<String, String> searchNameParams = new HashMap<>();
        searchNameParams.put("action", "search_biz");
        searchNameParams.put("token", token);
        searchNameParams.put("lang", "zh_CN");
        searchNameParams.put("f", "json");
        searchNameParams.put("ajax", "1");
        searchNameParams.put("random", random.nextDouble() + "");
        searchNameParams.put("query", sourceName);
        searchNameParams.put("begin", "0");
        searchNameParams.put("count", "5");

        searchPath = setParams(searchPath, searchNameParams);
        driver.get(searchPath);

        Document preDocument = Jsoup.parse(driver.getPageSource());
        Elements preList = preDocument.select("pre");
        if (Objects.isNull(preList)) {
            throw new Exception("获取公众号错误");
        }
        PlatformResult result = gson.fromJson(preList.text(), PlatformResult.class);

        Platform platform = null;
        for (int index = 0; index < result.getList().size(); index++) {
            Platform item = result.getList().get(index);
            if (sourceName.equals(item.getNickname())) {
                platform = item;
            }
        }
        return platform;
    }

    private static String getToken(WebDriver driver) throws Exception {
        String current = driver.getCurrentUrl();
        if (StringUtils.isBlank(current)) {
            throw new Exception("获取token链接有误");
        }
        String token = current.split("token=")[1];
        if (StringUtils.isBlank(token)) {
            throw new Exception("token错误");
        }
        return token;
    }

    private static void weixinLogin(WebDriver driver) throws Exception {
        driver.get(targetPath);
        WebElement usernameWebElement = driver.findElement(By.name("account"));
        usernameWebElement.clear();
        usernameWebElement.sendKeys(username);

        WebElement passwordWebElement = driver.findElement(By.name("password"));
        passwordWebElement.clear();
        passwordWebElement.sendKeys(password);

        WebElement helpWebElement = driver.findElement(By.className("icon_checkbox"));
        helpWebElement.click();

        WebElement btnWebElement = driver.findElement(By.className("btn_login"));
        btnWebElement.click();

        System.out.println("请用手机微信扫码二维码登录公众号");
        Thread.sleep(15000);
    }

    public static String setParams(String address, Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder(address);
            sb.append("?");
            for (String key : params.keySet()) {
                sb.append(key + "=" + params.get(key));
                sb.append("&");
            }
            String origin = sb.toString();
            return origin.substring(0, origin.length() - 1);
        }
        return address;
    }
}

