package com.fearon.util.poi;

import com.fearon.util.date.DateTimeUtil;
import com.fearon.util.poi.contact.ExcelType;
import com.fearon.util.poi.entities.BasePOIEntity;
import com.fearon.util.poi.entities.ExcelFormatEntity;
import com.fearon.util.poi.util.ClassHandleUtil;
import com.fearon.util.string.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @description: Excel 处理工具
 * @author: Fearon
 * @create: 2019-04-03 10:42
 **/
public class ExcelUtil {
    public static void createExcel(ExcelFormatEntity entity, List<BasePOIEntity> tableHeader, List<Map<String, Object>> tableRowData) {
        Workbook workbook = null;
        Sheet sheet = null;
        CellStyle style = null;
        Font font = null;
        StringBuilder realFileName = new StringBuilder(entity.getFileName());
        Integer excelType = (tableRowData.size() < 100000) ? entity.getExcelType().getType() : ExcelType.SXSSF.getType();
        switch (ExcelType.getType(excelType)) {
            case HSSF:
                workbook = new HSSFWorkbook();
                realFileName.append(".xls");
                break;
            case XSSF:
                workbook = new XSSFWorkbook();
                realFileName.append(".xlsx");
                break;
            case SXSSF:
                workbook = new SXSSFWorkbook();
                realFileName.append(".xlsx");
                break;
            default:
                workbook = new HSSFWorkbook();
                realFileName.append(".xls");
                break;
        }
        entity.setFileName(realFileName.toString());

        sheet = workbook.createSheet(entity.getSheetName());
        //设置默认宽度
        sheet.setDefaultColumnWidth(16);
        sheet.setDefaultRowHeightInPoints(20);
        //创建样式
        style = workbook.createCellStyle();
        //设置样式
        style.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中

        //生成字体
        font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.index);
        //应用字体
        style.setFont(font);
        //自动换行
        style.setWrapText(true);

        Map<String, Object> tableBodyStyle = createTableHeader(sheet, style, entity.getTitle(), tableHeader);
        TreeMap<Integer, BasePOIEntity> associateHeaderDivision = ClassHandleUtil.castTheUnknownClass(tableBodyStyle.get("associateHeaderDivision"));
        if (null == associateHeaderDivision)
            throw new NullPointerException("[table_columns] no valid data was obtains!");

        writeTheData(sheet, (Row) tableBodyStyle.get("row"), style, associateHeaderDivision, tableRowData);

        write(entity, workbook);
    }

    /**
     * 创建表头
     *
     * @param sheet       工作表对象
     * @param title       标题
     * @param tableHeader 表头集合
     * @return
     */
    private static Map<String, Object> createTableHeader(Sheet sheet, CellStyle style, String title, List<BasePOIEntity> tableHeader) {
        int headerSize = 0;
        if (null == tableHeader || (headerSize = tableHeader.size()) <= 0)
            throw new NullPointerException("[table_header] no valid data was obtains!");

        int rowIndex = 0, cellIndex = 0, cellIndexCache = 0;

        Row row = null;
        Cell cell = null;
        // 若存在标题，写入表格标题
        boolean isNeedTitle = false;
        if (null != title && title.trim().length() > 0) {
            row = sheet.createRow(rowIndex);
            cell = row.createCell(cellIndex);
            cell.setCellValue(title);
            cell.setCellStyle(style);
            isNeedTitle = true;
            rowIndex++;
        }

        List<BasePOIEntity> rowCells = new ArrayList<>();
        row = sheet.createRow(rowIndex++);
        CellRangeAddress cellAddresses = null;
        int i = 0, index = 0, noneCounter = 0, maxCells = 0;
        Map<Integer, Integer> mergeColumns = new LinkedHashMap<>();
        Map<Integer, BasePOIEntity> associateHeaderDivision = new TreeMap<>();
        for (; i < headerSize; i++) {
            BasePOIEntity entity = tableHeader.get(i);
            if (null != entity) {
                index++;
                cell = row.createCell(cellIndex);
                entity.setOrderNumber(cellIndex);
                cell.setCellValue(entity.getName());
                cell.setCellStyle(style);

                if (null != entity.getKey() && entity.getKey().toString().trim().length() > 0) {
                    associateHeaderDivision.put(cellIndex, entity);
                }

                // 计算单元格宽度，宽度大于1，则进行横向合并
                int width = getTheEntityCellNumber(entity);
                if (width > 1) {
                    int orderNumber = entity.getOrderNumber();
                    cellIndexCache = cellIndex += width;
                    cellAddresses = new CellRangeAddress(
                            rowIndex - 1,
                            rowIndex - 1,
                            orderNumber,
                            orderNumber + width - 1
                    );
                    sheet.addMergedRegion(cellAddresses);
                } else {
                    cellIndexCache++;
                }
                if (cellIndex < cellIndexCache) {
                    cellIndex++;
                }

                List<BasePOIEntity> grandsons = entity.getChildren();
                if (null != grandsons) {
                    rowCells.addAll(grandsons);
                } else {
                    BasePOIEntity none = new BasePOIEntity("", "");
                    rowCells.add(none);
                    noneCounter++;

                    if (mergeColumns.containsKey(cellIndex)) {
                        mergeColumns.put(cellIndex, mergeColumns.get(cellIndex) + 1);
                    } else {
                        mergeColumns.put(cellIndex, 0);
                    }
                }

                if (index == headerSize) {
                    tableHeader = rowCells;
                    headerSize = tableHeader.size();

                    if (maxCells < cellIndex)
                        maxCells = cellIndex;

                    if (noneCounter == rowCells.size()) {
                        break;
                    }

                    rowCells = new ArrayList<>();
                    i = -1;
                    row = sheet.createRow(rowIndex++);
                    index = cellIndex = cellIndexCache = noneCounter = 0;
                }
            }
        }

        if (isNeedTitle) {
            cellAddresses = new CellRangeAddress(0, 0, 0, maxCells - 1);
            sheet.addMergedRegion(cellAddresses);
        }

        if (!mergeColumns.isEmpty()) {
            int cover = 0;
            // 检测是否需要进行纵向补位操作
            if (mergeColumns.containsValue(0)) {
                cover = 1;
            }

            int freezeRows = 0, freezeRowsCache = 0;
            // 检测单元格深度，深度>=1，则进行纵向合并
            for (Map.Entry<Integer, Integer> entry : mergeColumns.entrySet()) {
                Integer rows = entry.getValue();
                if (rows >= 1) {
                    freezeRowsCache = rows + cover;
                    cellAddresses = new CellRangeAddress(1, freezeRowsCache, entry.getKey() - 1, entry.getKey() - 1);
                    sheet.addMergedRegion(cellAddresses);
                }

                if (freezeRows < freezeRowsCache)
                    freezeRows = freezeRowsCache;
            }

            // 冻结表头
            sheet.createFreezePane(0, freezeRows + 1, 0, freezeRows + 1);
        }

        Map<String, Object> tableBodyStyle = new HashMap<>();
        tableBodyStyle.put("row", row);
        tableBodyStyle.put("associateHeaderDivision", associateHeaderDivision);
        return tableBodyStyle;
    }

    /**
     * 获取当前单元格需要占用的横向格子数目
     *
     * @param entity 单元格实体
     * @return
     */
    private static int getTheEntityCellNumber(BasePOIEntity entity) {
        if (null == entity)
            throw new NullPointerException("[cell_entity] no valid data was obtains!");

        int width = 1, childrenNumber = 0;
        List<BasePOIEntity> children = entity.getChildren();
        if (null == children)
            return width;
        childrenNumber = children.size();
        if ((width = childrenNumber) == 0)
            return 1;

        List<BasePOIEntity> row = new ArrayList<>();
        int index = 0;
        int i = 0, widthCache = 0;
        for (; i < childrenNumber; i++) {
            BasePOIEntity child = children.get(i);
            if (null != child) {
                index++;

                List<BasePOIEntity> grandsons = child.getChildren();
                if (null != grandsons) {
                    widthCache += grandsons.size();
                    row.addAll(grandsons);
                }

                if (width < widthCache) {
                    width = widthCache;
                }

                if (index == childrenNumber) {
                    children = row;
                    childrenNumber = children.size();


                    row = new ArrayList<>();
                    i = -1;
                    index = widthCache = 0;
                }
            }
        }

        return width;
    }

    /**
     * 写入表数据
     *
     * @param sheet                   工作表
     * @param row                     现有的行对象
     * @param style                   工作表风格
     * @param associateHeaderDivision 写入数据的列（关联合并表头的最终列集合）
     * @param tableRowData            数据集
     */
    private static void writeTheData(Sheet sheet, Row row, CellStyle style,
                                     TreeMap<Integer, BasePOIEntity> associateHeaderDivision,
                                     List<Map<String, Object>> tableRowData) {
        int divisionSize = 0;
        if (null == tableRowData || (divisionSize = tableRowData.size()) <= 0)
            throw new NullPointerException("[table_row_data] no valid data was obtains!");

        int rowIndex = row.getRowNum(), cellIndex = 0, cellIndexCache = 0;

        Cell cell = null;
        BasePOIEntity entity = null;
        Map<String, Object> tableDivision = null;
        boolean flag = false;
        int rowCount = 0, columnCount = 0;
        for (; ; ) {
//            System.out.println("columnCount ---> " + columnCount);
            if (columnCount == 0) {
//                System.out.println("rowIndex ---> " + rowIndex);
                row = sheet.createRow(++rowIndex);
                cellIndex = 0;
//                System.out.println("rowCount ---> " + rowCount);
                if (++rowCount <= divisionSize) {
                    tableDivision = tableRowData.get(rowCount - 1);
                    flag = false;
                } else {
                    flag = true;
                }
            }
            if (null == tableDivision || flag) {
                /*System.out.println("flag ---> " + flag);
                System.out.println("rowCount ---> " + --rowCount);*/
                break;
            }

            if (associateHeaderDivision.containsKey(cellIndex)) {
                cellIndexCache = cellIndex;
                entity = associateHeaderDivision.get(cellIndex);
                Object dataSource = tableDivision.get(entity.getKey().toString());
                String formatter = null;
                StringBuilder data = new StringBuilder();
                if (null == dataSource) { // 允许单元格写入空数据
                    data.append("");
                } else if (StringUtil.isNotEmpty(formatter = entity.getNumberFormatter()) && dataSource instanceof Number) {
                    data.append(new DecimalFormat(formatter).format(dataSource));
                } else if (StringUtil.isNotEmpty(formatter = entity.getDateFormatter())) {
                    data.append(DateTimeUtil.formatDate(dataSource, formatter));
                } else {
                    data.append(dataSource.toString());
                }
                String suffix = null;
                if (StringUtil.isNotEmpty(suffix = entity.getSuffix())) {
                    data.append(suffix);
                }

                cell = row.createCell(cellIndex);
                cell.setCellValue(data.toString());
                cell.setCellStyle(style);
                if (cellIndex > 2 && !associateHeaderDivision.containsKey(cellIndex - 2)) {
                    CellRangeAddress cellAddresses = new CellRangeAddress(rowIndex - 1, rowIndex - 1, cellIndexCache, cellIndex - 1);
                    sheet.addMergedRegion(cellAddresses);
                }
                cellIndex++;

                columnCount++;
                if (columnCount == tableDivision.size()) {
                    if (rowCount == divisionSize) {
//                        System.out.println("rowCount ---> " + --rowCount);
                        break;
                    }
                    columnCount = 0;
                }
            }
        }
    }

    private static void write(ExcelFormatEntity entity, Workbook workbook) {
        try {
            OutputStream outputStream = null;
            String path = null, fileName = entity.getFileName();
            if (StringUtil.isNotEmpty(path = entity.getPath())) {
                File file = new File(path + fileName);
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                    file.createNewFile();
                }
                outputStream = new FileOutputStream(file);
            } else {
                HttpServletResponse response = entity.getResponse();
                response.setContentType("application/application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                outputStream = response.getOutputStream();

            }

            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
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

        createExcel(entity1, tableHeader, rowData);
    }
}
