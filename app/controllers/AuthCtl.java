package controllers;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import common.Const;
import models.MsEmployee;
import models.TblLoginInfo;
import models.form.LoginForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.utils.DateUtil;
import views.html.login;
import views.html.menu;

public class AuthCtl extends Controller {
    @Inject
    FormFactory formFactory;

    /**
     * ログイン画面を表示します。
     * @return ログイン画面
     */
    public Result index() {
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class);
        return ok(login.render(loginForm));
    }

    /**
     * メニュー画面を表示します。
     * @return メニュー画面
     */
    public Result menu() {
    	LoginForm lForm = new LoginForm();
    	Form<LoginForm> loginForm = formFactory.form(LoginForm.class).fill(lForm);
        final String employeeNo = session("employeeNo");
        final String employeeName = session("employeeName");
        final String authorityClass = session("authorityClass");
        String yyyyMM = DateUtil.getNowYYYYMM();
        String year = yyyyMM.substring(0,4);
        String month = yyyyMM.substring(4,6);
        return ok(menu.render(loginForm,employeeNo,employeeName,authorityClass,year,month));
    }

    /**
     * ログイン処理を行います。
     * @return 結果
     */
    public Result login() {
        Form<LoginForm> form = formFactory.form(LoginForm.class).bindFromRequest();
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
        LoginForm loginForm = form.get();
        String employeeNo = loginForm.employeeNo;
        if (TblLoginInfo.isLogin(employeeNo, loginForm.password)) {
            //ログイン成功時一度セッションクリアをし、社員番号をセッションに持たせる。（セッションの有効期限はブラウザ閉じるまで）
            session().clear();
            session("employeeNo", loginForm.employeeNo);

            String yyyyMM = DateUtil.getNowYYYYMM();
            String Year = yyyyMM.substring(0,4);
            String Month = yyyyMM.substring(4,6);

            // 社員情報を取得し権限情報をセッションへ
            SqlRow result = MsEmployee.getEmployeeInfo(employeeNo);
            if(result.isEmpty()) {
            	 return ok(Json.toJson(
                         ImmutableMap.of(
                                 "result", "ng",
                                 "msg1","ログインできませんでした。" ,
                                 "msg2","社員番号とパスワードをお確かめの上、もう一度お試しください。"
                         )));
            }
            String authority = result.getString("authority_class");
            session("authorityClass", authority);
            session("employeeName", result.getString("employee_name"));

        	// ログイン回数カウントアップ
            int loginCount  = TblLoginInfo.getLoginInfo(employeeNo).getInteger("login_count");
            loginCount ++;
            TblLoginInfo.loginCountUp(employeeNo,loginCount);

           // 権限が05：自陣のみ閲覧以外の場合はメニュー画面に遷移
            if (!Const.AUTHORITY_CLASS_SELF.equals(authority)) {
            	return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ok",
                                "link", java.lang.String.valueOf(routes.AuthCtl.menu())
                        )));
            } else {
                return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ok",
                                "link", java.lang.String.valueOf(routes.AttendanceCtl.index(employeeNo,Year, Month))
                        )));
            }


        } else {
        	// ログインNG回数カウントアップ
            SqlRow info = TblLoginInfo.getLoginInfo(employeeNo);
            if(!info.isEmpty()) {
                int loginNgCount  = info.getInteger("login_ng_count");
                loginNgCount ++;
                TblLoginInfo.loginNgCountUp(employeeNo,loginNgCount);
            }

            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg1","ログインできませんでした。" ,
                            "msg2","社員番号とパスワードをお確かめの上、もう一度お試しください。"
                    )));
        }
    }

    public Result menuRedirect() {
    return ok(Json.toJson(
            ImmutableMap.of(
                    "result", "ok",
                    "link", java.lang.String.valueOf(routes.AuthCtl.menu())
            )));
    }

    /**
     * ログアウト処理を行います。
     * @return 結果
     */
    public Result logout() {
        session().clear();
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result", "ok",
                        "link", java.lang.String.valueOf(routes.AuthCtl.index())
                )));
    }


}
