package services.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.avaje.ebean.SqlRow;

import common.Const;
import models.MsEmployee;
import models.MsGeneralCode;
import models.TblYearMonthAttribute;
import models.form.AttendanceInputForm;
import models.form.AttendanceSumForm;
import models.form.DateList;
import models.form.StatusAndWorkForm;

/**
 * Created by suzuki-daisuke on 2017/04/13.
 */
public class MakeModelUtil {

    /**
     * 年月属性テーブル用のデータを作成します。
     * @param empNo 社員番号
     * @param monthsYears 年月(yyyyMM)
     * @return YearMonthAttributeTbl
     */
    public static TblYearMonthAttribute makeYearMonthAttributeTbl(
            String empNo, String monthsYears, StatusAndWorkForm statusAndWorkForm){

        TblYearMonthAttribute ymat = new TblYearMonthAttribute();
        ymat.employeeNo = empNo;
        ymat.monthsYears = 	monthsYears;
        ymat.businessCode   = statusAndWorkForm.businessCode;
        ymat.businessTeamCode   = statusAndWorkForm.businessTeamCode;
        ymat.departmentCode = statusAndWorkForm.departmentCode;
        ymat.divisionCode = statusAndWorkForm.divisionCode;
        ymat.breakdownName1 = statusAndWorkForm.breakdownName1;
        ymat.breakdownName2 = statusAndWorkForm.breakdownName2;
        ymat.breakdownName3 = statusAndWorkForm.breakdownName3;
        ymat.breakdownName4 = statusAndWorkForm.breakdownName4;
        ymat.monthsYearsStatus = statusAndWorkForm.monthsYearsStatus;
        return ymat;
    }

    /**
     * ステータスと作業実績内訳詳細用のデータを作成します。
     * @param empNo 社員番号
     * @param monthsYears 年月(yyyyMM)
     * @return StatusAndWorkForm
     */
    public static StatusAndWorkForm makeStatusAndWorkForm(String empNo, String monthsYears){
        StatusAndWorkForm sawf = new StatusAndWorkForm();
        SqlRow ymat = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
        //渡された年月の年月属性テーブルがある場合とない場合で処理を分岐
        if (ymat != null) {
            sawf.monthsYears = monthsYears;
            sawf.employeeName = ymat.getString("employee_name");
            sawf.employeeNo = empNo;
            sawf.businessCode = Optional.ofNullable(ymat.getString("business_code")).orElse("");
            // FIXME Const化
            sawf.businessCodeDisp = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", ymat.getString("business_code")).getString("ANY_VALUE1");
            // FIXME Const化
            sawf.businessName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", ymat.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = Optional.ofNullable(ymat.getString("business_team_code")).orElse("");
            // FIXME Const化
            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_TEAM_CODE", ymat.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = Optional.ofNullable(ymat.getString("department")).orElse("");
            // FIXME Const化
            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    "DEPARTMENT_CODE", ymat.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = Optional.ofNullable(ymat.getString("division_code")).orElse("");
            sawf.breakdownName1 = Optional.ofNullable(ymat.getString("breakdown_name1")).orElse("");
            sawf.breakdownName2 = Optional.ofNullable(ymat.getString("breakdown_name2")).orElse("");
            sawf.breakdownName3 = Optional.ofNullable(ymat.getString("breakdown_name3")).orElse("");
            sawf.breakdownName4 = Optional.ofNullable(ymat.getString("breakdown_name4")).orElse("");
            sawf.monthsYearsStatus = Optional.ofNullable(ymat.getString("months_years_status")).orElse("");
        } else {
            // 社員マスタから社員情報を取得
            SqlRow empInfo = MsEmployee.getEmployeeInfo(empNo);

            sawf.monthsYears = monthsYears;
            sawf.employeeName = empInfo.getString("employee_name");
            sawf.employeeNo = empNo;
            sawf.businessCode = empInfo.getString("business_code");
            // FIXME Const化
            sawf.businessCodeDisp = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", empInfo.getString("business_code")).getString("ANY_VALUE1");
            // FIXME Const化
            sawf.businessName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", empInfo.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = empInfo.getString("business_team_code");
            // FIXME Const化
            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_TEAM_CODE", empInfo.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = empInfo.getString("department_code");
            // FIXME Const化
            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    "DEPARTMENT_CODE", empInfo.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = empInfo.getString("division_code");
            // FIXME Const化
            sawf.divisionName = MsGeneralCode.getCodeMaster(
                    "DIVISION_CODE", empInfo.getString("division_code")).getString("CODE_NAME");
            sawf.breakdownName1 = empInfo.getString("breakdown_name1");
            sawf.breakdownName2 = empInfo.getString("breakdown_name2");
            sawf.breakdownName3 = empInfo.getString("breakdown_name3");
            sawf.breakdownName4 = empInfo.getString("breakdown_name4");
            sawf.monthsYearsStatus = Const.MONTHS_YEARS_STATUS_UNFIX;
        }
        return sawf;
    }

    /**
     * 実績保存用データを作成します。
     * @param dateList 日付リスト
     * @param performanceData performanceData
     * @return List<AttendanceInputForm>
     */
    public static List<AttendanceInputForm> makeAttendanceInputFormList(
            List<DateList> dateList, List<SqlRow> performanceData) {
        List<AttendanceInputForm> aifList = new ArrayList<>();
        for(DateList dl : dateList){
            String openingTime = "";
            String closingTime = "";
            double breakdown1 = 0.0;
            double breakdown2 = 0.0;
            double breakdown3 = 0.0;
            double breakdown4 = 0.0;
            double deductionNight = 0.0;
            double deductionOther = 0.0;
            double performanceTime = 0.0;
            String holidayClassCode = Const.HOLIDAY_CLASS_NOTHING;
            String shifｔClassCode = Const.SHIFT_CLASS_NOTHING;
            String otherApprovalClass = "";
            String performanceStatus = "";

            String remarks = "";

            for(SqlRow pd : performanceData) {
                if (dl.monthsYears.equals(pd.getString("months_years"))
                        && dl.date.equals(pd.getString("performance_date"))) {

                    openingTime = pd.getString("opening_time");
                    closingTime = pd.getString("closing_time");
                    breakdown1 = Optional.ofNullable(pd.getDouble("breakdown1")).orElse(breakdown1);
                    breakdown2 = Optional.ofNullable(pd.getDouble("breakdown2")).orElse(breakdown2);
                    breakdown3 = Optional.ofNullable(pd.getDouble("breakdown3")).orElse(breakdown3);
                    breakdown4 = Optional.ofNullable(pd.getDouble("breakdown4")).orElse(breakdown4);
                    deductionNight = Optional.ofNullable(pd.getDouble("deduction_night")).orElse(deductionNight);
                    deductionOther = Optional.ofNullable(pd.getDouble("deduction_other")).orElse(deductionOther);
                    performanceTime = Optional.ofNullable(pd.getDouble("performance_time")).orElse(performanceTime);
                    holidayClassCode = Optional.ofNullable(pd.getString("holiday_class")).orElse(holidayClassCode);
                    shifｔClassCode = Optional.ofNullable(pd.getString("shift_class")).orElse(shifｔClassCode);
                    remarks = Optional.ofNullable(pd.getString("remarks")).orElse(remarks);
                }
            }
            AttendanceInputForm aif = new AttendanceInputForm();
            aif.monthsYears = dl.monthsYears;
            aif.date = DateUtil.getZeroPadding(dl.date);
            aif.openingTime = openingTime;
            aif.closingTime = closingTime;
            aif.breakdown1 = breakdown1;
            aif.breakdown2 = breakdown2;
            aif.breakdown3 = breakdown3;
            aif.breakdown4 = breakdown4;
            aif.deductionNight = deductionNight;
            aif.deductionOther = deductionOther;
            aif.performanceTime = performanceTime;
            aif.holidayClassCode = holidayClassCode;
            // TODO
            aif.holidayClassCode = holidayClassCode;
            aif.shifｔClassCode = shifｔClassCode;
            aif.otherApprovalClass = otherApprovalClass;
            aif.performanceStatus = performanceStatus;

            if (!"".equals(closingTime)) {
                aif.holidayClassName = MsGeneralCode.getClassNameByCode(holidayClassCode);
                aif.salaried = MsGeneralCode.getSalariedTime(holidayClassCode);
            }
            if (!"".equals(closingTime) && closingTime != null) {
                aif.nightWork = DateUtil.getLateNightWorkTime(closingTime);
            }
            aif.remarks = remarks;
            aifList.add(aif);
        }
        return aifList;
    }

    /**
     * 休暇区分のリストを作成します。
     * @return List<MGeneralCode>
     */
    public static List<MsGeneralCode> makeHolidayClassMst(){
        List<MsGeneralCode> hcmList = new ArrayList<>();
        // FIXME Const化
        for (SqlRow hcm : MsGeneralCode.getCodeMasterList("HOLIDAY_CLASS")) {
            MsGeneralCode mGeneralCode = new MsGeneralCode();
            mGeneralCode.code = hcm.getString("code");
            mGeneralCode.codeName = hcm.getString("code_name");
            hcmList.add(mGeneralCode);
        }
        return hcmList;
    }

    /**
     * シフト区分のリストを作成します。
     * @return List<MGeneralCode>
     */
    public static List<MsGeneralCode> makeShiftClassMst(){
        List<MsGeneralCode> mgcList = new ArrayList<>();
        // FIXME Const化
        for (SqlRow mgc : MsGeneralCode.getCodeMasterList("SHIFT_CLASS")) {
            MsGeneralCode mGeneralCode = new MsGeneralCode();
            mGeneralCode.code = mgc.getString("code");
            mGeneralCode.codeName = mgc.getString("code_name");
            mgcList.add(mGeneralCode);
        }
        return mgcList;
    }

    /**
     *  勤怠各種合計表示フォーム用のデータを作成します。
     * @param
     * @return AttendanceSumForm
     */
    public static AttendanceSumForm makeAttendanceSumForm(List<AttendanceInputForm> aifl){
        Double sumBreakdown1 = 0.0;
        Double sumBreakdown2 = 0.0;
        Double sumBreakdown3 = 0.0;
        Double sumBreakdown4 = 0.0;
        Double sumDeductionNight = 0.0;
        Double sumDeductionOther = 0.0;
        Double sumNightWork = 0.0;
        Double sumSalaried = 0.0;
        Double sumPerformanceTime = 0.0;
        if (aifl != null) {
            for (AttendanceInputForm aif : aifl) {
                sumBreakdown1 = sumBreakdown1 + aif.breakdown1;
                sumBreakdown2 = sumBreakdown2 + aif.breakdown2;
                sumBreakdown3 = sumBreakdown3 + aif.breakdown3;
                sumBreakdown4 = sumBreakdown4 + aif.breakdown4;
                sumDeductionNight = sumDeductionNight + aif.deductionNight;
                sumDeductionOther = sumDeductionOther + aif.deductionOther;
                sumNightWork = sumNightWork + aif.nightWork;
                sumSalaried = sumSalaried + aif.salaried;
                sumPerformanceTime = sumPerformanceTime + aif.performanceTime;
            }
        }
        AttendanceSumForm asf = new AttendanceSumForm();
        asf.sumBreakdown1 = sumBreakdown1;
        asf.sumBreakdown2 = sumBreakdown2;
        asf.sumBreakdown3 = sumBreakdown3;
        asf.sumBreakdown4 = sumBreakdown4;
        asf.sumDeductionNight = sumDeductionNight;
        asf.sumDeductionOther = sumDeductionOther;
        asf.sumNightWork = sumNightWork;
        asf.sumSalaried = sumSalaried;
        asf.sumPerformanceTime = sumPerformanceTime;
        return asf;
    }
}
