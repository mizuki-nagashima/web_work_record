package controllers;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import models.TblPerformance;
import models.form.ApproveForm;
import models.form.ApproveFormList;
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

import java.util.ArrayList;
import java.util.List;
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
    public Result index(String year,String month) {
//        Form<OvertimeListForm> overtimeListForm = formFactory.form(OvertimeListForm.class);
    	List<ApproveForm> approveFormList = new ArrayList<>();
        return ok(overtimeList.render("残業一覧画面",year,month,approveFormList,"00216","test","03"));
    }

    /**
     * 残業一覧画面で「年月を指定して移動」時の処理をします。
     * @param empNo 社員番号
     * @param yearMonth 年月(yyyyMM)
     * @return 勤怠管理画面画面
     */
    public Result moveTargetYearMonth(String empNo, String yearMonth, String nowYearMonth) {
        String year = yearMonth.substring(0,4);
        String month = yearMonth.substring(4,6);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result", "ok",
                        "link",String.valueOf(routes.OvertimeListCtl.index(year,month))
                )));
    }

}
