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
import models.MsPerformanceManage;
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
	 * 社員登録画面を表示します。
	 * @return 社員登録画面
	 */
	public Result index() {
		// 表示用フォーム
		RegistEmpForm apfl = new RegistEmpForm();
		apfl.breakdownName1 = Const.DEFAULT_BREAKDOWN_NAME1;
		apfl.breakdownName2 = Const.DEFAULT_BREAKDOWN_NAME2;
		apfl.breakdownName3 = Const.DEFAULT_BREAKDOWN_NAME3;
		apfl.breakdownName4 = Const.DEFAULT_BREAKDOWN_NAME4;
		Form<RegistEmpForm> registEmpForm = formFactory.form(RegistEmpForm.class).fill(apfl);
		List<MsGeneralCode> positionList = MakeModelUtil.makeCodeTypeList(Const.POSITION_CODE_NAME);
		List<MsGeneralCode> departList = MakeModelUtil.makeCodeTypeList(Const.DEPARTMENT_CODE_NAME);
		List<MsGeneralCode> divisionList = MakeModelUtil.makeCodeTypeList(Const.DIVISION_CODE_NAME);
		List<MsGeneralCode> businessList = MakeModelUtil.makeCodeTypeList(Const.BUSINESS_CODE_NAME);
		List<MsGeneralCode> businessTeamList = MakeModelUtil.makeCodeTypeList(Const.BUSINESS_TEAM_CODE_NAME);
		String sesEmpNo = session("employeeNo");
		String sesEmpName = session("employeeName");
		String sesAuthClass = session("authorityClass");
		return ok(regist_emp.render(sesEmpNo, sesEmpName,sesAuthClass, registEmpForm, positionList,departList, divisionList, businessList,
				businessTeamList));
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
			//フォームエラー処理
			if (form.hasErrors()) {
				ValidationError errorEmpNo = form.error("employeeNo");
				ValidationError errorEmpName = form.error("employeeName");
				ValidationError errorEmpNameKana = form.error("employeeNameKana");
				if (errorEmpNo != null) {
					map.put("errorEmpNo", errorEmpNo.message());
				}
				if (errorEmpName != null) {
					map.put("errorEmpName", errorEmpName.message());
				}
				if (errorEmpNameKana != null) {
					map.put("errorEmpNameKana", errorEmpNameKana.message());
				}
				errorMsgList.add(map);
			} else {
				RegistEmpForm registEmpForm = form.get();

				if (!MsEmployee.isRegistEmp(registEmpForm.employeeNo)) {
					try {
					// 社員マスタに登録
					MsEmployee ymat = MakeModelUtil.makeMsEmployeeTbl(registEmpForm);
					System.out.println(registEmpForm.positionCode);
					ymat.registUserId = session("employeeNo");
					ymat.updateUserId = session("employeeNo");
					MsEmployee.insertMsEmployee(ymat);
					// 社員業務管理マスタに登録
					MsPerformanceManage perManage = MakeModelUtil.makeMsPerformanceManage(registEmpForm);
					perManage.registUserId = session("employeeNo");
					perManage.updateUserId = session("employeeNo");
					MsPerformanceManage.insertMsPerManage(perManage);
					// TODO ログインマスタに登録 パスワードどうするか？
					TblLoginInfo tblInfo = MakeModelUtil.makeTblInfo(registEmpForm.employeeNo,session("employeeNo"));
					TblLoginInfo.insertTblInfo(tblInfo);
				} catch (Exception e) {
					//  debug
					System.out.println(e);
					map.put("isRegistEmp", "社員情報を登録中にエラーが発生しました。");
					errorMsgList.add(map);
				}
				} else {
					map.put("isRegistEmp", "入力された社員番号は既に存在します。");
					errorMsgList.add(map);
				}
				// TODO ログイン情報に登録
			}
		if (!errorMsgList.isEmpty()) {
			return ok(Json.toJson(
					ImmutableMap.of(
							"result", "ng",
							"msg", errorMsgList)));
		}
		return ok(Json.toJson(
				ImmutableMap.of(
						"result", "ok")));
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
			String Year = yyyyMM.substring(0, 4);
			String Month = yyyyMM.substring(4, 6);
			return ok(Json.toJson(
					ImmutableMap.of(
							"result", "ok",
							"link", java.lang.String.valueOf(routes.AttendanceCtl.index(empNo, Year, Month)))));
		} catch (Exception e) {
			return notFound();
		}
	}

}
