package com.fearon.util.poi.contact;

/**
 * @description:
 * @author: Fearon
 * @create: 2019-04-11 17:48
 **/
public enum ExcelType {
    DEFAULT(-1, "no value can match this..."),
    HSSF(0, "HSSF"),
    XSSF(1, "XSSF"),
    SXSSF(2, "SXSSF");

    Integer type;
    String description;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    ExcelType(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public static ExcelType getType(Integer type) {
        for (ExcelType excelType : ExcelType.values()) {
            if (excelType.getType().compareTo(type) == 0) {
                return excelType;
            }
        }
        return DEFAULT;
    }
}
