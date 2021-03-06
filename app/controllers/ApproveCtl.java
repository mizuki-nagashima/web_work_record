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
import services.utils.CheckUtil;
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

    	String yearMonth = year + DateUtil.getZeroPadding(month);
    	String appEmp = session("employeeNo");
    	String appName = session("employeeName");
    	String authorityClass = session("authorityClass");

    	if(!session("authorityClass").equals(Const.AUTHORITY_CLASS_CHECK) ||
    			!session("authorityClass").equals(Const.AUTHORITY_CLASS_SELF)) {

    		// 業務チームコード取得
    	List<SqlRow> sqlBusinessTeamCodeList = getBusinessTeamCode();
    	List<String> businessTeamCodeList = new ArrayList<>();
    	for(SqlRow sqlrow :sqlBusinessTeamCodeList) {
    		businessTeamCodeList.add(sqlrow.getString("business_team_code"));
    	}
       	// 承認申請されたデータを取得
    	List<SqlRow> sqlList = TblPerformance.getApproveList(businessTeamCodeList, yearMonth);

        // 表示用フォーム
    	ApproveFormList apfl = new ApproveFormList();

    	apfl.approveFormList = MakeModelUtil.makeApproveInputFormList(sqlList);

    	Form<ApproveFormList> appForm = formFactory.form(ApproveFormList.class).fill(apfl);

        return ok(approve.render("承認画面",
         		year,month,
         		apfl.approveFormList
         		,appEmp,appName,authorityClass
        		));

    	} else {
        	return ok(Json.toJson(ImmutableMap.of(
                    "result", "ok",
                    "link",String.valueOf(routes.AttendanceCtl.index(appEmp,year,month))
            )));
    	}
    }

    /**
     * 承認処理
     * @param empNo 該当社員番号
     * @param monthsYears 該当年月
     * @param date 該当日
     * @param flg 承認不可フラグ
     * @return 承認画面
     */
    public Result updateApprove(String empNo, String monthsYears , String  date, Integer flg) {

        // エラーメッセージを詰め込むためのリスト
        ArrayList<HashMap> errorMsgList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

	    try {
	    	String appEmp = session("employeeNo");	// 承認者社員番号
		    if(flg == 0) {
		    	String perStatus = Const.PERFORMANCE_STATUS_APPROVED;
		    	TblPerformance.updateApprove(empNo, monthsYears, date, perStatus, appEmp);
		    } else if(flg == 1) {
		        	String perStatus = Const.PERFORMANCE_STATUS_APPROVAL_NOT;
		        	TblPerformance.updateApprove(empNo, monthsYears, date, perStatus);
		    } else {
		            map.put(date, "承認フラグに不正なデータが入っています");
		            errorMsgList.add(map);
		    }
		} catch (Exception e) {
	       //  debug
	       System.out.println(CheckUtil.getClassName()+ " " +e);
	       map.put(date, "承認処理中にエラーが発生しました。");
	       errorMsgList.add(map);
		}

    	if(!errorMsgList.isEmpty()) {
    		return ok(Json.toJson(ImmutableMap.of("result", "ng","msg",errorMsgList)));
    	} else {
    		return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    	}
    }

    /**
     * 業務チームコード取得処理
     * @param 承認者社員番号
     * @param 年月
     * @return 業務チームコードリスト
     */
    public List<SqlRow> getBusinessTeamCode() {
    	List<SqlRow> resultList = new ArrayList<>();

    	resultList = MsPerformanceManage.getMsPerManage(session("employeeNo"));

    	return resultList;
    }

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

    /**
     * 承認画面の月日から勤怠管理画面に遷移
     * @param empNo
     * @param year
     * @param month
     * @return
     */
    public Result moveAttendanceByApprove(String empNo, String year, String month) {
	    return ok(Json.toJson(
	            ImmutableMap.of(
	                    "result", "ok",
	                    "link", java.lang.String.valueOf(routes.AttendanceCtl.index(empNo,year, month))
	            )));
    }
}
