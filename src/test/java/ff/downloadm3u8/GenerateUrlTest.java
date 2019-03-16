package ff.downloadm3u8;


import java.util.ArrayList;
import java.util.List;

import enums.StealVideoTypeEnum;
import lombok.extern.slf4j.Slf4j;
import utils.FileUtils;
import utils.SourceConstant;

/**
 * @Author: jinye
 * @Date: Create in 16:07 2019-03-16
 */
@Slf4j
public class GenerateUrlTest {

    private static final String PATH = "c:/Users/liuqi/Desktop/url.html";

    public static void main(String[] args) {

        List<String> videoContent = getVideoContent(StealVideoTypeEnum.ASIA_IMAGE);

//        videoContent.forEach(e -> {
//            System.out.println(videoContent.toString());
//        });
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<videoContent.size();i++){
            stringBuilder.append(String.format("<a href=\"%s\">%s</a>\n", videoContent.get(i),i+1));
        }
        FileUtils.WriteStringToFile2(PATH, stringBuilder.toString());

    }


    private static List<String> getVideoContent(StealVideoTypeEnum videoTypeEnum) {
        List<String> videoContentList = new ArrayList<>();
        for (int i = videoTypeEnum.getStart(); i <= videoTypeEnum.getEnd(); i++) {
            String videoContent = String.format(SourceConstant.BASE_VIDEO_URL, videoTypeEnum.getCode(), i);
            log.info(videoContent);
            videoContentList.add(videoContent);
        }
        return videoContentList;
    }

}
