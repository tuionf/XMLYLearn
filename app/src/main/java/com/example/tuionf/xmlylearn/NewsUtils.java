package com.example.tuionf.xmlylearn;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tuionf
 * @date 2017/10/31
 * @email 596019286@qq.com
 * @explain
 */

public class NewsUtils {

    /**one hour in ms*/
    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    /**one minute in ms*/
    private static final int ONE_MIN = 1 * 60 * 1000;
    /**one second in ms*/
    private static final int ONE_SECOND = 1 * 1000;

    public static String formatPlayCountNum(String count){
        double playCount = Double.parseDouble(count);
        playCount = playCount / 10000;

        DecimalFormat df = new DecimalFormat("####.0");
        //添加下面的代码不会四舍五入
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(playCount);
    }

    public static String formatUpdateTime(long updateTime){
        long intervalTime = System.currentTimeMillis() - updateTime;
        String updateAt = "";
        DecimalFormat df = new DecimalFormat("####");
        //添加下面的代码不会四舍五入
        df.setRoundingMode(RoundingMode.FLOOR);

        long intervalHour = intervalTime / ONE_HOUR ;

        if (intervalHour < 24) {
            updateAt = df.format(intervalHour)+"小时前";
        }else if ((intervalHour > 24) && (intervalHour < (30*24))){
            updateAt = df.format(intervalHour / 24)+"天前";
        }else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            updateAt = simpleDateFormat.format(new Date(updateTime));
        }

        return updateAt;
    }

    public static String formatTrackUpdateTime(long updateTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMDD");
        return  simpleDateFormat.format(new Date(updateTime));
    }

    /**HH:mm:ss*/
    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }
}
