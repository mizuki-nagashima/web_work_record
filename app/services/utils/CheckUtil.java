package services.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.Const;
import models.MsGeneralCode;
import play.Logger;

/**
 * チェックに関する共通クラス
 */
public class CheckUtil {

    /**
     * 作業実績内訳チェック
     * @param break1 作業内訳1
     * @param break2 作業内訳2
     * @param break3 作業内訳3
     * @param break4 作業内訳4
     * @param deductionNight 深夜控除
     * @param deductionOther その他控除
     * @param performance 実績時間
     * @return	boolean
     */
    public static boolean isBreakDownAndDeduction(double break1,double break2,double break3,double break4,double deductionNight,double deductionOther,double performance) {
        // 作業内訳1～4の合計が実績時間と一致するかチェックします。
        if (break1 + break2 + break3 + break4 == performance){
            return true;
        }
        return false;
    }



    /**
     * 始業終業時間チェック
     * @param startTime 始業時間
     * @param endTime 終業時間
     * @return boolean
     */
    public static boolean isStartEndTime(String startTime,String endTime) {
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

        // 始業時間より終業時間のが大きいかチェックします。
        if (dateTimeFrom < dateTimeTo){
            return true;
        }
        return false;
    }

    /**
     * 始業終業時間の文字チェック
     * @param startTime 始業時間
     * @param endTime 終業時間
     * @return boolean
     */
    public static boolean checkStrStartEndTime(String startTime, String endTime) {
        final String MATCH_NUMBER = "^[0-9]+$";
        //文字数チェック(5文字でtrue)
        if (startTime.length() != 5 && endTime.length() != 5) {
            return false;
        //文字チェック(半角数字のみでtrue)
        } else if (!(startTime.substring(0, 1).matches(MATCH_NUMBER) && startTime.substring(3, 4).matches(MATCH_NUMBER) &&
                 endTime.substring(0, 1).matches(MATCH_NUMBER) && endTime.substring(3, 4).matches(MATCH_NUMBER))) {
            return false;
        //文字チェック(3文字目が":"でtrue)
        } else if (':' != startTime.charAt(2) && ':' != endTime.charAt(2)){
            return false;
        } else {
            return true;
        }
    }

    /**
     * 休暇区分チェック
     * @param year 年
     * @param month 月
     * @param day 日
     * @param startTime 始業時間
     * @param endTime 終業時間
     * @param holidayClassCode 休暇区分コード
     * @return boolean
     */
    public static String isHolidayClass(
            String year, String month, String day, String startTime, String endTime, String holidayClassCode) {

        // 休日かどうか判定します。
        if (DateUtil.isHoliday(year, month, day)) {
            // 休日かつ休暇等区分が01:休日出勤かつ始業、終業時間に値がない場合、エラー
            if (Const.HOLIDAY_CLASS_HOLIDAY_WORK.equals(holidayClassCode) && (startTime == null || endTime == null)) {
                return "【休暇等区分：" + MsGeneralCode.getClassNameByCode(holidayClassCode) +
                        "】が選択されていますが、始業時間と終業時間が入力されていません。";
            // 休日かつ休暇等区分が選択されていないかつ始業、終業時間に値がある場合、エラー
            } else if (Const.HOLIDAY_CLASS_NOTHING.equals(holidayClassCode) && (startTime != null || endTime != null)){
                return "休日に始業時間と終業時間が入力されています。休日出勤した場合は【休暇等区分：" +
            MsGeneralCode.getClassNameByCode(Const.HOLIDAY_CLASS_HOLIDAY_WORK) + "】を選択してください。";
            // 休日かつ休暇等区分が選択されているかつ01:休日出勤以外が選択されている場合、エラー
            } else if (!Const.HOLIDAY_CLASS_HOLIDAY_WORK.equals(holidayClassCode)){
                return "休日に誤った休暇等区分が入力されています。休日出勤した場合は【休暇等区分：" +
            MsGeneralCode.getClassNameByCode(Const.HOLIDAY_CLASS_HOLIDAY_WORK) + "】を選択してください。";
            }
        } else {
            // 平日かつ休暇等区分が1:休日出勤の場合、エラー
            if (Const.HOLIDAY_CLASS_HOLIDAY_WORK.equals(holidayClassCode)) {
                return "平日に【休暇等区分：" +
                        MsGeneralCode.getClassNameByCode(Const.HOLIDAY_CLASS_HOLIDAY_WORK) + "】が選択されています。";
            // 平日かつ休暇等区分が選択されているかつ01:休日出勤以外が選択されているかつ始業、終業時間に値がある場合、エラー
            } else if (!Const.HOLIDAY_CLASS_NOTHING.equals(holidayClassCode)
                    && !Const.HOLIDAY_CLASS_HOLIDAY_WORK.equals(holidayClassCode)
                    && !Const.HOLIDAY_CLASS_HALF_HOLIDAY.equals(holidayClassCode)
                    && (startTime != null || endTime != null)) {
                return "【休暇等区分：" + MsGeneralCode.getClassNameByCode(holidayClassCode) +
                        "】が選択されていますが、始業時間と終業時間が入力されています。";
            }
        }
        return null;
    }

}
