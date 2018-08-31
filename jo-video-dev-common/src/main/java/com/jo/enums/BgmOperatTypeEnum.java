package com.jo.enums;

public enum BgmOperatTypeEnum {
    ADD("1","添加BGM"),
    DELETE("2","删除BGM");

    public final String type;
    public final String value;

    BgmOperatTypeEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    public static String getValueByKey(String key) {
        for (BgmOperatTypeEnum type: BgmOperatTypeEnum.values()) {
            if (type.getType().equals(key)) {
                return type.value;
            }
        }
        return null;
    }
}
