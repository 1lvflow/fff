package ff.stealImage;

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
public class AppTest  {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);


    private static final String baseImageUrl = "http://www.197tt.com/htm/pic%s/%s.htm";

    private final String basePath = "C:/Users/admin/Desktop/%s.html";
    //文件夹名+文件名
    private static final String basePathMac = "/Volumes/外接磁盘/stealImage/%s";

    @Data
    private static class ImageMessage {
        private String title;
        private List<String> urls;
    }

    public static void main(String[] args) {



        StealImageTypeEnum imageTypeEnum = StealImageTypeEnum.STEAL_IMAGE;
        int x = (imageTypeEnum.getEnd()-imageTypeEnum.getStart()) / 5;

        new Thread(() -> {
            for (int i = imageTypeEnum.getStart(); i <= imageTypeEnum.getStart() + x; i++) {
                downloadOne(imageTypeEnum, i);
            }
        }).start();
        new Thread(()->{
            for (int i = imageTypeEnum.getStart()+x; i <= imageTypeEnum.getStart()+x*2; i++) {
                downloadOne(imageTypeEnum, i);
            }

        }).start();
        new Thread(()->{
            for (int i = imageTypeEnum.getStart()+x*2; i <= imageTypeEnum.getStart()+x*3; i++) {
                downloadOne(imageTypeEnum, i);
            }

        }).start();
        new Thread(()->{
            for (int i =imageTypeEnum.getStart()+x*3; i <= imageTypeEnum.getStart()+x*4; i++) {
                downloadOne(imageTypeEnum, i);
            }

        }).start();
        new Thread(()->{
            for (int i = imageTypeEnum.getStart()+x*4; i <= imageTypeEnum.getEnd(); i++) {
                downloadOne(imageTypeEnum, i);
            }

        }).start();

    }

    private static void downloadOne(StealImageTypeEnum imageTypeEnum, int i) {
        String url;
        url = String.format(baseImageUrl, imageTypeEnum.getCode(), i);


        try {
            // 解析
            ImageMessage message = getMessage(url);

            // 保存
            getImageByte(message, imageTypeEnum);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return;
        }


    }

    /**
     * 获取图片地址信息
     */
    private static ImageMessage getMessage(String url) {

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


    private static void getImageByte(ImageMessage message, StealImageTypeEnum imageTypeEnum) {


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

