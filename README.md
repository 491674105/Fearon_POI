# 工具集
1、目前仅对excel进行处理，支持多级复杂表头生成（四级及以下表头可正常生成导出）

2、表头创建必须是树形结构

3、内部使用迭代算法进行实现保证了生成和导出的效率

4、调用路径：com.fearon.util.poi.ExportUtil

    测试代码：
    
    public static void test(String[] args) throws IOException {
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
        tableHeader.add(entity);

        List<Map<String, Object>> row = new ArrayList<>();
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

        ExcelFormatEntity entity1 = new ExcelFormatEntity("D:\\");
        entity1.setExcelType(ExcelType.XSSF);
        entity1.setFileName("test");
        entity1.setSheetName("数据");
        entity1.setTitle("测试表");

        ExcelUtil.createExcel(entity1, tableHeader, rowData);
    }
