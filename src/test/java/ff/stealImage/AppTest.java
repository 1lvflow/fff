package ff.stealImage;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import enums.StealImageTypeEnum;
import lombok.Data;
import utils.FileUtils;
import utils.HttpUtils;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);


    private final String baseImageUrl = "http://www.197tt.com/htm/pic%s/%s.htm";

    private final String basePath = "C:/Users/admin/Desktop/%s.html";
    //文件夹名+文件名
    private final String basePathMac = "/Volumes/外接磁盘/stealImage/%s";

    @Data
    private class ImageMessage {
        private String title;
        private List<String> urls;
    }

    /**
     * 爬取文件
     */
    public void testApp() {
        StealImageTypeEnum imageTypeEnum = StealImageTypeEnum.STEAL_IMAGE;
        String url = null;
        for (int i = imageTypeEnum.getStart(); i <= imageTypeEnum.getEnd(); i++) {
            url = String.format(baseImageUrl, imageTypeEnum.getCode(), i);

            // 解析
            ImageMessage message = getMessage(url);

            // 保存
            getImageByte(message, imageTypeEnum);
        }

    }

    /**
     * 获取图片地址信息
     */
    private ImageMessage getMessage(String url) {

        String content = HttpUtils.get(url);
        if (StringUtils.isBlank(content)) {
            LOGGER.warn("Link错误");
            return null;
        }

        Document doc = Jsoup.parse(content);
        ImageMessage imageMessage = new ImageMessage();

        Elements imgContent = doc.select("img");
        // title
        Element title = doc.select("span.cat_pos_l").first();
        String titleString = title.ownText().replaceAll("»", "").replaceAll(" ", "").trim();
        imageMessage.setTitle(titleString);
        // url
        List<String> urls = new ArrayList<>();
        imgContent.forEach(e -> {
            String imgUrl = e.attr("src");
            urls.add(imgUrl);
            System.out.println(e.attr("src"));
        });
        imageMessage.setUrls(urls);

        return imageMessage;
    }


    private void getImageByte(ImageMessage message, StealImageTypeEnum imageTypeEnum) {
        //

        List<String> imgUrls = message.getUrls();

        imgUrls.forEach((String e) -> {
            // path
            String fileName = e.substring(e.lastIndexOf("/") + 1);//从后往前寻找

            String path = String.format(basePathMac, imageTypeEnum.getDescription() + "/" + message.getTitle() + "/" + fileName);

            try {
                FileUtils.downloadPicture(e, path);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });

    }


}

