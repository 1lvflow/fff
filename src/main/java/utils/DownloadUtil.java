package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {

    private String path; // 远程资源路径
    private String useAgent; // useAgent
    private String targetPath; // 本地存储路径
    private DownFileThread[] threads; // 线程list
    private int threadNum; // 线程数量
    private long length; // 下载的文件大小

    // 构造初始化
    public DownloadUtil(String path, String targetPath, int threadNum,String useAgent) {
        super();
        this.path = path;
        this.targetPath = targetPath;
        this.threads = new DownFileThread[threadNum];
        this.threadNum = threadNum;
        this.useAgent = useAgent;
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
            conn.setRequestProperty("user-agent", useAgent);

            // 获取远程文件的大小
            length = conn.getContentLength();
            conn.disconnect();
            // 设置本地文件大小
            RandomAccessFile targetFile = new RandomAccessFile(targetPath, "rw");
            targetFile.setLength(length);

            // 每个线程下载大小
            long avgPart = length / threadNum + 1;
            // 下载文件
            for (int i = 0; i < threadNum; i++) {
                long startPos = avgPart * i;
                RandomAccessFile targetTmp = new RandomAccessFile(targetPath,
                        "rw");
                targetTmp.seek(startPos); // 分段下载
                threads[i] = new DownFileThread(startPos, targetTmp,path, avgPart,useAgent);
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




    //链接url下载图片
    public void downloadPicture() throws Exception {
        URL url = new URL(path);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", useAgent);
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        //new一个文件对象用来保存图片，默认保存当前工程根目录

        File imgFile = new File(targetPath);
        if(imgFile.isFile()){
            imgFile.createNewFile();
        }

        //创建输出流
        FileOutputStream outStream = new FileOutputStream(imgFile);
        //写入数据
        outStream.write(data);
        //关闭输出流
        outStream.close();

    }



    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();

    }
    }
