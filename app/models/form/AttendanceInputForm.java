package models.form;


/**
 * Created by suzuki-daisuke on 2017/04/07.
 */

/**
 * 勤怠入力用のフォーム
 */
public class AttendanceInputForm {
    public String employeeNo = "00000";
    public String monthsYears = "000000";
    public String date = "00";
    public String dateId = "";
    public String stringDate = "";
    public String openingTime = "";
    public String closingTime = "";
    public Double breakdown1 = 0.0;
    public Double breakdown2 = 0.0;
    public Double breakdown3 = 0.0;
    public Double breakdown4 = 0.0;
    public Double deductionNight = 0.0;
    public Double deductionOther = 0.0;
    public Double performanceTime = 0.0;
    public String holidayClassCode = "";
    public String holidayClassName = "";
    public String shiftClassCode = "";
    public String shiftClassName = "";
    public String performanceStatus = "";
    public Double nightWork = 0.0;
    public Double salaried = 0.0;
    public String remarks = "";
}