package ff.downloadm3u8;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import enums.StealVideoTypeEnum;
import ff.stealImage.AppTest;
import lombok.Data;
import utils.HttpUtils;
import utils.UseAgent;
import utils.ffmpeg.ConvertVideo;

public class StealVideo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);


    private static final String BASE_PAGE = "http://www.193tt.com/htm/mp4play3/%s.htm";

    private static final String BASE_VIDEO_URL = "https://333.326gg.com/htm/mp4playiframe3/%s.htm";

    private static final String BASE_SRC = "https://m3u8.121yy.com/m3u8/javbox_water_m3u8%s";


    public static String basePath = "/Volumes/外接磁盘/stealm3u8/%s";

    //文件夹名+文件名
    public static String BASE_PATH = "/Users/xmac/Documents/apple/steal/%s";

    @Data
    private static class VideoMessage {
        private String title;
        private String url;
    }


    public static void main(String[] args) {
        StealVideoTypeEnum videoTypeEnum = StealVideoTypeEnum.STEAL;
        int size = 20;

        for (int i = videoTypeEnum.getStart(); i <= videoTypeEnum.getStart() + size; i++) {
            downloadOne(i);
        }

    }

    private static void downloadOne(int i) {


        try {
            // 解析
            VideoMessage message = getMessage(i);

            // 保存
            DownloadM3U8.download(basePath, message.getTitle(), message.getUrl(), UseAgent.user_agent_list[0]);

            // 转码
            String input = String.format(basePath, message.getTitle()) + "/" + message.getTitle() + ".ts";
            System.out.println(input);
//            String output = String.format(basePath, message.getTitle()) + "/" + message.getTitle() + ".mp4";
            ConvertVideo.convertVedio(input);

            new File(input).delete();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }


    }

    /**
     * 获取图片地址信息
     */
    private static VideoMessage getMessage(int i) {

        final String start = "javbox";
        String pageContent = HttpUtils.get(String.format(BASE_PAGE, i));
        if (StringUtils.isBlank(pageContent)) {
            LOGGER.warn("Link错误");
            return null;
        }

        String videoContent = HttpUtils.get(String.format(BASE_VIDEO_URL, i));
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
        Element title = page.select("a").stream().filter(e ->
                e.attr("href").indexOf("htm/mp4play3/") > 0
        ).collect(Collectors.toList()).get(0);
        String titleString = title.ownText().replaceAll("»", "").replaceAll(" ", "").trim();
        message.setTitle(titleString);

        return message;
    }


}
