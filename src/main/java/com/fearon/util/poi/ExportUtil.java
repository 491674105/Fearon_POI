package com.fearon.util.poi;

import com.fearon.util.poi.contact.ExcelType;
import com.fearon.util.poi.entities.BasePOIEntity;
import com.fearon.util.poi.entities.ExcelFormatEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author: fearon
 * @Date: 2019-04-03 9:42
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
}
