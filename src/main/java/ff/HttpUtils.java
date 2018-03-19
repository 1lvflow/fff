package ff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wangjinliang on 2018/1/5.
 */
public final class HttpUtils {

    private HttpUtils() {}

    public static void sendPost(String urlParam,String Json) {
        String charset = "utf-8";
        HttpURLConnection con = null;
        OutputStreamWriter osw = null;
        try {
            URL url = new URL(urlParam);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/json");
            if (Json!=null&&Json.length()>0) {
                osw = new OutputStreamWriter(con.getOutputStream(), charset);
                osw.write(Json);
                osw.flush();
            }
            StringBuilder stringBuilder = new StringBuilder();
           BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
            String temp;
            while ((temp = br.readLine()) != null) {
                stringBuilder.append(temp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {}
            }
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {}
            }
        }
    }
}
