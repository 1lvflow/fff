package ff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtils;
import utils.HttpUtils;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );

    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
        String iframeBg ="https://v.qq.com/iframe/player.html?vid=";
//        String iframeBg ="<iframe frameborder=\"0\" width=\"640\" height=\"498\" src=\"https://v.qq.com/iframe/player.html?vid=";
//        String iframeEnd ="&tiny=0&auto=0\" allowfullscreen></iframe>";
        String iframeEnd ="&tiny=0&auto=0";
        String link ="http://mp.weixin.qq.com/s?__biz=MjM5NzQ5MDg2MA==&mid=2650641446&idx=3&sn=a0239d5f8aa274b84e445b2b04051b43&chksm=bed00de589a784f38ee9574fd7157cc05289eb18c02042dcbe76af04824b218318f3ffaa0849#rd";
        String path ="/Users/xmac/Desktop/sitemap01.html";
        String bg ="vid=";
        String en ="&width";
        String content = HttpUtils.get(link);
        if (StringUtils.isBlank(content)) {
            LOGGER.warn("Link错误");
            return;
        }

   //     FileUtils.WriteStringToFile2(path,content);
        Document doc = Jsoup.parse(content);
        Element metaContent = doc.select("div#meta_content").first();
        Elements ems = metaContent.select("em");
        doc.select("div#meta_content").first().remove();
        Elements videos = doc.select("iframe");
        videos.forEach(video->{
            String vi =  video.attr("data-src");
 //           System.out.println("未替换"+video.toString());
            int begin = vi.indexOf(bg);
            int end = vi.indexOf(en);
            String vid = vi.substring(begin+bg.length(),end);
//            System.out.println(vid);
            String iframe =iframeBg+vid+iframeEnd;
//            System.out.println("iframe"+iframe);
            video.attr("src",iframe).attr("width","98%").attr("height","640");
        });

        //处理图片
        Elements imgs = doc.select("img");
        imgs.forEach(img->{
            System.out.println(img.toString());
           String src =  img.attr("data-src");
           img.attr("src",src);
        });


        FileUtils.WriteStringToFile2(path,doc.toString());
//        System.out.println(videos.toString());
//        Element metaContent = doc.select("div#meta_content").first();
//        Elements ems = metaContent.select("em");
//        try {
//            if (Objects.nonNull(ems) && Objects.nonNull(ems.get(1)) && StringUtils.isNotBlank(ems.get(1).text())) {
//                System.out.println(ems.get(1).text());
//            }
//        } catch (Exception e) {
//            LOGGER.warn("没有抓到作者");
//        }
//
//        try {
//            Elements aSource = metaContent.select("a");
//            if (Objects.nonNull(aSource) && Objects.nonNull(aSource.first()) && StringUtils.isNotBlank(aSource.first().text())) {
//                System.out.println(aSource.first().text());
//            }
//        } catch (Exception e) {
//            LOGGER.warn("没有抓到来源");
//        }
//        System.out.println(content);
    }
}
