package ff.guava;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinye
 * @Date: Create in 16:16 2018/12/12 
 */
public class TestGuava {


    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        String result = Joiner.on("-").join(list);
        System.out.println(result);
    }
}
