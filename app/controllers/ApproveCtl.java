package controllers;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;


import models.form.LoginForm;
import models.form.StatusAndWorkForm;
import models.MsGeneralCode;
import models.MsPerformanceManage;
import models.TblPerformance;
import models.form.ApproveForm;
import models.form.ApproveFormList;
import models.form.AttendanceInputForm;
import models.form.AttendanceInputFormList;
import models.form.DateList;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;
import views.html.index;
import play.data.validation.*;
import views.html.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.*;

/**
 * Created by otsuka on 2018/04/03.
 */

public class ApproveCtl extends Controller {
    @Inject
    FormFactory formFactory;

    /**
     * 承認画面を表示します。
     * @return 承認画面
     */
    public Result index(String year,String month) {
    	ApproveFormList apfl = new ApproveFormList();
    	List<ApproveForm> approveFormList = new ArrayList<>();
    	MsGeneralCode msGeneralCode = new MsGeneralCode();

    	String yearmonth = year+month;
    	String emp = session("employeeNo");
    	// 業務チームコード取得SQL
    	List<SqlRow> sqlBusinessTeamCodeList = getBusinessTeamCode();
    	List<String> businessTeamCodeList = new ArrayList<>();
    	for(SqlRow sqlrow :sqlBusinessTeamCodeList) {
    		businessTeamCodeList.add(sqlrow.getString("business_team_code"));
    	}
    	// 承認一覧データ取得SQL
    	List<SqlRow> sqlList = TblPerformance.getApproveList(businessTeamCodeList, yearmonth);


    	String business_code = "";
    	String employee_no = "";
    	String employee_name = "";
    	String months_years = "";
        String performance_date = "";
        String holiday_class = "";
        String shift_class = "";
        String remarks = "";
        String performance_status = "";
        String approval_employee_no = "";
        String approval_date = "";
        String approval_position_code = "";
        String approval_employee_name = "";
        String months_years_status = "";

    	for(SqlRow appList : sqlList){
    		// 業務コード(BUSINESS_CODE)の名称を取得
    		SqlRow bs_code = null;
    		bs_code = MsGeneralCode.getCodeMaster("BUSINESS_CODE", appList.getString("bs_code"));
    		business_code = bs_code.getString("code_name");
    		// 社員番号
    		employee_no = appList.getString("emp_no");
    		// 社員氏名
    		employee_name = appList.getString("emp_name");
    		// 年月
    		months_years = appList.getString("mon_yr");
    		// 日
    		performance_date = appList.getString("per_date");
    		// 休暇区分
    		SqlRow ho_cl = null;
    		if (appList.getString("ho_cl") != null && appList.getString("ho_cl") != "") {
	    		ho_cl = MsGeneralCode.getCodeMaster("HOLIDAY_CLASS", appList.getString("ho_cl"));
	    		holiday_class = ho_cl.getString("code_name");
    		} else {
    			holiday_class = appList.getString("code_name");
    		}
    		// シフト区分
    		SqlRow shi_cl = null;
    		if (appList.getString("shi_cl") != null && appList.getString("shi_cl") != "") {
	    		shi_cl = MsGeneralCode.getCodeMaster("SHIFT_CLASS", appList.getString("shi_cl"));
	    		shift_class = shi_cl.getString("code_name");
    		} else {
    			shift_class = appList.getString("code_name");
    		}
    		// 備考欄
    		remarks = appList.getString("rem");
    		// 状況(実績ステータス)
    		SqlRow per_st = null;
    		per_st = MsGeneralCode.getCodeMaster("PERFORMANCE_STATUS", appList.getString("per_st"));
    		performance_status = per_st.getString("code_name");
    		// 承認者社員番号
    		approval_employee_no = appList.getString("app_emp_no");
    		// 承認日
    		approval_date = appList.getString("app_date");
    		// 承認者役職
    		SqlRow app_emp_position = null;
    		if (appList.getString("app_emp_position") != null && appList.getString("app_emp_position") != "") {
    			app_emp_position = MsGeneralCode.getCodeMaster("POSITION_CODE", appList.getString("app_emp_position"));
    			approval_position_code = app_emp_position.getString("code_name");
    		} else {
    			approval_position_code = appList.getString("app_emp_position");
    		}
    		// 承認者社員氏名
    		approval_employee_name = appList.getString("app_emp_name");
    		// 年月別ステータス
    		months_years_status = appList.getString("mon_yr_st");

	    	ApproveForm approveForm = new ApproveForm();
	    	approveForm.bsCode = business_code;
	    	approveForm.employeeNo = employee_no;
	    	approveForm.employeeName = employee_name;
	    	approveForm.monthsYears = months_years;
	    	approveForm.performanceDate = performance_date;
	    	approveForm.holidayClass = holiday_class;
	    	approveForm.shiftClass = shift_class;
	    	approveForm.remarks = remarks;
	    	approveForm.performanceStatus = performance_status;
	    	approveForm.approvalEmployeeNo = approval_employee_no;
	    	approveForm.approvalDate = approval_date;
	    	approveForm.approvalPositionCode = approval_position_code;
	    	approveForm.approvalEmployeeName = approval_employee_name;
	    	approveForm.monthsYearsStatus = months_years_status;

	    	approveFormList.add(approveForm);
    	}

    	apfl.approveFormList = approveFormList;

    	Form<ApproveFormList> appForm = formFactory.form(ApproveFormList.class).fill(apfl);

    	// 年度リスト取得
//    	List<SqlRow> sqlGetApproveYearMonthList = getApproveYearMonth();



        return ok(approve.render("承認画面",
        		approveFormList));
    }

    /**
     * 承認処理
     * @param 実績ステータス
     * @param 承認者社員番号
     * @return 承認画面
     */
    public Result updateApprove() {
    	String perStatus = "04";	// 実績ステータス
    	String appEmp = "00000";	// 承認者社員番号
    	String emp = "00229";	// 社員番号
    	String year = "2018";
    	String month = "04";
    	String date = "02";
    	// 承認したときに更新
    	TblPerformance.updateApprove(emp, year+month, date, perStatus, appEmp);



    	return ok(Json.toJson(ImmutableMap.of(
                "result", "ok",
                "link",String.valueOf(routes.ApproveCtl.index(year,month))
        )));
    }

    /**
     * 承認不可処理
     * @param 実績ステータス
     * @return 承認画面
     */
    public Result updateNotApprove(String perStatus) {
    	//String perStatus = "05";	// 実績ステータス
    	String emp = "00229";	// 社員番号
    	String year = "2018";
    	String month = "04";
    	String date = "02";

    	TblPerformance.updateApprove(emp, year+month, date, perStatus);

    	return ok(Json.toJson(ImmutableMap.of(
                "result", "ok",
                "link", String.valueOf(routes.ApproveCtl.index(year,month)))
        ));
    }

    /**
     * 業務チームコード取得処理
     * @param 承認者社員番号
     * @param 年月
     * @return 業務チームコードリスト
     */
    public List<SqlRow> getBusinessTeamCode() {
    	List<SqlRow> resultList = new ArrayList<>();

    	resultList = MsPerformanceManage.getBusinessTeamCode();

    	return resultList;
    }

//    /**
//     * 承認画面年度リスト取得処理
//     * @param 承認者社員番号
//     * @return 業務チームコードリスト
//     */
//    public List<SqlRow> getApproveYearMonth() {
//    	List<SqlRow> resultList = new ArrayList<>();
//        String emp = "00000";
//    	resultList = MsPerformanceManage.getApproveYearMonth(emp);
//
//    	return resultList;
//    }
}
