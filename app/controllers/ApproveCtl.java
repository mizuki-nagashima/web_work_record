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
       	// 承認申請されたデータを取得
    	List<SqlRow> sqlList = TblPerformance.getApproveList(businessTeamCodeList, yearMonth);

        // 表示用フォーム
    	ApproveFormList apfl = new ApproveFormList();

    	apfl.approveFormList = MakeModelUtil.makeApproveInputFormList(sqlList);

    	Form<ApproveFormList> appForm = formFactory.form(ApproveFormList.class).fill(apfl);

        return ok(approve.render("承認画面",
        		appEmp,
         		year,month,
         		apfl.approveFormList
        		));

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
    public Result updateApprove(String emp, String year, String month , String date,Double flg) {
    	System.out.println("きてる？");
    	String appEmp = session("employeeNo");	// 承認者社員番号
        try {
    	if(flg == 0) {
    	System.out.println("承認処理開始");
    	String perStatus = Const.PERFORMANCE_STATUS_APPROVED;
    	TblPerformance.updateApprove(emp, year+month, date, perStatus, appEmp);
    	} else if(flg == 1) {
    		System.out.println("承認不可処理開始");
        	String preStatus = Const.PERFORMANCE_STATUS_APPROVAL_NOT;
        	TblPerformance.updateApprove(emp, year+month, date, preStatus,appEmp);
    	} else {
    		return ok(Json.toJson(ImmutableMap.of("result", "ng")));
    	}

    } catch (Exception e) {
        //  debug
        System.out.println(e);
        return ok(Json.toJson(ImmutableMap.of("result", "ng")));
    }
    	return ok(Json.toJson(ImmutableMap.of("result", "ok")));
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
    	System.out.println("てすと"+empNo+yearMonth+nowYearMonth);

        String Year = yearMonth.substring(0,4);
        String Month = yearMonth.substring(4,6);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result", "ok",
                        "link",String.valueOf(routes.ApproveCtl.index(Year,Month))
                )));
    }
}
