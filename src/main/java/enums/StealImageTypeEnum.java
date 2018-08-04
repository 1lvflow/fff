package enums;

public enum StealImageTypeEnum implements BaseCodeEnum {

    STEAL_IMAGE(9, "偷拍自拍", 84800, 122599),

    ASIA_IMAGE(1, "亚洲", 1, 1),

    US_IMAGE(2, "欧美", 1, 1),

    CG_IMAGE(3, "动漫", 1, 1),

    CONFUSED_SEX_IMAGE(4, "乱性&另类", 1, 1),

    BEAUTIFUL_STOCKING(7, "偷拍", 1, 1),

    ADJECTIVE_IMAGE(8, "唯美", 1, 1);


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
