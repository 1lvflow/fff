package ff.downloadm3u8;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import enums.StealVideoTypeEnum;
import ff.stealImage.AppTest;
import lombok.Data;
import utils.FileUtils;
import utils.HttpUtils;
import utils.UseAgent;
import utils.ffmpeg.FfmpegUtil;

import static java.lang.Thread.sleep;

public class StealVideo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);


    private static final String BASE_PAGE = "http://www.193tt.com/htm/mp4play%s/%s.htm";

    private static final String BASE_VIDEO_URL = "https://333.326gg.com/htm/mp4playiframe%s/%s.htm";

    private static final String BASE_SRC = "https://m3u8.121yy.com/m3u8/javbox_water_m3u8%s";


    public static String basePath = "/Volumes/外接磁盘/stealm3u8/%s/%s";

    //文件夹名+文件名
    public static String BASE_PATH = "/Users/xmac/Documents/apple/steal/%s";

    @Data
    private static class VideoMessage {
        private String title;
        private String url;
    }


    public static void main(String[] args) {
        StealVideoTypeEnum videoTypeEnum = StealVideoTypeEnum.ASIA_IMAGE;
//        int size = 100;
//        int oldSize = 169;
//        for (int i = videoTypeEnum.getStart() + oldSize; i < videoTypeEnum.getStart() + oldSize + size; i++) {
//            downloadOne(videoTypeEnum,i);
//        }
//
//        System.out.println("已经完成"+size+"条");
//        int size = 20;
//        int oldSize = 172;
        int size = 2;
        int oldSize = 0;
        int threadSize = 2;
        int x = (size) / threadSize;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadSize);

        for (int j = 0; j < threadSize; j++) {
            int finalJ = j;
            fixedThreadPool.execute(() -> {
                for (int i = videoTypeEnum.getStart() + oldSize + x * finalJ; i < videoTypeEnum.getStart() + oldSize + x * (finalJ + 1); i++) {
                    downloadOne(videoTypeEnum, i);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(finalJ + "主进程完成");
            });
        }
    }

    private static void downloadOne(StealVideoTypeEnum videoTypeEnum,int i) {


        VideoMessage message = new VideoMessage();
        // 文件夹路径
        String folderPath = null;

        try {
            // 解析
            message = getMessage(videoTypeEnum,i);
            folderPath = String.format(basePath,videoTypeEnum.getDescription(), message.getTitle());
            // 保存
            int index = (int) (Math.random() * UseAgent.user_agent_list.length);
            DownloadM3U8.download(folderPath, message.getTitle(), message.getUrl(), UseAgent.user_agent_list[index]);
            // 转码
            String input = folderPath + "/" + message.getTitle() + ".ts";
            System.out.println(input);
            String output = String.format(basePath,videoTypeEnum.getDescription(),(message.getTitle() + ".mp4"));
            FfmpegUtil.ffmpeg("",input,output);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        } finally {
            // 删除
             FileUtils.delFolder(folderPath);
        }


    }

    /**
     * 获取地址信息
     */
    private static VideoMessage getMessage(StealVideoTypeEnum videoTypeEnum,int i) {

        final String start = "javbox";
        String pageContent = HttpUtils.get(String.format(BASE_PAGE,videoTypeEnum.getCode(), i));
        if (StringUtils.isBlank(pageContent)) {
            LOGGER.warn("Link错误");
            return null;
        }

        String videoContent = HttpUtils.get(String.format(BASE_VIDEO_URL, videoTypeEnum.getCode(),i));
        if (StringUtils.isBlank(videoContent)) {
            LOGGER.warn("Link错误");
            return null;
        }


        Document page = Jsoup.parse(pageContent);
        Document video = Jsoup.parse(videoContent);

        VideoMessage message = new VideoMessage();
        // url
        Elements videoElement = video.select("div.mt10");
        String videoDiv = videoElement.toString();
        int x1 = videoDiv.indexOf(start);
        videoDiv = videoDiv.substring(x1);
        int x2 = videoDiv.indexOf(";");
        videoDiv = videoDiv.substring(start.length(), x2);
        String videoPath = videoDiv.replace("(", "")
                .replaceAll("\"", "")
                .replace(")", "");
        message.setUrl(String.format(BASE_SRC, videoPath));

        // title
        String href = String.format("htm/mp4play%s/",videoTypeEnum.getCode());
        Element title = page.select("a").stream().filter(e ->
                e.attr("href").indexOf(href) > 0
        ).collect(Collectors.toList()).get(0);
        String titleString = title.ownText().replaceAll("»", "").replaceAll(" ", "").trim();
        message.setTitle(titleString);

        return message;
    }


}
