package com.fearon.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 日期时间处理工具
 * @author: Fearon
 * @create: 2019-03-26 13:45
 **/
public class DateTimeUtil {
    public enum DateTimeEnum {
        DEFAULT(-1, "no value can be marched!"),
        YEAR(1, "year"),
        MONTH(2, "month"),
        DAY(3, "day");

        private Integer index;
        private String description;

        DateTimeEnum(Integer index, String description) {
            this.index = index;
            this.description = description;
        }

        public static DateTimeEnum getEnum(Integer index) {
            for (DateTimeEnum value : DateTimeEnum.values()) {
                if (value.getIndex().compareTo(index) == 0) {
                    return value;
                }
            }
            return DEFAULT;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 跨时间段计算对应时间点
     *
     * @param source    起始时间
     * @param formatter 格式化
     * @param skipType  时间段类型（年月日）
     * @param skip      时间段长度（向前为正，向后为负）
     * @return
     */
    public static String getTargetDate(Date source, String formatter, DateTimeEnum skipType, int skip) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);

        SimpleDateFormat format = new SimpleDateFormat(formatter);
        switch (DateTimeEnum.getEnum(skipType.getIndex())) {
            case DAY:
                calendar.add(Calendar.DATE, skip);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, skip);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, skip);
                break;
            default:
                break;
        }

        return format.format(calendar.getTime());
    }

    /**
     * 获取指定日期到对应月一号之间的时间列表（包含指定日期）
     *
     * @param endTime     指定日期
     * @param isNeedToday 若指定日期处于现实意义的今天，是否需要合并今天的日期
     * @return
     */
    public static Map<Integer, Date> getDatesItemToMap(String endTime, boolean isNeedToday) {
        Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
        try {
            end.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int year = end.get(Calendar.YEAR), month = end.get(Calendar.MONTH);
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.DAY_OF_MONTH, start.getActualMinimum(Calendar.DAY_OF_MONTH));

        if (!isNeedToday) {
            Calendar now = Calendar.getInstance();
            isNeedToday = !(
                    (year == now.get(Calendar.YEAR)) &&
                            (month == now.get(Calendar.MONTH) &&
                                    (end.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)))
            );
        }

        int index = 0;
        Map<Integer, Date> result = new HashMap<>();
        for (; ; ) {
            if (!end.after(start)) {
                if (isNeedToday)
                    result.put(++index, end.getTime());
                break;
            }

            result.put(++index, start.getTime());
            start.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }

    /**
     * 为指定年月补充日期（如果是今天或往后则补足今天的日期，否则补足往月的最后一天）
     *
     * @param date
     * @return
     */
    public static String completeDate(String date) {
        Calendar now = Calendar.getInstance(), target = Calendar.getInstance();
        try {
            target.setTime(new SimpleDateFormat("yyyy-MM").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date nowTime = null, targetDate = null;
        try {
            nowTime = new SimpleDateFormat("yyyy-MM").parse(new SimpleDateFormat("yyyy-MM").format(new Date()));
            long timestampDiffer = target.getTimeInMillis() - nowTime.getTime();

            if (timestampDiffer < 0L) {
                now.set(Calendar.YEAR, target.get(Calendar.YEAR));
                now.set(Calendar.MONTH, target.get(Calendar.MONTH));
                now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
            }

            targetDate = now.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("yyyy-MM-dd").format(targetDate);
    }

    /**
     * 将数据格式化成对应的日期时间
     *
     * @param source
     * @param formatter
     * @param <T>
     * @return
     */
    public static <T> String formatDate(T source, String formatter) {
        String result = null;
        try {
            if (source instanceof Date) {
                result = new SimpleDateFormat(formatter).format(source);
            } else if (source instanceof String) {
                result = new SimpleDateFormat(formatter).format(new SimpleDateFormat().parse((String) source));
            }
            return result;
        } catch (ParseException e) {
            System.err.println("[Formatter Exception] : " + source + "is not a time or time type in the form of a string.");
        }
        return null;
    }
}
