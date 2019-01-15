package services.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.avaje.ebean.SqlRow;

import common.Const;
import models.MsEmployee;
import models.MsGeneralCode;
import models.MsPerformanceManage;
import models.TblLoginInfo;
import models.TblPerformance;
import models.TblPerformanceAdmin;
import models.TblYearMonthAttribute;
import models.form.ApproveForm;
import models.form.AttendanceInputForm;
import models.form.AttendanceSumForm;
import models.form.DateList;
import models.form.OvertimeListForm;
import models.form.OvertimeListForm;
import models.form.RegistEmpForm;
import models.form.StatusAndWorkForm;

/**
 * Created by suzuki-daisuke on 2017/04/13.
 */
public class MakeModelUtil {


    /**
     * 社員マスタ登録用のデータを作成します
     * @param registEmpForm
     * @return 社員マスタ
     */
    public static MsEmployee makeMsEmployeeTbl(RegistEmpForm registEmpForm){

    	MsEmployee mst = new MsEmployee();
    	mst.employeeNo = registEmpForm.employeeNo;
    	mst.employeeName = registEmpForm.employeeName;
    	mst.employeeNameKana = registEmpForm.employeeNameKana;
    	mst.positionCode = registEmpForm.positionCode;
    	mst.employmentClass = registEmpForm.employmentClass;
    	mst.departmentCode = registEmpForm.departmentCode;
    	mst.divisionCode = registEmpForm.divisionCode;
    	mst.breakdownName1 = registEmpForm.breakdownName1;
    	mst.breakdownName2 = registEmpForm.breakdownName2;
    	mst.breakdownName3 = registEmpForm.breakdownName3;
    	mst.breakdownName4 = registEmpForm.breakdownName4;
    	mst.authorityClass = registEmpForm.authorityClass;
        return mst;
    }

    /**
     * 社員情報取得用のフォームを作成します
     * @param 社員情報sqlリスト
     * @return 社員登録用フォーム
     */
	public static List<RegistEmpForm> makeRegistEmpForm(List<SqlRow> sqlList){
        List<RegistEmpForm> refList = new ArrayList<>();
        for(SqlRow pd : sqlList) {
            String employeeNo = "";
            String employeeName = "";
            String employeeNameKana = "";
            String authorityClass = "";
            String authorityClassName = "";
            String employmentClass = "";
            String employmentClassName = "";
            String positionCode = "";
            String positionName = "";
            String departmentCode = Const.DEFAULT_CODE;
            String departmentName = "";
            String divisionCode = Const.DEFAULT_CODE;
            String divisionName = "";
            List<String> businessCode = new ArrayList<>();
            List<String> businessName = new ArrayList<>();
            List<String> businessTeamCode = new ArrayList<>();
            List<String> businessTeamName = new ArrayList<>();
            String breakdownName1 = Const.DEFAULT_BREAKDOWN_NAME1;
            String breakdownName2 = Const.DEFAULT_BREAKDOWN_NAME2;
            String breakdownName3 = Const.DEFAULT_BREAKDOWN_NAME3;
            String breakdownName4 = Const.DEFAULT_BREAKDOWN_NAME4;
            String retirementDate = "";

            	employeeNo = pd.getString("employee_no");
            	employeeName = Optional.ofNullable(pd.getString("employee_name")).orElse(employeeName);
            	employeeNameKana = Optional.ofNullable(pd.getString("employee_name_kana")).orElse(employeeNameKana);
            	authorityClass = pd.getString("authority_class");
            	authorityClassName = MsGeneralCode.getCodeMaster(
                        Const.AUTHORITY_CODE_NAME, authorityClass).getString("CODE_NAME");
            	employmentClass = pd.getString("employment_class");
            	employmentClassName = MsGeneralCode.getCodeMaster(
                        Const.EMPLOYMENT_CODE_NAME, employmentClass).getString("CODE_NAME");
            	positionCode = pd.getString("position_code");
            	positionName = MsGeneralCode.getCodeMaster(
                        Const.POSITION_CODE_NAME, positionCode).getString("CODE_NAME");
            	departmentCode = pd.getString("department_code");
            	departmentName = MsGeneralCode.getCodeMaster(
                        Const.DEPARTMENT_CODE_NAME, departmentCode).getString("CODE_NAME");
            	divisionCode = pd.getString("division_code");
            	divisionName = MsGeneralCode.getCodeMaster(
                        Const.DIVISION_CODE_NAME, divisionCode).getString("CODE_NAME");
            	//  業務コード　Listに収納
            	String busCode = Const.DEFAULT_CODE ;
            	String busTeamCode = Const.DEFAULT_CODE ;
//                for(SqlRow mst :MsPerformanceManage.getBusCodeByEmpNo(employeeNo)) {
                for(SqlRow mst :MsPerformanceManage.getBusCodeByEmpNo(employeeNo)) {
                	busCode = mst.getString(Const.BUSINESS_CODE_NAME);
                	businessCode.add(busCode);
                	businessName.add(MsGeneralCode.getCodeMaster(
                          Const.BUSINESS_CODE_NAME,busCode).getString("CODE_NAME"));
                	busTeamCode = mst.getString(Const.BUSINESS_TEAM_CODE_NAME);
                	businessTeamCode.add(busTeamCode);
	                businessTeamName.add(MsGeneralCode.getCodeMaster(
	                            Const.BUSINESS_TEAM_CODE_NAME,busTeamCode).getString("CODE_NAME"));
                }
                if(businessCode.isEmpty()) {
                	businessCode.add(busCode);
                	businessName.add(MsGeneralCode.getCodeMaster(
                            Const.BUSINESS_CODE_NAME,busCode).getString("CODE_NAME"));
                }
                if(businessTeamCode.isEmpty()) {
                	businessTeamCode.add(busTeamCode);
                	businessName.add(MsGeneralCode.getCodeMaster(
                            Const.BUSINESS_CODE_NAME,busTeamCode).getString("CODE_NAME"));
                }
            	breakdownName1 = Optional.ofNullable(pd.getString("breakdown_name1")).orElse(breakdownName1);
            	breakdownName2 = Optional.ofNullable(pd.getString("breakdown_name2")).orElse(breakdownName2);
            	breakdownName3 = Optional.ofNullable(pd.getString("breakdown_name3")).orElse(breakdownName3);
            	breakdownName4 = Optional.ofNullable(pd.getString("breakdown_name4")).orElse(breakdownName4);
            	retirementDate = Optional.ofNullable(pd.getString("retirement_date")).orElse(retirementDate);

            RegistEmpForm ref = new RegistEmpForm();
        	ref.employeeNo = employeeNo;
        	ref.employeeName = employeeName;
        	ref.employeeNameKana = employeeNameKana;
        	ref.authorityClass = authorityClass;
        	ref.authorityClassName = authorityClassName;
        	ref.employmentClass = employmentClass;
        	ref.employmentClassName = employmentClassName;
        	ref.positionCode = positionCode;
        	ref.positionName = positionName;
        	ref.departmentCode = departmentCode;
        	ref.departmentName = departmentName;
        	ref.divisionCode = divisionCode;
        	ref.divisionName = divisionName;
        	ref.businessCode   = businessCode;
        	ref.businessName   = businessName;
        	ref.businessTeamCode   = businessTeamCode;
        	ref.businessTeamName   = businessTeamName;
        	ref.breakdownName1 = breakdownName1;
        	ref.breakdownName2 = breakdownName2;
        	ref.breakdownName3 = breakdownName3;
        	ref.breakdownName4 = breakdownName4;
        	refList.add(ref);
            }
        return refList;
    }

    /**社員業務管理マスタ登録用のデータを作成します。
     * @param registEmpForm
     * @return 社員業務管理マスタ
     */
    public static MsPerformanceManage makeMsPerformanceManage(String empNo, String busCode,String busTeamCode,String sesEmpNo){
    	MsPerformanceManage mst = new MsPerformanceManage();

	    mst.startDate = DateUtil.getDateFormat();
	    mst.endDate = DateUtil.getEndOfFiscalYear(DateUtil.getDateFormat()) + "-03-31";
	    mst.employeeNo = empNo;
	    mst.businessCode   = busCode;
	    mst.businessTeamCode   = busTeamCode;
	    mst.businessManageAuthClass = "02";
	    mst.registUserId = sesEmpNo;
	    mst.updateUserId = sesEmpNo;

        return mst;
    }

    /**
     * ログイン情報登録用のデータを作成します。
     * @param empNo
     * @param password
     * @return
     */
    public static TblLoginInfo makeTblInfo(String empNo,String password){
    	TblLoginInfo mst = new TblLoginInfo();
    	mst.employeeNo = empNo;
    	mst.password = password;
    	mst.loginCount = 0;
    	mst.loginNgCount = 0;
    	mst.isAccountLock = "0";
    	mst.isDelete = "0";
        return mst;
    }

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
                    Const.BUSINESS_CODE_NAME, ymat.getString("business_code")).getString("ANY_VALUE1");

            sawf.businessName = MsGeneralCode.getCodeMaster(
                    Const.BUSINESS_CODE_NAME, ymat.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = Optional.ofNullable(ymat.getString("business_team_code")).orElse("");

            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    Const.BUSINESS_TEAM_CODE_NAME, ymat.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = ymat.getString("department_code");

            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    Const.DEPARTMENT_CODE_NAME, ymat.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = ymat.getString("division_code");
            sawf.divisionName = MsGeneralCode.getCodeMaster(
                    Const.DIVISION_CODE_NAME, ymat.getString("division_code")).getString("CODE_NAME");
            sawf.breakdownName1 = ymat.getString("breakdown_name1");
            sawf.breakdownName2 = Optional.ofNullable(ymat.getString("breakdown_name2")).orElse("");
            sawf.breakdownName3 = Optional.ofNullable(ymat.getString("breakdown_name3")).orElse("");
            sawf.breakdownName4 = Optional.ofNullable(ymat.getString("breakdown_name4")).orElse("");
            sawf.monthsYearsStatus = ymat.getString("months_years_status");
        	}catch (Exception e) {

        		System.out.println(CheckUtil.getClassName()+ " " +e);
			}
        } else {
            // 社員マスタから社員情報を取得
            SqlRow empInfo = MsEmployee.getEmployeeInfo(empNo);

            sawf.monthsYears = monthsYears;
            sawf.employeeName = empInfo.getString("employee_name");
            sawf.employeeNo = empNo;
            sawf.businessCode = empInfo.getString("business_code");

            sawf.businessCodeDisp = MsGeneralCode.getCodeMaster(
                    Const.BUSINESS_CODE_NAME, empInfo.getString("business_code")).getString("ANY_VALUE1");

            sawf.businessName = MsGeneralCode.getCodeMaster(
                    Const.BUSINESS_CODE_NAME, empInfo.getString("business_code")).getString("CODE_NAME");
            sawf.businessTeamCode = empInfo.getString("business_team_code");

            sawf.businessTeamName = MsGeneralCode.getCodeMaster(
                    Const.BUSINESS_TEAM_CODE_NAME, empInfo.getString("business_team_code")).getString("CODE_NAME");
            sawf.departmentCode = empInfo.getString("department_code");

            sawf.departmentName = MsGeneralCode.getCodeMaster(
                    Const.DEPARTMENT_CODE_NAME, empInfo.getString("department_code")).getString("CODE_NAME");
            sawf.divisionCode = empInfo.getString("division_code");

            sawf.divisionName = MsGeneralCode.getCodeMaster(
                    Const.DIVISION_CODE_NAME, empInfo.getString("division_code")).getString("CODE_NAME");
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
					System.out.println(CheckUtil.getClassName()+ " " +e);
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
            String holidayClassCode = Const.DEFAULT_CODE;
            String holidayClassName = "";
            String shiftClassCode = Const.DEFAULT_CODE;
            String shiftClassName = "";
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
                    holidayClassName = MsGeneralCode.getClassNameByCode(Const.HOLIDAY_CODE_NAME,holidayClassCode);
                    shiftClassCode = Optional.ofNullable(pd.getString("shift_class")).orElse(shiftClassCode);
                    shiftClassName = MsGeneralCode.getClassNameByCode(Const.SHIFT_CODE_NAME, shiftClassCode);
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
            aif.holidayClassName = holidayClassName;
            aif.shiftClassCode = shiftClassCode;
            aif.shiftClassName = shiftClassName;
            aif.performanceStatus = performanceStatus;

            if (!"".equals(closingTime)) {
                aif.holidayClassName = MsGeneralCode.getClassNameByCode(Const.HOLIDAY_CODE_NAME,holidayClassCode);
                aif.salaried = MsGeneralCode.getAnyValue1ByCode(holidayClassCode,Const.HOLIDAY_CODE_NAME);
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
    		bsCode = MsGeneralCode.getCodeMaster(Const.BUSINESS_CODE_NAME, appList.getString("bs_code"));
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
    		if(!holidayClass.equals(Const.DEFAULT_CODE)) {
    			holidayClass = MsGeneralCode.getCodeMaster(Const.HOLIDAY_CODE_NAME,holidayClass).getString("code_name");
    			map.put("休暇区分",holidayClass);
    			contentsList.add(map);
    		}
    		// シフト区分
    		String shiftClass =  appList.getString("shi_cl");
    		if(!shiftClass.equals(Const.DEFAULT_CODE)){
    			shiftClass = MsGeneralCode.getCodeMaster(Const.SHIFT_CODE_NAME,shiftClass).getString("code_name");
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
     * 残業一覧用データを作成します。
     * @param performanceData performanceData
     * @return List<OvertimeListForm>
     */
    //TODO 1/11 年月ごとの残業代を求めるメソッドを別につくる
    public static List<OvertimeListForm> makeOvertimeForm(List<SqlRow> performanceData,String year,String month) {
    	//所定労働時間/月を取得
    	double defaultWorkTime = DateUtil.getDefaultWorkTime(year, month);

    	List<OvertimeListForm> aifList = new ArrayList<>();
    	String empNo = "";
    	String empName = "";
    	String positionName = "";
    	String workCode = "";
    	String teamLeader = "";
    	double sumDeductionNight = 0.0;
    	double sumDeductionOther = 0.0;
    	double sumPerformanceTime = 0.0;
    	double overTime = 0.0;

    	for(SqlRow pd : performanceData) {
    		empNo = pd.getString("employee_no");
    		empName = pd.getString("employee_name");
    		positionName = MsGeneralCode.getCodeMaster(Const.POSITION_CODE_NAME, pd.getString("position_code")).getString("code_name");
    		workCode = MsGeneralCode.getCodeMaster(Const.BUSINESS_CODE_NAME, pd.getString("business_code")).getString("code_name");
    		teamLeader = pd.getString("leader");
    		sumDeductionNight = Optional.ofNullable(pd.getDouble("sum_deduction_night")).orElse(sumDeductionNight);
    		sumDeductionOther = Optional.ofNullable(pd.getDouble("sum_deduction_other")).orElse(sumDeductionOther);
    		sumPerformanceTime = Optional.ofNullable(pd.getDouble("sum_performance_time")).orElse(sumPerformanceTime);
        	// 残業時間/月の計算
    		overTime = sumPerformanceTime - defaultWorkTime;
    		if(Math.signum(overTime) == -1) {
    			overTime = 0.0;
    		}

    		OvertimeListForm aif = new OvertimeListForm();
    		aif.employeeNo = empNo;
    		aif.employeeName = empName;
    		aif.year = year;
    		aif.month = month;
    		aif.positionName= positionName;
    		aif.workCode = workCode;
    		aif.teamLeader = teamLeader;
    		aif.sumDeductionNight = sumDeductionNight;
    		aif.sumDeductionOther = sumDeductionOther;
    		aif.sumPerformanceTime = sumPerformanceTime;
    		aif.overTime = overTime;
    		aifList.add(aif);
    	}
    	return aifList;
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
    public static List<MsGeneralCode> makeCodeTypeList(String codeType,List<String> anyValue2){
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
     * 年リストを作成します。
     * @return List<MGeneralCode>
     */
    public static List<MsGeneralCode> makeYearList(){
        List<MsGeneralCode> mgcList = new ArrayList<>();

        for (SqlRow mgc : MsGeneralCode.getYearList()) {
            MsGeneralCode mGeneralCode = new MsGeneralCode();
            mGeneralCode.targetYear = mgc.getString("TARGET_YEAR");
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