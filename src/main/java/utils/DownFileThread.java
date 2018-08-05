package utils;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载类
 */
public class DownFileThread   extends Thread {
    private long startPos;
    private RandomAccessFile raf;
    private long size;
    public long length;
    private String path;
    private String useAgent;


    public DownFileThread(long startPos, RandomAccessFile raf, String path,long size,String useAgent) {
        super();
        this.startPos = startPos;
        this.raf = raf;
        this.size = size;
        this.path = path ;
        this.useAgent = useAgent;
    }

    @Override
    public void run() {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", useAgent);

            InputStream in = conn.getInputStream();
            in.skip(this.startPos);
            byte[] buf = new byte[1024];
            int hasRead = 0;
            while (length < size && (hasRead = in.read(buf)) != -1) {
                raf.write(buf, 0, hasRead);
                length += hasRead;
            }
            raf.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
