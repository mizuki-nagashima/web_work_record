package controllers;

import models.TblLoginInfo;
import models.TblYearMonthAttribute;
import models.form.LoginForm;
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

import common.Const;
import models.MsEmployee;
import play.Logger;
import views.html.*;
import play.data.validation.*;

import java.util.Optional;

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

            // TODO 権限が05：自陣のみ閲覧以外の場合はメニュー画面に遷移
            SqlRow result = MsEmployee.getEmployeeInfo(employeeNo);
            result.getString("authority_class");

            // FIXME debug
            System.out.println("authority_class ===== " + result.getString("authority_class"));

            if (!Const.AUTHORITY_CLASS_SELF.equals(result.getString("authority_class"))) {
                return ok(
                        menu.render()
                );
            } else {
                return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ok",
                                "link", java.lang.String.valueOf(routes.AttendanceCtl.index(Year, Month))
                        )));
            }


        } else {
        	// ログインNG回数カウントアップ
            int loginNgCount  = TblLoginInfo.getLoginInfo(employeeNo).getInteger("login_ng_count");
            loginNgCount ++;
            TblLoginInfo.loginNgCountUp(employeeNo,loginNgCount);

            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg1","ログインできませんでした。" ,
                            "msg2","社員番号とパスワードをお確かめの上、もう一度お試しください。"
                    )));
        }
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
