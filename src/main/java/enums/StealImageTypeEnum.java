package enums;

public enum StealImageTypeEnum implements BaseCodeEnum {

    STEAL_IMAGE(9, "偷拍自拍", 84800, 122599),

    ASIA_IMAGE(1, "亚洲", 88483, 122630),

    US_IMAGE(2, "欧美", 87893, 111379),

    CG_IMAGE(3, "动漫", 87310, 114653),

    CONFUSED_SEX_IMAGE(4, "另类", 86730, 114632),

    BEAUTIFUL_STOCKING(7, "美腿丝袜", 86124, 110365),

    ADJECTIVE_IMAGE(8, "唯美", 85531, 122661);


    private int code;
    private String description;
    private int start;
    private int end;


    StealImageTypeEnum(int code, String description, int start, int end) {
        this.description = description;
        this.code = code;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getCode() {
        return code;
    }


}
