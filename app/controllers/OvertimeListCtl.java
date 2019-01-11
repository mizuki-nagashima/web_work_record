package controllers;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import common.Const;
import models.MsGeneralCode;
import models.MsPerformanceManage;
import models.TblPerformance;
import models.TblPerformanceAdmin;
import models.form.ApproveForm;
import models.form.ApproveFormList;
import models.form.LoginForm;
import models.form.OvertimeListForm;
import models.form.StatusAndWorkForm;
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
    public Result index(String year) {
		String sesEmpNo = session("employeeNo");
		String sesEmpName = session("employeeName");
		String sesAuthClass = session("authorityClass");
		String month = DateUtil.getNowYYYYMM().substring(5, 6);
    	String yearMonth = year + DateUtil.getZeroPadding(month);

    	// 業務チームコード取得
    	List<SqlRow> sqlBusinessTeamCodeList = getBusinessTeamCode();
    	List<String> businessTeamCodeList = new ArrayList<>();
    	for(SqlRow sqlrow :sqlBusinessTeamCodeList) {
    		businessTeamCodeList.add(sqlrow.getString("business_team_code"));
    	}
       	// 承認申請されたデータを取得
    	List<SqlRow> sqlList = TblPerformanceAdmin.getPerformanceData();
    	// TODO 上記参考にしつつ残業一覧データを取得

        // 表示用フォーム
    	List<OvertimeListForm> overList = MakeModelUtil.makeOvertimeForm(sqlList,year,month);
    	List<MsGeneralCode> yearList = MakeModelUtil.makeYearList();

        return ok(overtimeList.render(sesEmpNo, sesEmpName, sesAuthClass,year,month,overList,yearList));
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
}
