package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import common.Const;
import models.MsEmployee;
import models.MsGeneralCode;
import models.MsPerformanceManage;
import models.TblLoginInfo;
import models.form.RegistEmpForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;
import views.html.regist_emp;

public class RegistEmpCtl extends Controller {
	@Inject
	FormFactory formFactory;

	/**
	 * 社員登録画面を表示します。
	 * @return 社員登録画面
	 */
	public Result index() {
		// 登録されたデータを取得
    	List<SqlRow> sqlList = MsEmployee.getEmployeeInfoList();
    	List<RegistEmpForm> registEmpFormList = MakeModelUtil.makeRegistEmpForm(sqlList);

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

		return ok(regist_emp.render(sesEmpNo, sesEmpName, sesAuthClass,registEmpForm, positionList,departList, divisionList, businessList,
				businessTeamList,registEmpFormList));
	}

	/**
	 * 社員情報登録モーダルボタンが押下されたときのエラーチェックを行います。
	 * @return 結果
	 */
	public Result registEmpCheck() {
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
			if (!errorMsgList.isEmpty()) {
				return ok(Json.toJson(
						ImmutableMap.of(
								"result", "ng",
								"msg", errorMsgList)));
			}
		}
		//社員番号検索
		RegistEmpForm registEmpForm = form.get();
		String empNo = registEmpForm.employeeNo;
		if (MsEmployee.isRegistEmp(empNo)) {
			return ok(Json.toJson(
					ImmutableMap.of(
							"result", "ok",
							"msg","※入力された社員番号は既に使われているため、上書きしますがよろしいですか？")));
		}
		return ok(Json.toJson(
				ImmutableMap.of(
						"result", "ok",
						"msg","")));

	}

	/**
	 *  社員登録処理を行います。
	 * @return 結果
	 */
	public Result registEmp() {
		RegistEmpForm registEmpForm = formFactory.form(RegistEmpForm.class).bindFromRequest().get();

		String sesEmpNo = session("employeeNo");
		String empNo = registEmpForm.employeeNo;
		List<String> busCodeList = registEmpForm.businessCode;
		String busTeamCode = registEmpForm.businessTeamCode;

		try {
			// 社員情報があった場合は更新する;
			if(!MsEmployee.isRegistEmp(empNo)) {
				// 社員マスタに登録
				MsEmployee ymat = MakeModelUtil.makeMsEmployeeTbl(registEmpForm);
				ymat.registUserId = sesEmpNo;
				ymat.updateUserId = sesEmpNo;
				MsEmployee.insertMsEmployee(ymat);
				// ログイン情報を登録
				String password = services.PasswordGenerator.main();
				TblLoginInfo tblInfo = MakeModelUtil.makeTblInfo(empNo,password);
				tblInfo.registUserId = sesEmpNo;
				tblInfo.updateUserId = sesEmpNo;
				TblLoginInfo.insertLoginInfo(tblInfo);
			} else {
				// 社員マスタを更新
				MsEmployee ymat = MakeModelUtil.makeMsEmployeeTbl(registEmpForm);
				MsEmployee.updateMsEmployee(ymat);
			}

			// 社員業務マスタをreplaceする(削除→登録)
			MsPerformanceManage.deleteMsPerManage(empNo);
			// 社員業務管理マスタ登録
				for (String busCode : busCodeList) {
					MsPerformanceManage perManage = MakeModelUtil.makeMsPerformanceManage(empNo,busCode,busTeamCode,sesEmpNo);
					MsPerformanceManage.insertMsPerManage(perManage);
				}
		} catch (Exception e) {
			System.out.println(e);
			return ok(Json.toJson(
					ImmutableMap.of(
							"result", "ng","msg","社員情報登録中にエラーが発生しました。")));
		}
		return ok(Json.toJson(
				ImmutableMap.of(
						"result", "ok","buslist",busCodeList)));
	}

	/**
	 * ユーザ情報の削除
	 * @param empNo
	 * @return
	 */
	public Result deleteEmp(String empNo) {
		// エラーメッセージを詰め込むためのリスト
		ArrayList<HashMap> errorMsgList = new ArrayList<>();
		HashMap<String, String> map = new HashMap<>();
		try {
			// 社員マスタ削除
			MsEmployee.updateRetirementDate(empNo);
			// 社員業務管理マスタ削除
			SqlRow empInfo = MsEmployee.getEmployeeInfo(empNo);
			String businessCode = empInfo.getString("business_code");
			String businessTeamCode = empInfo.getString("business_team_code");
			MsPerformanceManage.updateEndDate(empNo,businessCode,businessTeamCode);
			// ログイン情報を削除
			TblLoginInfo.updateDeleteFlg(empNo,"1");
		} catch (Exception e) {
			//  debug
			System.out.println(e);
			map.put("isRegistEmp", "社員情報を削除中にエラーが発生しました。");
			errorMsgList.add(map);
		}
		if (!errorMsgList.isEmpty()) {
			return ok(Json.toJson(
					ImmutableMap.of(
							"result", "ng",
							"msg", errorMsgList)));
		}
		return ok(Json.toJson(
				ImmutableMap.of(
						"result", "ok",
						"msg","ユーザ情報を削除しました。")));
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
