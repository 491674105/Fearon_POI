package com.fearon.util.poi;

import com.fearon.util.poi.contact.ExcelType;
import com.fearon.util.poi.entities.BasePOIEntity;
import com.fearon.util.poi.entities.ExcelFormatEntity;
import com.fearon.util.string.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author: venter.zhu
 * @Date: 2019-03-13 19:44
 * @Version: 1.0
 **/
public class ExportUtil {
    /**
     * Excel导出工具（基于ApachePOI）
     *
     * @param response    Servlet响应对象
     * @param fileName    文件名
     * @param title       工作表标题
     * @param tableHeader 工作表表头
     * @param tableData   工作表数据内容
     */
    public static void exportExcelWithPOI(HttpServletResponse response, String fileName, String title,
                                          List<BasePOIEntity> tableHeader, List<Map<String, Object>> tableData) {
        try {
            if (tableData != null) {
                ExcelFormatEntity entity = new ExcelFormatEntity(response);
                // 如果表头长度大于256，修改成office 2007以上版本
                if (tableHeader.size() > 256) {
                    entity.setExcelType(ExcelType.XSSF);
                }
                entity.setFileName(fileName);
                entity.setSheetName("数据");
                entity.setTitle(title);

                ExcelUtil.createExcel(entity, tableHeader, tableData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        List<BasePOIEntity> tableHeader = new ArrayList<>();
        /*BasePOIEntity entity = new BasePOIEntity("年月", "punchDate");
        tableHeader.add(entity);
        entity = new BasePOIEntity("姓名", "name");
        tableHeader.add(entity);

        entity = new BasePOIEntity("日期", "punchDay");
        BasePOIEntity child1 = new BasePOIEntity("上午", "morning");
        child1.setChildren(Arrays.asList(
                new BasePOIEntity("状态", "morningStatus"),
                new BasePOIEntity("时间", "morningTime"),
                new BasePOIEntity("地点", "morningLocation")
                )
        );
        BasePOIEntity child2 = new BasePOIEntity("下午", "afternoon");
        child2.setChildren(Arrays.asList(
                new BasePOIEntity("状态", "afternoonStatus"),
                new BasePOIEntity("时间", "afternoonTime"),
                new BasePOIEntity("地点", "afternoonLocation")
                )
        );
        entity.setChildren(Arrays.asList(child1, child2));
        tableHeader.add(entity);*/

        /*List<Map<String, Object>> row = new ArrayList<>();
        Map<String, Object> division = new HashMap<>();
        division.put("punchDate", "2019-01");
        division.put("name", "fearon");
        division.put("morningStatus", "正常");
        division.put("morningTime", "2019-4-11 8:20:25");
        division.put("morningLocation", "人民广场");
        division.put("afternoonStatus", "正常");
        division.put("afternoonTime", "2019-4-11 18:20:25");
        division.put("afternoonLocation", "人民广场");
        row.add(division);*/


        BasePOIEntity entity = new BasePOIEntity("UUID", "uuids");
        tableHeader.add(entity);

        List<Map<String, Object>> rowData = new ArrayList<>();
        Map<String, Object> division = null;
        int i = 0;
        for (; i < 447; i++) {
            division = new HashMap<>();
            division.put("uuids", StringUtil.getUUID32(null, null));
            rowData.add(division);
        }
//        System.out.println("rowCount ---> " + i);

        ExcelFormatEntity entity1 = new ExcelFormatEntity("D:\\");
        entity1.setExcelType(ExcelType.XSSF);
        entity1.setFileName("test");
        entity1.setSheetName("数据");
//        entity1.setTitle("测试表");

        ExcelUtil.createExcel(entity1, tableHeader, rowData);
    }
}
