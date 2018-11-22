package controllers;

import models.TblLoginInfo;
import models.TblYearMonthAttribute;
import models.form.ApproveFormList;
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
import models.MsGeneralCode;
import play.Logger;
import views.html.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class RegistEmpCtl extends Controller {
    @Inject
    FormFactory formFactory;

    /**
     * ログイン画面を表示します。
     * @return ログイン画面
     */
    public Result index() {
    	 // 表示用フォーム
    	RegistEmpForm apfl = new RegistEmpForm();
        Form<RegistEmpForm> registEmpForm = formFactory.form(RegistEmpForm.class).fill(apfl);
        List<MsGeneralCode> departList = MakeModelUtil.makeCodeTypeList(Const.DEPARTMENT_CODE_NAME);
        List<MsGeneralCode> divisionList = MakeModelUtil.makeCodeTypeList(Const.DIVISION_CODE_NAME);
        List<MsGeneralCode> businessList = MakeModelUtil.makeCodeTypeList(Const.BUSINESS_CODE_NAME);
        List<MsGeneralCode> businessTeamList = MakeModelUtil.makeCodeTypeList(Const.BUSINESS_TEAM_CODE_NAME);
    	String sesEmpNo = session("employeeNo");
    	String sesEmpName = session("employeeName");
        return ok(regist_emp.render(sesEmpNo,sesEmpName, registEmpForm,departList,divisionList,businessList,businessTeamList));
    }

    /**
     * 社員登録処理を行います。
     * @return 結果
     */
    public Result registEmp() {
        // エラーメッセージを詰め込むためのリスト
        ArrayList<HashMap> errorMsgList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

        Form<RegistEmpForm> form = formFactory.form(RegistEmpForm.class).bindFromRequest();
        if (form.hasErrors()) {
            ValidationError errorEmpNo = form.error("employeeNo");
            ValidationError errorEmpName = form.error("employeeName");
            String errorEmpNoMsg = "";
            String errorEmpNameMsg = "";
            if (errorEmpNo != null) {
            	errorEmpNoMsg = errorEmpNo.message();
            }
            if (errorEmpName != null) {
            	errorEmpNameMsg = errorEmpName.message();
            }
            map.put("errorEmpNo", errorEmpNoMsg);
            map.put("errorEmpName", errorEmpNameMsg);
            errorMsgList.add(map);

            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg",errorMsgList
                    )));
        }
        //入力値エラーがなかった場合
        RegistEmpForm registEmpForm = form.get();
        String employeeNo = registEmpForm.employeeNo;
        if (!MsEmployee.isRegistEmp(employeeNo)) {

            // TODO ポップアップで入力値確認
            // TODO 社員マスタに登録
        	// TODO ログイン情報に登録
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ok"
                    )));

        } else {
            map.put("errorEmpName", "入力された社員番号は既に存在しています。");
            errorMsgList.add(map);

            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg",errorMsgList
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
