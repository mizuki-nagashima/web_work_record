package controllers;

import models.TblLoginInfo;
import models.TblYearMonthAttribute;
import models.form.AttendanceInputFormList;
import models.form.RegistEmpForm;
import models.form.StatusAndWorkForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.mysql.jdbc.Messages;

import common.Const;
import models.MsEmployee;
import play.Logger;
import views.html.*;
import play.data.validation.*;

import java.util.Optional;

public class RegistEmpCtl extends Controller {
    @Inject
    FormFactory formFactory;

    /**
     * ログイン画面を表示します。
     * @return ログイン画面
     */
    public Result index() {
        Form<RegistEmpForm> registEmpForm = formFactory.form(RegistEmpForm.class);
        return ok(regist_emp.render(registEmpForm));
    }

    /**
     * 社員登録処理を行います。
     * @return 結果
     */
    public Result registEmp() {
        Form<RegistEmpForm> form = formFactory.form(RegistEmpForm.class).bindFromRequest();
        if (form.hasErrors()) {
            ValidationError errorEmployeeNo = form.error("employeeNo");
            ValidationError errorPassword = form.error("password");
            String errorEmployeeNoMsg = "";
            String errorPasswordMsg = "";
            if (errorEmployeeNo != null) {
                errorEmployeeNoMsg = errorEmployeeNo.message();
            }
            if (errorPassword != null) {
                errorPasswordMsg = errorPassword.message();
            }
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "errorEmployeeNo",errorEmployeeNoMsg,
                            "errorPassword",errorPasswordMsg
                    )));
        }
        //ログインの入力値エラーがなかった場合
        RegistEmpForm registEmpForm = form.get();
        String employeeNo = registEmpForm.employeeNo;
        if (MsEmployee.isRegistEmp(employeeNo)) {

           // 権限が05：自陣のみ閲覧以外の場合はメニュー画面に遷移
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ok"
                    )));

        } else {
        	// ログインNG回数カウントアップ
            int registEmpNgCount  = TblLoginInfo.getLoginInfo(employeeNo).getInteger("registEmp_ng_count");
            registEmpNgCount ++;
            TblLoginInfo.loginNgCountUp(employeeNo,registEmpNgCount);

            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg1","ログインできませんでした。" ,
                            "msg2","社員番号とパスワードをお確かめの上、もう一度お試しください。"
                    )));
        }
        //return notFound();
    }

    /*
     * メニュー画面から勤怠入力画面への遷移です。
     * @param name 移動画面名
     * @return 勤怠入力画面
     */
    public Result menuAttendance() {
    	Form<RegistEmpForm> form = formFactory.form(RegistEmpForm.class).bindFromRequest();
    	String empNo = session("employeeNo");
    	try {
		    	String yyyyMM = DateUtil.getNowYYYYMM();
		        String Year = yyyyMM.substring(0,4);
		        String Month = yyyyMM.substring(4,6);
    	        return ok(Json.toJson(
    	                ImmutableMap.of(
    	                        "result", "ok",
    	                        "link", java.lang.String.valueOf(routes.AttendanceCtl.index(empNo,Year,Month))
    	                )));
		} catch (Exception e) {
			return notFound();
		}
    }

}
