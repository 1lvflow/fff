package ff;


        import com.google.gson.annotations.SerializedName;
        import lombok.Data;
        import lombok.ToString;

/**
 * Created by wangjinliang on 2018/2/5.
 */
@Data
@ToString
public class Info {
    private String aid;
    private String appmsgid;
    private String cover;
    private String digest;
    private String itemidx;
    private String link;
    private String title;
    @SerializedName("update_time")
    private Long updateTime;
}

