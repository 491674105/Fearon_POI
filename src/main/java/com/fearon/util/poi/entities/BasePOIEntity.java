package com.fearon.util.poi.entities;

import java.util.List;

/**
 * @description:
 * @author: Fearon
 * @create: 2019-04-03 13:39
 **/
public class BasePOIEntity {
    private int index;
    private String name;
    private Object key;
    private String suffix;
    private String dateFormatter;
    private String numberFormatter;
    private int orderNumber;
    private List<BasePOIEntity> children;

    private int parentKey = this.getClass().hashCode();

    public BasePOIEntity(String name, Object key) {
        this.name = name;
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getKey() {
        return key;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatter(String dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String getNumberFormatter() {
        return numberFormatter;
    }

    public void setNumberFormatter(String numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<BasePOIEntity> getChildren() {
        return children;
    }

    public void setChildren(List<BasePOIEntity> children) {
        this.children = children;
    }

    public int getParentKey() {
        return parentKey;
    }

    public void setParentKey(int parentKey) {
        this.parentKey = parentKey;
    }
}
