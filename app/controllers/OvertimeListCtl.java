package controllers;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import models.form.LoginForm;
import models.form.StatusAndWorkForm;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;
import views.html.index;
import views.html.login;
import play.data.validation.*;
import views.html.*;

import java.util.Optional;

/**
 * Created by otsuka on 2018/04/03.
 */

public class OvertimeListCtl extends Controller {
    @Inject
    FormFactory formFactory;

    /**
     * 残業時間一覧画面を表示します。
     * @return 残業一覧画面
     */
    public Result index() {
		String sesEmpNo = session("employeeNo");
		String sesEmpName = session("employeeName");
		String sesAuthClass = session("authorityClass");
//        Form<OvertimeListForm> overtimeListForm = formFactory.form(OvertimeListForm.class);
        return ok(overtimeList.render(sesEmpNo, sesEmpName, sesAuthClass));
    }
}
