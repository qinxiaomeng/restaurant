package com.ej.restaurant.enums;

public enum DataStatus implements BaseEnum {
    USEABLE("可用"),
    UNUSEABLE("不可用");

    private String key;
    private DataStatus(String key){
        this.key = key;
    }
    @Override
    public String getKey() {
        return null;
    }
}
