package services.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.avaje.ebean.SqlRow;

import akka.japi.Option;
import common.Const;
import models.MsEmployee;
import models.MsGeneralCode;
import models.TblPerformance;
import models.TblYearMonthAttribute;
import models.form.ApproveForm;
import models.form.ApproveFormList;
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
        	try {
            sawf.monthsYears = monthsYears;
            sawf.employeeName = ymat.getString("employee_name");
            sawf.employeeNo = empNo;
            sawf.businessCode = Optional.ofNullable(ymat.getString("business_code")).orElse("");

            sawf.businessCodeDisp = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", ymat.getString("business_code")).getString("ANY_VALUE1");

            sawf.businessName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", ymat.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = Optional.ofNullable(ymat.getString("business_team_code")).orElse("");

            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_TEAM_CODE", ymat.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = ymat.getString("department_code");

            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    "DEPARTMENT_CODE", ymat.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = ymat.getString("division_code");
            sawf.divisionName = MsGeneralCode.getCodeMaster(
                    "DIVISION_CODE", ymat.getString("division_code")).getString("CODE_NAME");
            sawf.breakdownName1 = ymat.getString("breakdown_name1");
            sawf.breakdownName2 = Optional.ofNullable(ymat.getString("breakdown_name2")).orElse("");
            sawf.breakdownName3 = Optional.ofNullable(ymat.getString("breakdown_name3")).orElse("");
            sawf.breakdownName4 = Optional.ofNullable(ymat.getString("breakdown_name4")).orElse("");
            sawf.monthsYearsStatus = ymat.getString("months_years_status");
        	}catch (Exception e) {

        		System.out.println(e);
			}
        } else {
            // 社員マスタから社員情報を取得
            SqlRow empInfo = MsEmployee.getEmployeeInfo(empNo);

            sawf.monthsYears = monthsYears;
            sawf.employeeName = empInfo.getString("employee_name");
            sawf.employeeNo = empNo;
            sawf.businessCode = empInfo.getString("business_code");

            sawf.businessCodeDisp = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", empInfo.getString("business_code")).getString("ANY_VALUE1");

            sawf.businessName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_CODE", empInfo.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = empInfo.getString("business_team_code");

            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    "BUSINESS_TEAM_CODE", empInfo.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = empInfo.getString("department_code");

            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    "DEPARTMENT_CODE", empInfo.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = empInfo.getString("division_code");

            sawf.divisionName = MsGeneralCode.getCodeMaster(
                    "DIVISION_CODE", empInfo.getString("division_code")).getString("CODE_NAME");
            sawf.breakdownName1 = empInfo.getString("breakdown_name1");
            sawf.breakdownName2 = empInfo.getString("breakdown_name2");
            sawf.breakdownName3 = empInfo.getString("breakdown_name3");
            sawf.breakdownName4 = empInfo.getString("breakdown_name4");
            sawf.monthsYearsStatus = Const.MONTHS_YEARS_STATUS_UNFIX;
            // 年月属性テーブルを新規作成
                // FormからEntityに詰め替え
                TblYearMonthAttribute tblYearMonthAttribute = MakeModelUtil.makeYearMonthAttributeTbl(
                		sawf.employeeNo, sawf.monthsYears, sawf);
                tblYearMonthAttribute.monthsYearsStatus = Const.PERFORMANCE_STATUS_SAVE;
                tblYearMonthAttribute.registUserId = sawf.employeeNo;
                tblYearMonthAttribute.updateUserId = sawf.employeeNo;
                try {
					TblYearMonthAttribute.insertYearMonthData(tblYearMonthAttribute);
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					System.out.println(e);
				}
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
            String shiftClassCode = Const.SHIFT_CLASS_NOTHING;
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
                    shiftClassCode = Optional.ofNullable(pd.getString("shift_class")).orElse(shiftClassCode);
                    remarks = Optional.ofNullable(pd.getString("remarks")).orElse(remarks);
                    performanceStatus = Optional.ofNullable(pd.getString("performance_status")).orElse(performanceStatus);
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
            aif.shiftClassCode = shiftClassCode;
            aif.performanceStatus = performanceStatus;

            if (!"".equals(closingTime)) {
                aif.holidayClassName = MsGeneralCode.getClassNameByCode(holidayClassCode);
                aif.salaried = MsGeneralCode.getAnyValue1ByCode(holidayClassCode,"HOLIDAY_CLASS");
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
     * 承認一覧用データを作成します。
     * @param performanceData performanceData
     * @return List<AttendanceInputForm>
     */
    public static List<ApproveForm> makeApproveInputFormList(List<SqlRow> performanceData) {
    	//表示用フォーム
    	List<ApproveForm> approveFormList = new ArrayList<>();

    	Integer appNo =0;
    	String businessCode = "";
    	String employeeNo = "";
    	String employeeName = "";
    	String monthsYears = "";
        String performanceDate = "";
        String contents = "";
        String remarks = "";
        String performanceStatus = "";
        Boolean isApprove;
        String approvalEmployeeNo = "";
        String approvalDate = "";
        String approvalEmployeeName = "";
        String monthsYears_status = "";

    	for(SqlRow appList : performanceData){
    		appNo ++;
    		// 業務コード(BUSINESS_CODE)の名称を取得
    		SqlRow bsCode = null;
    		bsCode = MsGeneralCode.getCodeMaster("BUSINESS_CODE", appList.getString("bs_code"));
    		businessCode = bsCode.getString("code_name");
    		// 社員番号
    		employeeNo = appList.getString("emp_no");
    		// 社員氏名
    		employeeName = appList.getString("emp_name");
    		// 年月
    		monthsYears = appList.getString("mon_yr");
    		// 日
    		performanceDate = appList.getString("per_date");
    		// TODO 休暇区分・シフト区分・開始時間・終了時間によって表記変更
    		// コンテンツを詰め込むためのリスト
    		ArrayList<HashMap> contentsList = new ArrayList<>();
    		HashMap<String, String> map = new HashMap<>();
    		// 休暇区分
    		String holidayClass =  appList.getString("ho_cl");
    		contents = holidayClass;
    		if(!holidayClass.equals(Const.HOLIDAY_CLASS_NOTHING)) {
    			holidayClass = MsGeneralCode.getCodeMaster("HOLIDAY_CLASS",holidayClass).getString("code_name");
    			map.put("休暇区分",holidayClass);
    			contentsList.add(map);
    		}
    		// シフト区分
    		String shiftClass =  appList.getString("shi_cl");
    		if(!shiftClass.equals(Const.SHIFT_CLASS_NOTHING)){
    			shiftClass = MsGeneralCode.getCodeMaster("SHIFT_CLASS",shiftClass).getString("code_name");
    			map.put("シフト区分",shiftClass);
    			contentsList.add(map);
    		}
    		//開始時間と終了時間
    		String startTime = appList.getString("opn_time");
    		String endTime = appList.getString("clo_time");
    		if(!startTime.equals("") &&
    				!endTime.equals("")){
    			Double nightOverTime = DateUtil.getLateNightWorkTime(endTime);
    			if (nightOverTime == 0.0) {
    				map.put("勤務時間",startTime + "～" + endTime);
    			} else {
    				map.put("深夜時間超過", endTime + "("  + String.valueOf(nightOverTime) + "h)");
    			}
    			contentsList.add(map);
    		}
    		if(!contentsList.isEmpty()) {
    			contents = contents.replace(contents,map.toString().replace("=", "：").replace("{", "").replace("}", ""));
    		}
    		// 備考欄
    		remarks = appList.getString("rem");
    		// 状況(実績ステータス)
    		performanceStatus = appList.getString("per_st");
    		String performanceStatusStr = MsGeneralCode.getCodeMaster("PERFORMANCE_STATUS", performanceStatus)
    				.getString("code_name");
    		if(performanceStatus.equals(Const.PERFORMANCE_STATUS_APPROVED)) {
    			isApprove = true;
    		} else {
    			isApprove = false;
    		}
    		// 承認者社員番号
    		approvalEmployeeNo = appList.getString("app_emp_no");
    		// TODO 日にち表記がおかしいの直す
    		// 承認日
    		approvalDate = appList.getString("app_date");
    		// 承認者社員氏名
    		approvalEmployeeName = appList.getString("app_emp_name");
    		// 年月別ステータス
    		monthsYears_status = appList.getString("mon_yr_st");

	    	ApproveForm approveForm = new ApproveForm();
	    	approveForm.appNo = appNo;
	    	approveForm.bsCode = businessCode;
	    	approveForm.employeeNo = employeeNo;
	    	approveForm.employeeName = employeeName;
	    	approveForm.monthsYears = monthsYears;
	    	approveForm.performanceDate = performanceDate;
	    	approveForm.contents = contents;
	    	approveForm.remarks = remarks;
	    	approveForm.performanceStatus = performanceStatusStr;
	    	approveForm.isApprove = isApprove;
	    	approveForm.approvalEmployeeNo = approvalEmployeeNo;
	    	approveForm.approvalDate = approvalDate;
	    	approveForm.approvalEmployeeName = approvalEmployeeName;
	    	approveForm.monthsYearsStatus = monthsYears_status;

	    	approveFormList.add(approveForm);
    	}
    	return approveFormList;
    }

    /**
     * コードタイプのリストを作成します。
     * @return List<MGeneralCode>
     */
    public static List<MsGeneralCode> makeCodeTypeList(String codeType){
        List<MsGeneralCode> mgcList = new ArrayList<>();

        for (SqlRow mgc : MsGeneralCode.getCodeMasterList(codeType)) {
            MsGeneralCode mGeneralCode = new MsGeneralCode();
            mGeneralCode.code = mgc.getString("code");
            mGeneralCode.codeName = mgc.getString("code_name");
            mgcList.add(mGeneralCode);
        }
        return mgcList;
    }

    /**
     * コードタイプのリストをanyvalue2から作成します。
     * @return List<MGeneralCode>
     */
    public static List<MsGeneralCode> makeCodeTypeList(String codeType,String anyValue2){
        List<MsGeneralCode> mgcList = new ArrayList<>();

        for (SqlRow mgc : MsGeneralCode.getCodeListByAnyValue2(codeType,anyValue2)) {
            MsGeneralCode mGeneralCode = new MsGeneralCode();
            mGeneralCode.code = mgc.getString("code");
            mGeneralCode.codeName = mgc.getString("code_name");
            mGeneralCode.anyValue2 = mgc.getString("any_value2");
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