package controllers;

import static models.TblPerformance.*;
import static models.TblYearMonthAttribute.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.TimeOfDay;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import common.Const;
import models.MsEmployee;
import models.MsGeneralCode;
import models.TblPerformance;
import models.TblYearMonthAttribute;
import models.form.AttendanceInputForm;
import models.form.AttendanceInputFormList;
import models.form.DateList;
import models.form.StatusAndWorkForm;
import models.form.StatusAndWorkFormList;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.auth.Secured;
import services.utils.CheckUtil;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;
import views.html.attendance;

/**
 * 勤怠管理画面用コントローラです。
 */
//セッション管理用のクラス
/**
 * @author nagashima-mizuki
 *
 */
/**
 * @author nagashima-mizuki
 *
 */
/**
 * @author nagashima-mizuki
 *
 */
@Security.Authenticated(Secured.class)
public class AttendanceCtl extends Controller {

    @Inject
    FormFactory formFactory;

    /**
     * 勤怠管理画面を表示します。
     * @param year 年(yyyy形式)
     * @param month 月(1~12)
     * @return 勤怠管理画面画面
     */
    public Result index(String year, String month) {
        // 最大の月(1月)
        final int MIN_MONTH = 1;
        // 最大の月(月)
        final int MAX_MONTH = 12;
        // 社員番号
        final String employeeNo = session("employeeNo");
        // 社員名
        final String employeeName = session("employeeName");
        // 日付
        List<DateList> dateList = getDateList(year, DateUtil.getZeroPadding(month));

        String monthsYears = year + DateUtil.getZeroPadding(month);
        Double defalutWorkTime = DateUtil.getDefaultWorkTime(year,month);
        Boolean statusDefaultValue = false;
        Boolean existsDefaultValue = false;

        // 保存済実績を取得
        List<SqlRow> performanceData = getPerformanceData(employeeNo, monthsYears);
        SqlRow monthStatusData = getYearMonthData(employeeNo, monthsYears);
        try {
        // 表示用Form
        AttendanceInputFormList aifl = new AttendanceInputFormList();

        if(monthStatusData != null) {
        	statusDefaultValue = true;
        }
        aifl.statusAndWorkFormList = MakeModelUtil.makeStatusAndWorkForm(employeeNo, monthsYears);

        // 指定した年月の実績データが一件でもある場合は初期値をセット
        if (performanceData.size() != 0) {
            existsDefaultValue = true;
        }
        aifl.attendanceInputFormList = MakeModelUtil.makeAttendanceInputFormList(dateList, performanceData);

        Form<StatusAndWorkForm> statusAndWorkForm = formFactory.form(StatusAndWorkForm.class).fill(aifl.statusAndWorkFormList);
        Form<AttendanceInputFormList> inputForm = formFactory.form(AttendanceInputFormList.class).fill(aifl);

        //ステータスのリスト化
        List<MsGeneralCode> hcmList = MakeModelUtil.makeCodeTypeList("HOLIDAY_CLASS");
        List<MsGeneralCode> shiftList = MakeModelUtil.makeCodeTypeList("SHIFT_CLASS");
        List<MsGeneralCode> departList = MakeModelUtil.makeCodeTypeList("DEPARTMENT_CODE");
        List<MsGeneralCode> divisionList = MakeModelUtil.makeCodeTypeList("DIVISION_CODE");
        List<MsGeneralCode> businessList = MakeModelUtil.makeCodeTypeList("BUSINESS_CODE");
        List<MsGeneralCode> businessTeamList = MakeModelUtil.makeCodeTypeList("BUSINESS_TEAM_CODE");

        // 1~12(1月~12月以外がパラメータに入ってきたらエラー)
        if (MIN_MONTH <= Integer.parseInt(month) && MAX_MONTH >= Integer.parseInt(month)) {
            return ok(attendance.render(
                    statusAndWorkForm,
                    inputForm,
                    dateList,
                    year,
                    month,
                    statusDefaultValue,
                    existsDefaultValue,
                    employeeNo,
                    employeeName,
                    defalutWorkTime,
                    departList,
                    divisionList,
                    businessList,
                    businessTeamList,
                    hcmList,
                    shiftList
            ));
        } else {
            return notFound();
        }
	} catch (Exception e) {
        //  debug
        System.out.println(e);
        return notFound();
	}
    }

    /**
     * ステータスと作業実績内訳詳細の情報を保存します。
     * @return 結果
     */
    public Result statusAndWorkSave() {

        // 画面からForm取得
        StatusAndWorkForm statusAndWorkForm = formFactory.form(StatusAndWorkForm.class).bindFromRequest().get();
        // FormからEntityに詰め替え
        TblYearMonthAttribute ymat = MakeModelUtil.makeYearMonthAttributeTbl(
                statusAndWorkForm.employeeNo, statusAndWorkForm.monthsYears, statusAndWorkForm);

        try {
            // 年月属性テーブル情報を取得
            SqlRow targetYearMonthAttributeTbl = TblYearMonthAttribute.getYearMonthData(
                    statusAndWorkForm.employeeNo, statusAndWorkForm.monthsYears);
            // 年月属性テーブルが既に存在する場合は更新、なければ新規作成
            if (targetYearMonthAttributeTbl == null) {
                //  debug
                System.out.println("insert！！！");

                ymat.monthsYearsStatus = Const.PERFORMANCE_STATUS_SAVE;
                ymat.registUserId = statusAndWorkForm.employeeNo;
                ymat.updateUserId = statusAndWorkForm.employeeNo;
                TblYearMonthAttribute.insertYearMonthData(ymat);
            } else {
            	//  debug
                System.out.println("update！！！");

                ymat.monthsYearsStatus = Const.PERFORMANCE_STATUS_SAVE;
                ymat.updateUserId = statusAndWorkForm.employeeNo;
                TblYearMonthAttribute.updateYearMonthData(ymat);
            }
        } catch (Exception e) {
            //  debug
            System.out.println(e);
            return ok(Json.toJson(ImmutableMap.of("result", "ng")));
        }
        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

    /**
     * 勤怠管理画面のフォームの情報を保存します。
     * @param empNo 社員番号
     * @param monthsYears 月(1~12)
     * @return 勤怠管理画面画面
     */
    public Result save(String empNo, String monthsYears) {

        //  debug
        System.out.println("save!!!!!");

        // 画面からForm取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();
        List<AttendanceInputForm> adl = attendanceFormList.attendanceInputFormList;
        // 登録済の実績を取得
        ArrayList<String> performanceDataList = TblPerformance.getPerformanceDataList(empNo, monthsYears);

        // 年月属性を取得
        SqlRow yearMonthData = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
        // OKボタン押下時に年月別ステータスが03：承認済以外の場合に処理を実行する。
        if (yearMonthData != null
                && !Const.MONTHS_YEARS_STATUS_COMPLETE.equals(yearMonthData.getString("months_years_status"))) {

            // 当月の最大の日付分フォームがあるのでその分処理(31日まである月なら31個分)
            for (AttendanceInputForm inputForm : adl){
                String op = inputForm.openingTime;
                String cl = inputForm.closingTime;
                String emptyChar = "";

                // フォームの"始業"と"終業"が入力されている または 休暇等区分が選択されているフォームのみ処理する。
                if ((!emptyChar.equals(op) && !emptyChar.equals(cl))
                        || !Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)) {
                        TblPerformance pft = new TblPerformance();
                        String holidayClassCode = inputForm.holidayClassCode;
                        if (holidayClassCode.isEmpty()) {
                            holidayClassCode = null;
                        }
                        pft.employeeNo = inputForm.employeeNo;
                        pft.monthsYears = inputForm.monthsYears;
                        pft.performanceDate = DateUtil.getZeroPadding(inputForm.date);
                        pft.openingTime = op;
                        pft.closingTime = cl;
                        pft.breakdown1 = inputForm.breakdown1;
                        pft.breakdown2 = inputForm.breakdown2;
                        pft.breakdown3 = inputForm.breakdown3;
                        pft.breakdown4 = inputForm.breakdown4;
                        pft.performanceTime = inputForm.performanceTime;
                        pft.deductionNight = inputForm.deductionNight;
                        pft.deductionOther = inputForm.deductionOther;
                        pft.holidayClass = holidayClassCode;
                        pft.shiftClass = inputForm.shiftClassCode;
                        pft.other_approval_class = "";
                        pft.remarks = inputForm.remarks;
                        pft.performanceStatus = Const.PERFORMANCE_STATUS_SAVE;
                        try {
                            String date = DateUtil.getZeroPadding(inputForm.date);
                            // 登録しようとしている日のデータがある場合は更新、ない場合登録
                            if (performanceDataList.contains(date)) {
                                //
	                            pft.updateUserId = inputForm.employeeNo;
	                            TblPerformance.updatePerformanceData(pft);
                            } else {
                                //
                                pft.registUserId = inputForm.employeeNo;
                                pft.updateUserId = inputForm.employeeNo;
                                TblPerformance.insertPerformanceData(pft);
                            }
	                    } catch (Exception e) {
	        	            //  debug
	        	            System.out.println(e);
	        	            return ok(Json.toJson(
	        	                    ImmutableMap.of(
	        	                            "result", "ng",
	        	                            "msg", "保存処理時エラーが発生しました。もう一度お試しください。"
	        	                    )));
	                    }
                } else if (!TblPerformance.getPerformanceDataByYearMonthAndDate(
                        inputForm.employeeNo,inputForm.monthsYears,inputForm.date).isEmpty()){
                    TblPerformance.deletePerformanceData(inputForm.employeeNo,inputForm.monthsYears,inputForm.date);
                }
            }
        }
        //  debug
        System.out.println("exit save!!!!!");
        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

	/**
     * 「勤怠を保存」押下時チェック処理
     * @param empNo
     * @param monthsYears
     * @return
     */
    public Result saveCheck(String empNo, String monthsYears) {

        //  debug
        System.out.println("saveCheck!!!!!");

        // 画面からForm取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();
        List<AttendanceInputForm> adl = attendanceFormList.attendanceInputFormList;

        // エラーメッセージを詰め込むためのリスト
        ArrayList<HashMap> errorMsgList = new ArrayList<>();

        // 年月属性を取得
        SqlRow yearMonthData = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
        // 勤怠を保存するボタン押下時に年月別ステータスが03：承認済の場合、 既に勤怠は凍結されているため、処理を実行しない。
        if (yearMonthData != null
                && Const.MONTHS_YEARS_STATUS_COMPLETE.equals(yearMonthData.getString("months_years_status"))) {

            HashMap<String, String> map = new HashMap<>();
            map.put("", "今月の勤怠情報は既に凍結されているため変更できません。管理部までお問い合わせください。");
            errorMsgList.add(map);
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg", errorMsgList
                    )
            ));
        // 年月別ステータスが承認済み以外の場合
        } else {

            // 当月の最大の日付分フォームがあるのでその分処理(31日まである月なら31個分)
            for (AttendanceInputForm inputForm : adl){
                String op = inputForm.openingTime;
                String cl = inputForm.closingTime;
                String emptyChar = "";

                // フォームの"始業"と"終業"が入力されているまたは 休暇等区分が選択されているフォームのみ処理する。
                if ((!emptyChar.equals(op) && !emptyChar.equals(cl))
                        || !Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)) {

                    // エラーメッセージがあるかチェック
                    ArrayList<HashMap> errorMsg = checkAttendanceInputForm(inputForm);
                    // エラーがある場合エラーメッセージのリストにメッセージを詰め込む
                    if (!errorMsg.isEmpty()) {
                        for (HashMap msg : errorMsg) {
                            errorMsgList.add(msg);
                        }
                    }
                }
            }
            if (!errorMsgList.isEmpty()) {
                return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ng",
                                "msg", errorMsgList
                        )
                ));
            }
        }
        //  debug
        System.out.println("exit saveCheck!!!!!");
        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

    /**
     * 「確定」ボタン押下処理
     * @param empNo 社員番号
     * @param year 年
     * @param month 月
     * @return 確定処理可否
     */
    public Result fix(String empNo, String monthsYears) {

        //  debug
        System.out.println("fix!!!!! :" + monthsYears);
        // 画面からForm取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();
        List<AttendanceInputForm> adl = attendanceFormList.attendanceInputFormList;
        for(AttendanceInputForm inputForm : adl) {
     // 実績一覧にすべて保存されている場合→承認申請を行う
        	try {
	        	// 年月属性を取得
	            SqlRow yearMonthData = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
		        	TblPerformance pft = new TblPerformance();
		            pft.employeeNo = empNo;
		            pft.monthsYears = monthsYears;
		            // 開始時間と終了時間は所属チームから規定の時間を取得
		            String teamCode = yearMonthData.getString("business_team_code");
		            SqlRow msGenList = MsGeneralCode.getCodeMaster("BUSINESS_TEAM_CODE",teamCode);
		            pft.openingTime = msGenList.getString("any_value3");
		            pft.closingTime = msGenList.getString("any_value4");
		            // 休日区分とシフト区分は「00:なし」を入れる（sqlにて00以外とする）
		            pft.holidayClass = Const.HOLIDAY_CLASS_NOTHING;
		            pft.shiftClass = Const.SHIFT_CLASS_NOTHING;

		            // 承認申請するデータ(承認済以外)を取得
		            List<SqlRow> appData= TblPerformance.getApproveNecessaryData(pft);
		            String perStatus = Const.PERFORMANCE_STATUS_NEED_APPROVAL;
		            String attrStatus = Const.MONTHS_YEARS_STATUS_FIX;
		            //TODO フォームデータのdateが承認申請されているデータと同じならフォーム保存する。違うならスルー
		            // 承認申請リスト
		            for(SqlRow list : appData) {
	            		String date = list.getString("performance_date");
	            		String status = list.getString("performance_status");
		            	if(date.equals(inputForm.date)) {
			            	System.out.println(date + "日　承認申請処理開始");
			            	attrStatus = Const.MONTHS_YEARS_STATUS_FIX;
			            	// 承認申請済のデータを修正した場合は、要承認状態に戻す
			            	if(Const.PERFORMANCE_STATUS_NEED_APPROVAL.equals(status)) {
			            		perStatus = Const.PERFORMANCE_STATUS_NEED_APPROVAL;
				            	// 業績テーブル更新
			            		TblPerformance.updateApprove(empNo, monthsYears, list.getString("performance_date"), perStatus);
			            	//差戻しデータの場合フォームからデータ取得、saveする
			            	} else if(Const.PERFORMANCE_STATUS_APPROVAL_NOT.equals(status)){
			                    String op = inputForm.openingTime;
			                    String cl = inputForm.closingTime;
			                    String emptyChar = "";

			                    // フォームの"始業"と"終業"が入力されている または 休暇等区分が選択されているフォームのみ処理する。
			                    if ((!emptyChar.equals(op) && !emptyChar.equals(cl))
			                            || !Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)) {
			                            String holidayClassCode = inputForm.holidayClassCode;
			                            if (holidayClassCode.isEmpty()) {
			                            }
			                            pft.employeeNo = inputForm.employeeNo;
			                            pft.monthsYears = inputForm.monthsYears;
			                            pft.performanceDate = DateUtil.getZeroPadding(inputForm.date);
			                            pft.openingTime = op;
			                            pft.closingTime = cl;
			                            pft.breakdown1 = inputForm.breakdown1;
			                            pft.breakdown2 = inputForm.breakdown2;
			                            pft.breakdown3 = inputForm.breakdown3;
			                            pft.breakdown4 = inputForm.breakdown4;
			                            pft.performanceTime = inputForm.performanceTime;
			                            pft.deductionNight = inputForm.deductionNight;
			                            pft.deductionOther = inputForm.deductionOther;
			                            pft.holidayClass = holidayClassCode;
			                            pft.shiftClass = inputForm.shiftClassCode;
			                            pft.other_approval_class = "";
			                            pft.remarks = inputForm.remarks;
			                            pft.performanceStatus = Const.PERFORMANCE_STATUS_NEED_APPROVAL;
			    	                    pft.updateUserId = inputForm.employeeNo;
			    	                    TblPerformance.updatePerformanceData(pft);
			                    }
			            	}
		            	}
		            }
		            if(appData.isEmpty()) {
		            	attrStatus = Const.MONTHS_YEARS_STATUS_COMPLETE;
		            }
	            	//年月属性テーブル更新
	            	TblYearMonthAttribute.updateYearMonthDataStatus(empNo,monthsYears,attrStatus);
        	} catch (Exception e) {
	            //  debug
	            System.out.println(e);
	            return ok(Json.toJson(
	                    ImmutableMap.of(
	                            "result", "ng",
	                            "msg", "確定処理時エラーが発生しました。もう一度お試しください。"
	                    )));
        	}
        }
        //  debug
        System.out.println("exit fix!!!!!");
        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

    /**
     * 「確定」押下時チェック処理
     * @param empNo 社員番号
     * @param year	年
     * @param month 月
     * @return エラーチェック可否
     */
    public Result fixCheck(String empNo, String year, String month) {
    	String monthsYears = year+DateUtil.getZeroPadding(month);

        //  debug
        System.out.println("fixCheck!!!!! :" + monthsYears);

        // エラーメッセージを詰め込むためのリスト
        ArrayList<HashMap> errorMsgList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();

        // 画面からForm取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();
        List<AttendanceInputForm> adl = attendanceFormList.attendanceInputFormList;
        for (AttendanceInputForm inputForm : adl){
            String op = inputForm.openingTime;
            String cl = inputForm.closingTime;
            String emptyChar = "";

            // フォームの"始業"と"終業"が入力されているまたは 休暇等区分が選択されているフォームのみ処理する。
            if ((!emptyChar.equals(op) && !emptyChar.equals(cl))
                    || !Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)) {
		        // エラーメッセージがあるかチェック
		        ArrayList<HashMap> errorMsg = checkAttendanceInputForm(inputForm);
		        // エラーがある場合エラーメッセージのリストにメッセージを詰め込む
		        if (!errorMsg.isEmpty()) {
		            for (HashMap msg : errorMsg) {
		                errorMsgList.add(msg);
		            }
		        }
            }
        }

        // 月の全ての営業日にデータが登録されているかをチェック
        // 日付リスト取得
        List<DateList> dateList = getDateList(year, DateUtil.getZeroPadding(month));
        ArrayList<String> notPDataList = new ArrayList<>();
        for(DateList dl : dateList){
        	if (!dl.isHoliday) {
        		// 平日の場合、実績一覧を見に行き、データが無い分をリスト化
		        List<SqlRow>  pData=  TblPerformance.getPerformanceDataByYearMonthAndDate(empNo, monthsYears, dl.date);
		        if (pData.isEmpty()) {
		        	notPDataList.add(dl.date);
		        }
        	}
        }
        // 実績一覧にすべて保存されている場合→承認申請を行う
        if(!notPDataList.isEmpty()) {
            for(String date :notPDataList) {
//            	String week = DateUtil.getWeek(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date));
            	map.put(date, "実績(もしくは休暇等区分)が記入(選択)されていない可能性があります。");
            }
        	errorMsgList.add(map);
        }
        // 勤怠を保存するボタン押下時に年月別ステータスが03：承認済の場合、 既に勤怠は凍結されているため、処理を実行しない。
     // 年月属性を取得
        SqlRow yearMonthData = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
        if (yearMonthData != null
                && Const.MONTHS_YEARS_STATUS_COMPLETE.equals(yearMonthData.getString("months_years_status"))) {

            map.put("", "今月の勤怠情報は既に凍結されているため変更できません。管理部までお問い合わせください。");
            errorMsgList.add(map);
        }

        if (!errorMsgList.isEmpty()) {
            //  debug
            System.out.println("exit error fix!!!!!");
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "datelist",notPDataList,
                            "msg", errorMsgList
                    )));
        }

        //  debug
        System.out.println("exit fixCheck!!!!!");
        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

    /**
     * 勤怠管理画面で「年月を指定して移動」時の処理をします。
     * @param empNo 社員番号
     * @param yearMonth 年月(yyyyMM)
     * @return 勤怠管理画面画面
     */
    // TODO 却下されたデータあったらしるしつける
    // TODO 管理メニューに戻れるように
    public Result moveTargetYearMonth(String empNo, String yearMonth) {
        String Year = yearMonth.substring(0,4);
        String Month = yearMonth.substring(4,6);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result", "ok",
                        "link",String.valueOf(routes.AttendanceCtl.index(Year,Month))
                )));
    }

    /**
     * 勤怠管理画面を表示用の日付リストを返します。
     * @param year 年(yyyy形式)
     * @param month 月(1~12)
     * @return DateList
     */
    public List<DateList> getDateList(String year, String month) {
        List<DateList> dl = new ArrayList<>();
        int day = 0;
        // 当月の最大日付まで処理
        for (String d: DateUtil.getDayOfTheMonth(year, month)) {
            DateList dateList = new DateList();
            dateList.stringDate = d;
            dateList.monthsYears = year + month;
            dateList.date = DateUtil.getZeroPadding(String.valueOf(++day));
            dateList.dateId = "date" + year + month + String.valueOf(day);
            dateList.isHoliday = DateUtil.isHoliday(year, month, DateUtil.getZeroPadding(String.valueOf(day)));
            dl.add(dateList);
        }
        return dl;
    }

    /**
     * 勤怠入力表の入力値エラーチェックをします
     * @param inputForm 勤怠入力フォーム
     * @return HashMapに詰めたエラーのメッセージ
     */
    public ArrayList<HashMap> checkAttendanceInputForm(AttendanceInputForm inputForm){
        // errorMsgListにはエラーが起きた日付のIDとエラーメッセージが入る
        // 例えば2017年4月20日のデータでエラーが起きた場合は
        // <date2017041,作業実績内訳と作業計が合っていません。>
        // 上記のようなデータができる
        ArrayList<HashMap> errorMsgList = new ArrayList<>();

        // 休暇区分が設定されていない場合か休日出勤が選ばれている場合
        if (Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)
                || Const.HOLIDAY_CLASS_HOLIDAY_WORK.equals(inputForm.holidayClassCode)) {
            // 作業実績内訳と作業計が合っていない場合エラー
            if (!CheckUtil.isBreakDownAndDeduction(
                    inputForm.breakdown1, inputForm.breakdown2, inputForm.breakdown3, inputForm.breakdown4,
                    inputForm.deductionNight, inputForm.deductionOther, inputForm.performanceTime)) {
                HashMap<String, String> map = new HashMap<>();
                map.put(inputForm.dateId, inputForm.stringDate + "作業実績内訳の数値が合計の数値と合っていません。");
                errorMsgList.add(map);
            }
        }

        String Year = inputForm.monthsYears.substring(0, 4);
        String Month = inputForm.monthsYears.substring(4, 6);
        String openingTime = null;
        String closingTime = null;
        if (!inputForm.openingTime.equals("")) {
            openingTime = inputForm.openingTime;
        }
        if (!inputForm.closingTime.equals("")) {
            closingTime = inputForm.closingTime;
        }
        String isHolidayClass = CheckUtil.isHolidayClass(
                Year, Month, inputForm.date, openingTime, closingTime, inputForm.holidayClassCode);
        // isHolidayClassメソッドを呼び出してエラーメッセージがあった場合はエラー
        if (isHolidayClass != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put(inputForm.dateId, inputForm.stringDate + isHolidayClass);
            errorMsgList.add(map);
        }

        return errorMsgList;
    }

    /**
     * フォームで「始業」「終業」をの入力したときのエラーチェックをします
     * @param start 開始(hh:mm)
     * @param end 終了(hh:mm)
     * @return エラーのメッセージ
     */
    public String checkStartAndEnd(String start, String end){
        if (!CheckUtil.checkStrStartEndTime(start, end)) {
            return "時間はHH:mm形式で入力してください。（例：午前0時と入力したい場合 ⇒【24:00】）";
        } else if (!CheckUtil.isStartEndTime(start, end)) {
            return "始業時間は終業時間よりも早い時間を入れてください。";
        } else {
        	//チーム名でanyvalue3(start)とanyvalue4(end)を見に行き、違ってたらエラーを返す
            return null;
        }
    }

    /**
     * 勤怠合計を取得します。
     * @param start 開始(hh:mm)
     * @param end 終了(hh:mm)
     * @param holidayClassCode 休暇区分コード
     * @param deducation 控除時間
     * @return 結果
     */
    public Result getPerformanceTime(String start, String end, String holidayClassCode, String deduction) {
        Double time = 0.0;
        if (!(start.equals("none") || end.equals("none"))) {
            String errorMsg = checkStartAndEnd(start, end);
            if (errorMsg != null) {
                return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ng",
                                "msg", errorMsg
                        )));
            }
            time = DateUtil.getPerformanceTime(start,end);
        }
        Double salariedTime = 0.0;
        // 休暇区分が設定されている場合休暇区分の時間を取得
        if (!holidayClassCode.equals(Const.HOLIDAY_CLASS_NOTHING)) {
            salariedTime = MsGeneralCode.getAnyValue1ByCode(holidayClassCode,"HOLIDAY_CLASS");
        }
        double doubleDeduction = Double.parseDouble(deduction);
        String performanceTime = String.valueOf(time + salariedTime - doubleDeduction);
        String workTime = String.valueOf(time);
        // 休暇時間が有給割当時間以上の場合、合計時間と勤務時間を0にする
        Double vacTime = MsGeneralCode.getAnyValue1ByCode("01", "PAID_VACATION_ASSIGN_TIME");
        if(salariedTime >= vacTime) {
        	performanceTime = "0.0";
        	workTime = "0.0";
        }
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "performanceTime", performanceTime,
                        "workTime", workTime
                )));
    }

    /**
     * 深夜作業（時間）を取得します。
     * @return 結果
     */
    public Result getNightWork(String endTime){
    	double nightwork = 0.0;
    	nightwork = DateUtil.getLateNightWorkTime(endTime);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", nightwork
                )));
    }

    /**
     * 有給割当を取得します。
     * @return 結果
     */
    public Result getSalaried(String holidayClassCode){
        double salaried = 0.0;
        if (!Const.HOLIDAY_CLASS_NOTHING.equals(holidayClassCode)) {
            salaried = MsGeneralCode.getAnyValue1ByCode(holidayClassCode,"HOLIDAY_CLASS");
        }
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", salaried
                )));
    }

    /**
     * 選択された部に応じた課リストを取得します。
     * @return 結果
     */
    public Result getDivisionList(String departCode){

    	if (departCode == "" || departCode.isEmpty()) {
    		departCode = "00";
    	}
    	List<MsGeneralCode> divisionList = MakeModelUtil.makeCodeTypeList("DIVISION_CODE",departCode);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", divisionList
                )));
    }

    /**
     *選択された業務名に応じた業務チームリストを取得します。
     * @return 結果
     */
    public Result getBusinessTeamList(String businessCode){

    	List<MsGeneralCode> businessTeamList = MakeModelUtil.makeCodeTypeList("BUSINESS_TEAM_CODE",businessCode);
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", businessTeamList
                )));
    }

    /**
     * 却下された差戻しデータがあれば取得する
     * @return dataList
     */
    public Result isApproveRemand() {
		// 社員番号
        String empNo = session("employeeNo");
        List<SqlRow> data = TblPerformance.getPerformanceDataByStatus(empNo,Const.PERFORMANCE_STATUS_APPROVAL_NOT);
        //データリストを詰め込むためのリスト
        ArrayList<HashMap> dataList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        if(!data.isEmpty()){
        	for(SqlRow d:data) {
        		// yyyy/MMに変換
        		String monthsYears = (d.getString("months_years").substring(0, 4))
        				.concat("/").concat(d.getString("months_years").substring(4, 6));
        		map.put(monthsYears,d.getString("performance_date"));
        	}
        	dataList.add(map);
			return ok(Json.toJson(
	                ImmutableMap.of(
	                        "result","ok",
	                        "value", dataList
	                )));
        } else {
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng")));
        }
	}

}
