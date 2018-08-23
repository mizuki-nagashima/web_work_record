package models.form;


/**
 * Created by suzuki-daisuke on 2017/04/10.
 */

/**
 * 日付のリスト
 */
public class DateList {
    public String stringDate = "";//「4月1日(土)」のようなデータが入る
    public String monthsYears = "000000";//「201704」のようなデータが入る
    public String date = "00";//「1」のようなデータが入る
    public String dateId = "";//「date2017041」のようなデータが入る
    public Boolean isHoliday = false;//休日の場合true
}

