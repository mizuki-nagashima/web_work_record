package services.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.MsGeneralCode;
import models.form.DateList;

/**
 * 日付・時間に関する共通クラス
 */
public class DateUtil {

    /**
     * 2桁まで左側0埋め
     * @param value
     */
    public static String getZeroPadding(String value) {
        return String.format("%2s", value).replace(" ", "0");
    }

    /**
     * 実績時間取得
     * @param startTime 始業時間
     * @param endTime 終業時間
     * @return timeDiff 実績時間
     */
    public static double getPerformanceTime(String startTime,String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date dateTo = null;
        Date dateFrom = null;

        // 日付を作成します。
        try {
            dateFrom = sdf.parse(startTime);
            dateTo = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 日付をlong値に変換します。
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();

        // 差分の時間を算出します。
        double timeDiff = (dateTimeTo - dateTimeFrom  ) / (1000 * 60);

        double resultTimeDiff = timeDiff / 60;
        final double fourHours = 4.0;
        final double fifteenHours = 15.0;
        //結果4時間以上で-1時間 15時間で-1時間する
        if(fourHours <= resultTimeDiff){
            resultTimeDiff = resultTimeDiff - 1;
        }
        if(fifteenHours <= resultTimeDiff){
            resultTimeDiff = resultTimeDiff - 1;
        }
        return resultTimeDiff;
    }

    /**
     * 今日の日付をYYYYMM形式で取得します。
     */
    public static String getNowYYYYMM(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(date);
    }

    /**
     * 今日の日付をDD形式で取得します。
     */
    public static String getNowDay(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(date);
    }


    /**
     * 引数で渡されたカレンダーを表示します。
     * @param year 年
     * @param month 月
     * @return	月、日、曜日
     */
    public static ArrayList<String> getDayOfTheMonth(String year, String month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        //セットした年、月の最大の日付を入れる
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        ArrayList<String> date = new ArrayList<>();
        /* 今月分の日付を格納する */
//        for (int day = 1 ; day <= maxDay ; day++){
//            date.add((month + "月" + day +  "日" + getWeek(
//                    Integer.parseInt(year), Integer.parseInt(month), day)));
//        }
        for (int day = 1 ; day <= maxDay ; day++){
            date.add((day +  "日" + getWeek(
                    Integer.parseInt(year), Integer.parseInt(month), day)));
        }
        return date;
    }

    /**
     * 曜日を取得します。
     * @param year 年
     * @param month 月
     * @param day 日
     * @return	曜日
     */
    public static String getWeek(int year, int month, int day) {
        String stringWeek = "";
        Calendar cal = Calendar.getInstance();
        cal.set(year, month -1, day);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                stringWeek = "(日)";
                break;
            case Calendar.MONDAY:
                stringWeek = "(月)";
                break;
            case Calendar.TUESDAY:
                stringWeek = "(火)";
                break;
            case Calendar.WEDNESDAY:
                stringWeek = "(水)";
                break;
            case Calendar.THURSDAY:
                stringWeek = "(木)";
                break;
            case Calendar.FRIDAY:
                stringWeek = "(金)";
                break;
            case Calendar.SATURDAY:
                stringWeek = "(土)";
                break;
        }
        return  stringWeek;
    }

    /**
     * 休日判定
     * @param year 年
     * @param month 月
     * @param day 日
     * @return	boolean
     */
    public static boolean isHoliday(String year, String month, String day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        boolean holiday = false;
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return true;
            case Calendar.SATURDAY:
                return true;
        }
        return MsGeneralCode.isPublicHoliday(year, month, day);
    }

    /**
     * 深夜作業時間取得
     * @param endTime 終業時間
     * @return timeDiff 深夜作業時間
     */
    public static double getLateNightWorkTime(String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date dateTo = null;
        Date dateFrom = null;
        double timeDiff = 0.0;
        String baseTime = "22:00";
        // 日付を作成します。
        try {
            dateFrom = sdf.parse(baseTime);
            dateTo = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 日付をlong値に変換します。
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();

        if (dateTimeTo > dateTimeFrom) {
            // 差分の時間を算出します。
            timeDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60);
            timeDiff = timeDiff / 60;

            if (timeDiff > 3) {
                timeDiff--;
            }
        }
        return timeDiff;
    }

    /**
     * 月間の所定労働時間を取得します。
     * @return 結果
     */
    public static double getDefaultWorkTime(String year, String month){
    	//所定労働時間/日を取得
    	double dailyWorkTime = MsGeneralCode.getAnyValue1ByCode("01","DEFAULT_DAILY_WORK_TIME");

    	//当月の最大日付を取得
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        //当月の平日の日数取得
        int workDay = getWorkDay(year, month, maxDay);

        //所定労働時間(日)*日数
    	double defalutTime = dailyWorkTime * workDay;

		return defalutTime;
    }

    /*
     * 月間の平日を取得します。
     */
    public static int getWorkDay(String year, String month,int maxDay) {
    	int workDay = 0;
        // 当月の最大日付まで処理
        for (int day = 1 ; day <= maxDay ; day++) {
           	boolean isHoliday = isHoliday(year, month, DateUtil.getZeroPadding(String.valueOf(day)));
           	if(!isHoliday) {
           		workDay ++;
           	}
        }
		return workDay;
	}

    /**
     * 画面を表示用の日付リストを返します。
     * @param year 年(yyyy形式)
     * @param month 月(1~12)
     * @return DateList
     */
    public static List<DateList> getDateList(String year, String month) {
        List<DateList> dl = new ArrayList<>();
        int day = 0;
        // 当月の最大日付まで処理
        for (String d: DateUtil.getDayOfTheMonth(year, month)) {
            DateList dateList = new DateList();
            dateList.stringDate = d;
            dateList.monthsYears = year + month;
            dateList.date = DateUtil.getZeroPadding(String.valueOf(++day));
            dateList.dateId = "date" + year + month + String.valueOf(day);
            dateList.isHoliday = DateUtil.isHoliday(year, month, DateUtil.getZeroPadding(String.valueOf(day)));
            dl.add(dateList);
        }
        return dl;
    }


}
