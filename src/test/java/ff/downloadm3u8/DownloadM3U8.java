package ff.downloadm3u8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

import utils.M3U8;

public class DownloadM3U8 {

    public static String BASE_PATH = "/Volumes/外接磁盘/stealm3u8/%s";
    public static int connTimeout = 30 * 60 * 1000;
    public static int readTimeout = 30 * 60 * 1000;
    public static String s1 = "https://m3u8.121yy.com/m3u8/javbox_water_m3u8/TP-23432/TP-23432.m3u8";
    public static String s2 = "http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8";

    public static void main(String[] args) {
        String path = String.format(BASE_PATH, 1);
        File tfile = new File(path);
        if (!tfile.exists()) {
            tfile.mkdirs();
        }

        M3U8 m3u8ByURL = getM3U8ByURL(s1);
        String basePath = m3u8ByURL.getBasepath();
        m3u8ByURL.getTsList().stream().parallel().forEach(m3U8Ts -> {
            File file = new File(path + File.separator + m3U8Ts.getFile());
            if (!file.exists()) {// 下载过的就不管了
                FileOutputStream fos = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL(basePath + m3U8Ts.getFile());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent", "google_user_agent");
                    conn.setConnectTimeout(connTimeout);
                    conn.setReadTimeout(readTimeout);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                        fos = new FileOutputStream(file);// 会自动创建文件
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while ((len = inputStream.read(buf)) != -1) {
                            fos.write(buf, 0, len);// 写入流中
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {// 关流
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("文件下载完毕!");
        mergeFiles(tfile.listFiles(), path + "/" + "test.ts");
    }

    public static void download(String downloadPath, String title, String videoUrl, String useAgent) {

        String path = String.format(downloadPath, title);
        File tfile = new File(path);
        if (!tfile.exists()) {
            tfile.mkdirs();
        }

        M3U8 m3u8ByURL = getM3U8ByURL(videoUrl);
        String basePath = m3u8ByURL.getBasepath();
        m3u8ByURL.getTsList().stream().parallel().forEach(m3U8Ts -> {
            File file = new File(path + File.separator + m3U8Ts.getFile());
            if (!file.exists()) {// 下载过的就不管了
                FileOutputStream fos = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL(basePath + m3U8Ts.getFile());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent", useAgent);
                    conn.setConnectTimeout(connTimeout);
                    conn.setReadTimeout(readTimeout);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                        fos = new FileOutputStream(file);// 会自动创建文件
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while ((len = inputStream.read(buf)) != -1) {
                            fos.write(buf, 0, len);// 写入流中
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {// 关流
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("文件下载完毕!");
        mergeFiles(tfile.listFiles(), getResultPath(title, path));

    }

    private static String getResultPath(String title, String path) {
        return path + "/" + title + ".ts";
    }


    public static M3U8 getM3U8ByURL(String m3u8URL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(m3u8URL).openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "google_user_agent");
            if (connection.getResponseCode() == 200) {
                String realUrl = connection.getURL().toString();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String basepath = realUrl.substring(0, realUrl.lastIndexOf("/") + 1);
                M3U8 ret = new M3U8();
                ret.setBasepath(basepath);

                String line;
                float seconds = 0;
                int mIndex;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) {
                        if (line.startsWith("#EXTINF:")) {
                            line = line.substring(8);
                            if ((mIndex = line.indexOf(",")) != -1) {
                                line = line.substring(0, mIndex);
                            }
                            try {
                                seconds = Float.parseFloat(line);
                            } catch (Exception e) {
                                seconds = 0;
                            }
                        }
                        continue;
                    }
                    if (line.endsWith("m3u8")) {
                        return getM3U8ByURL(basepath + line);
                    }
                    ret.addTs(new M3U8.Ts(line, seconds));
                    seconds = 0;
                }
                reader.close();

                return ret;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼接
     */
    public static boolean mergeFiles(File[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1) {
            return false;
        }

        if (fpaths.length == 1) {
            return fpaths[0].renameTo(new File(resultPath));
        }
        for (int i = 0; i < fpaths.length; i++) {
            if (!fpaths[i].exists() || !fpaths[i].isFile()) {
                return false;
            }
        }
        File resultFile = new File(resultPath);

        if(resultFile.exists()){
            return true;
        }

        try {
            FileOutputStream fs = new FileOutputStream(resultFile, true);
            FileChannel resultFileChannel = fs.getChannel();
            FileInputStream tfs;
            for (int i = 0; i < fpaths.length; i++) {
                tfs = new FileInputStream(fpaths[i]);
                FileChannel blk = tfs.getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                tfs.close();
                blk.close();
            }
            fs.close();
            resultFileChannel.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

         for (int i = 0; i < fpaths.length; i ++) {
         fpaths[i].delete();
         }

        return true;
    }
}
