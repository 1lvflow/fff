package ff.stealImage;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enums.StealImageTypeEnum;
import lombok.Data;
import utils.DownloadUtil;
import utils.HttpUtils;
import utils.UseAgent;

import static java.lang.Thread.sleep;

/**
 * Unit test for simple App.
 */
public class AppTest {
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

        int size = 800;
        StealImageTypeEnum imageTypeEnum = StealImageTypeEnum.STEAL_IMAGE;
        int threadSize = 20;
        int x = (size) / threadSize;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadSize);

        for (int j = 0; j < threadSize; j++) {
            int finalJ = j;
            fixedThreadPool.execute(() -> {
                for (int i = imageTypeEnum.getStart() + x * finalJ; i <= imageTypeEnum.getStart() + x * (finalJ + 1); i++) {
                    downloadOne(imageTypeEnum, i);
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("主进程完成");
            });
        }

    }

    private static void downloadOne(StealImageTypeEnum imageTypeEnum, int i) {
        String url;
        url = String.format(baseImageUrl, imageTypeEnum.getCode(), i);
        System.out.println(url);

        try {
            // 解析
            ImageMessage message = getMessage(url);

            // 保存
            getImageByte(message, imageTypeEnum);
        } catch (Exception e) {
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
        });
        imageMessage.setUrls(urls);

        return imageMessage;
    }

    /**
     * 请求imgUrl
     */
    private static void getImageByte(ImageMessage message, StealImageTypeEnum imageTypeEnum) {


        List<String> imgUrls = message.getUrls();

        String filePagePath = String.format(basePathMac, imageTypeEnum.getDescription() + "/" + message.getTitle());
        if (isCreatedFilePage(filePagePath)) return;

        imgUrls.forEach((String e) -> {
            // path
            String fileName = e.substring(e.lastIndexOf("/") + 1);//从后往前寻找

            String path = String.format(basePathMac, imageTypeEnum.getDescription() + "/" + message.getTitle() + "/" + fileName);


            try {

  //                  FileUtils.downloadPicture(e, path);

                int index = (int) (Math.random() * UseAgent.user_agent_list.length);

                DownloadUtil download = new DownloadUtil(e, path, 4,UseAgent.user_agent_list[index]);
                download.downloadPicture();
//                download.download();
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        while (download.getDownRate() < 1) {
//                            System.out.println(download.getDownRate());
//                            try {
//                                Thread.sleep(200); // 200毫秒扫描一次
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                }).start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    private static boolean isCreatedFilePage(String path) {
        // 创建文件夹
        System.out.println(path);
        File file = new File(path);
        if(file.exists()){
            System.out.println("文件夹已经存在"+path);
            return true;
        }
        file.mkdirs();
        return false;
    }


}

