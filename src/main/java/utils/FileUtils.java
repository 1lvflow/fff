package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtils {

    public static void WriteStringToFile2(String filePath, String txt) {
        try {
            File f=new File(filePath);
            if(!f.exists()){
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            // bw.append("在已有的基础上添加字符串");
            bw.write(txt);// 往已有的文件上添加字符串
            bw.close();
            fw.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
