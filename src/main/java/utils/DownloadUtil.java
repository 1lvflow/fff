package utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {

    private String path; // 远程资源路径
    private String targetPath; // 本地存储路径
    private DownFileThread[] threads; // 线程list
    private int threadNum; // 线程数量
    private long length; // 下载的文件大小

    // 构造初始化
    public DownloadUtil(String path, String targetPath, int threadNum) {
        super();
        this.path = path;
        this.targetPath = targetPath;
        this.threads = new DownFileThread[threadNum];
        this.threadNum = threadNum;
    }

    // 多线程下载文件资源
    public void download() {
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000); // 设置超时时间为5秒
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "google_user_agent");

            // 获取远程文件的大小
            length = conn.getContentLength();
            conn.disconnect();
            // 创建文件夹
            File file = new File(targetPath.substring(0,targetPath.lastIndexOf("/")));
            if(!file.exists()){
                file.mkdirs();
            }
//            File imgFile = new File(path);
//            // 设置本地文件大小
//            RandomAccessFile targetFile = new RandomAccessFile(targetPath, "rw");
//            targetFile.setLength(length);

            // 每个线程下载大小
            long avgPart = length / threadNum + 1;
            // 下载文件
            for (int i = 0; i < threadNum; i++) {
                long startPos = avgPart * i;
                RandomAccessFile targetTmp = new RandomAccessFile(targetPath,
                        "rw");
                targetTmp.seek(startPos); // 分段下载
                threads[i] = new DownFileThread(startPos, targetTmp,path, avgPart);
                threads[i].start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 监控下载进度
    public double getDownRate() {
        int currentSize = 0;
        for (int i = 0; i < threadNum; i++) {
            currentSize += threads[i].length;
        }
        return currentSize * 1.0 / length;
    }
    }
