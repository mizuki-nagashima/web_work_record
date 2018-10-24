package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import common.Const;
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
import play.data.validation.*;
import views.html.*;

import static models.TblPerformance.*;
import static models.TblYearMonthAttribute.*;

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
    	if(!session("authorityClass").equals(Const.AUTHORITY_CLASS_CHECK) ||
    			!session("authorityClass").equals(Const.AUTHORITY_CLASS_SELF)) {

    	String yearMonth = year + DateUtil.getZeroPadding(month);
    	String appEmp = session("employeeNo");

    	// 業務チームコード取得
    	List<SqlRow> sqlBusinessTeamCodeList = getBusinessTeamCode();
    	List<String> businessTeamCodeList = new ArrayList<>();
    	for(SqlRow sqlrow :sqlBusinessTeamCodeList) {
    		businessTeamCodeList.add(sqlrow.getString("business_team_code"));
    	}
    	// 保存済実績を取得
    	List<SqlRow> sqlList = TblPerformance.getApproveList(businessTeamCodeList, yearMonth);

        // 表示用フォーム
    	ApproveFormList apfl = new ApproveFormList();
    	List<ApproveForm> approveFormList = new ArrayList<>();


    	String businessCode = "";
    	String employeeNo = "";
    	String employeeName = "";
    	String monthsYears = "";
        String performanceDate = "";
        String holidayClass = "";
        String shiftClass = "";
        String remarks = "";
        String performanceStatus = "";
        String approvalEmployeeNo = "";
        String approvalDate = "";
        String approvalPositionCode = "";
        String approvalEmployeeName = "";
        String monthsYears_status = "";
    	try {
    	for(SqlRow appList : sqlList){
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
    		// 休暇区分
    		SqlRow hocl = null;
    		if (appList.getString("ho_cl") != null && appList.getString("ho_cl") != "") {
    			hocl = MsGeneralCode.getCodeMaster("HOLIDAY_CLASS", appList.getString("ho_cl"));
	    		holidayClass = hocl.getString("code_name");
    		} else {
    			holidayClass = appList.getString("code_name");
    		}
    		// シフト区分
    		SqlRow shicl = null;
    		if (appList.getString("shi_cl") != null && appList.getString("shi_cl") != "") {
    			shicl = MsGeneralCode.getCodeMaster("SHIFT_CLASS", appList.getString("shi_cl"));
	    		shiftClass = shicl.getString("code_name");
    		} else {
    			shiftClass = appList.getString("code_name");
    		}
    		// 備考欄
    		remarks = appList.getString("rem");
    		// 状況(実績ステータス)
    		SqlRow perst = null;
    		perst = MsGeneralCode.getCodeMaster("PERFORMANCE_STATUS", appList.getString("per_st"));
    		performanceStatus = perst.getString("code_name");
    		// 承認者社員番号
    		approvalEmployeeNo = appList.getString("app_emp_no");
    		// 承認日
    		approvalDate = appList.getString("app_date");
    		// 承認者役職
    		SqlRow appEmpPosition = null;
    		if (appList.getString("app_emp_position") != null && appList.getString("app_emp_position") != "") {
    			appEmpPosition = MsGeneralCode.getCodeMaster("POSITION_CODE", appList.getString("app_emp_position"));
    			approvalPositionCode = appEmpPosition.getString("code_name");
    		} else {
    			approvalPositionCode = appList.getString("app_emp_position");
    		}
    		// 承認者社員氏名
    		approvalEmployeeName = appList.getString("app_emp_name");
    		// 年月別ステータス
    		monthsYears_status = appList.getString("mon_yr_st");

	    	ApproveForm approveForm = new ApproveForm();
	    	approveForm.bsCode = businessCode;
	    	approveForm.employeeNo = employeeNo;
	    	approveForm.employeeName = employeeName;
	    	approveForm.monthsYears = monthsYears;
	    	approveForm.performanceDate = performanceDate;
	    	approveForm.holidayClass = holidayClass;
	    	approveForm.holidayClass = shiftClass;
	    	approveForm.remarks = remarks;
	    	approveForm.performanceStatus = performanceStatus;
	    	approveForm.approvalEmployeeNo = approvalEmployeeNo;
	    	approveForm.approvalDate = approvalDate;
	    	approveForm.approvalPositionCode = approvalPositionCode;
	    	approveForm.approvalEmployeeName = approvalEmployeeName;
	    	approveForm.monthsYearsStatus = monthsYears_status;

	    	approveFormList.add(approveForm);
    	}

    	apfl.approveFormList = approveFormList;

    	Form<ApproveFormList> appForm = formFactory.form(ApproveFormList.class).fill(apfl);

    	// 年度リスト取得
//    	List<SqlRow> sqlGetApproveYearMonthList = getApproveYearMonth();



        return ok(approve.render("承認画面",
        		appEmp,
         		year,month,
        		approveFormList
        		));

        } catch (Exception e) {
            // FIXME debug
            System.out.println("ぬるぽ？:" +e);
            throw e;
        } finally {
        	return notFound();
        }
    	} else {
        	return ok(Json.toJson(ImmutableMap.of(
                    "result", "ok",
                    "link",String.valueOf(routes.AttendanceCtl.index(year,month))
            )));
    	}
    }

    /**
     * 承認処理
     * @param 実績ステータス
     * @param 承認者社員番号
     * @return 承認画面
     */
    public Result updateApprove(String emp, String year, String month , String date) {
    	System.out.println("承認処理開始");
    	String perStatus = Const.PERFORMANCE_STATUS_APPROVED;	// 実績ステータス
    	String appEmp = session("employeeNo");	// 承認者社員番号
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
    public Result updateNotApprove(String emp, String year,String month,String date) {
    	System.out.println("承認不可処理開始");

    	String preStatus = Const.PERFORMANCE_STATUS_APPROVAL_NOT;
    	String appEmp = session("employeeNo");

    	TblPerformance.updateApprove(emp, year+month, date, preStatus,appEmp);

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

    	resultList = MsPerformanceManage.getBusinessTeamCode(session("employeeNo"));

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

    /**
     * 勤怠管理画面で「年月を指定して移動」時の処理をします。
     * @param empNo 社員番号
     * @param yearMonth 年月(yyyyMM)
     * @return 勤怠管理画面画面
     */
    public Result moveTargetYearMonth(String empNo, String yearMonth, String nowYearMonth) {

        String Year = yearMonth.substring(0,4);
        String Month = yearMonth.substring(4,6);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result", "ok",
                        "link",String.valueOf(routes.ApproveCtl.index(Year,Month))
                )));
    }
}
