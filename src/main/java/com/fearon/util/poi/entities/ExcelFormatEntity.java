package com.fearon.util.poi.entities;

import com.fearon.util.poi.contact.ExcelType;

import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: Fearon
 * @create: 2019-04-11 17:53
 **/
public class ExcelFormatEntity {
    private String path;

    private HttpServletResponse response;
    private ExcelType excelType = ExcelType.HSSF;
    private String fileName;
    private String title;
    private String sheetName;

    /**
     * 以本地路径导出
     * @param path
     */
    public ExcelFormatEntity(String path) {
        this.path = path;
    }

    /**
     * 以WEB数据流导出
     * @param response
     */
    public ExcelFormatEntity(HttpServletResponse response) {
        this.response = response;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ExcelType getExcelType() {
        return excelType;
    }

    public void setExcelType(ExcelType excelType) {
        this.excelType = excelType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
