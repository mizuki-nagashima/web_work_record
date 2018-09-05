package controllers;

import static models.TblPerformance.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avaje.ebean.SqlRow;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import common.Const;
import models.MsGeneralCode;
import models.TblPerformance;
import models.TblYearMonthAttribute;
import models.form.AttendanceInputForm;
import models.form.AttendanceInputFormList;
import models.form.DateList;
import models.form.StatusAndWorkForm;
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
        // 日付
        List<DateList> dateList = getDateList(year, DateUtil.getZeroPadding(month));

        String monthsYears = year + DateUtil.getZeroPadding(month);
        Boolean existsDefaultValue = false;
        // 保存済実績を取得
        List<SqlRow> performanceData = getPerformanceData(employeeNo, monthsYears);

        // 表示用Form
        AttendanceInputFormList aifl = new AttendanceInputFormList();
        StatusAndWorkForm sawf = MakeModelUtil.makeStatusAndWorkForm(employeeNo, monthsYears);

        // 指定した年月の実績データが一件でもある場合は初期値をセット
        if (performanceData.size() != 0) {
            existsDefaultValue = true;
            aifl.attendanceInputFormList = MakeModelUtil.makeAttendanceInputFormList(dateList, performanceData);
        }

        Form<StatusAndWorkForm> statusAndWorkForm = formFactory.form(StatusAndWorkForm.class).fill(sawf);
        Form<AttendanceInputFormList> inputForm = formFactory.form(AttendanceInputFormList.class).fill(aifl);

        // TODO 部署リスト

        // TODO 課リスト

        // TODO 業務名リスト

        // TODO 業務チーム名リスト

        // 休暇区分のリスト
        List<MsGeneralCode> hcmList = MakeModelUtil.makeHolidayClassMst();
        // シフト区分のリスト
        List<MsGeneralCode> shiftList = MakeModelUtil.makeShiftClassMst();

        // 1~12(1月~12月以外がパラメータに入ってきたらエラー)
        if (MIN_MONTH <= Integer.parseInt(month) && MAX_MONTH >= Integer.parseInt(month)) {
            return ok(attendance.render(
                    statusAndWorkForm,
                    inputForm,
                    dateList,
                    year,
                    month,
                    existsDefaultValue,
                    employeeNo,
                    hcmList,
                    shiftList
            ));
        } else {
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
                ymat.registUserId = statusAndWorkForm.employeeNo;
                ymat.updateUserId = statusAndWorkForm.employeeNo;
                TblYearMonthAttribute.insertYearMonthData(ymat);
            } else {
                ymat.updateUserId = statusAndWorkForm.employeeNo;
                TblYearMonthAttribute.updateYearMonthData(ymat);
            }
        } catch (Exception e) {
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

        // FIXME debug
        System.out.println("save!!!!!");

        // 画面からForm取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();
        List<AttendanceInputForm> adl = attendanceFormList.attendanceInputFormList;
        //　登録済の実績を取得
        ArrayList<String> performanceDataList = TblPerformance.getPerformanceDataList(empNo, monthsYears);

        //　エラーメッセージを詰め込むためのリスト
        ArrayList<HashMap> errorMsgList = new ArrayList<>();

        // TODO 年月属性を取得
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
        // 勤怠を保存するボタン押下時に年月別ステータスが02：承認依頼済の場合、一度確定ボタン押下後であるため、確定ボタン押下時と同等の処理を行う
        } else if (yearMonthData != null
                && Const.MONTHS_YEARS_STATUS_FIX.equals(yearMonthData.getString("months_years_status"))) {
            return ok(Json.toJson(ImmutableMap.of("result", "ok")));

        // 年月別ステータスが01：未確定の場合、年月別ステータスがまだ保存されていない場合は自由に変更して問題なし
        } else {
        // TODO 実績と月別属性のFormが異なるので、難しいため、ちょっと保留
//        // 年月属性テーブルが既に存在する場合は更新、なければ新規作成
//        SqlRow targetYearMonthAttributeTbl = TblYearMonthAttribute.getYearMonthData(empNo, monthsYears);
//        if (targetYearMonthAttributeTbl == null) {
//            // 新規作成
//        	statusAndWorkForm
//            TblYearMonthAttribute ymat = MakeModelUtil.makeYearMonthAttributeTbl(empNo, monthsYears, statusAndWorkForm);
//            ymat.registUserId = empNo;
//            ymat.updateUserId = empNo;
//            TblYearMonthAttribute.insertYearMonthData(ymat);
//        }

            //　当月の最大の日付分フォームがあるのでその分処理(31日まである月なら31個分)
            for (AttendanceInputForm inputForm : adl){
                String op = inputForm.openingTime;
                String cl = inputForm.closingTime;
                String emptyChar = "";

                //　フォームの"始業"と"終業"が入力されている または　休暇等区分が選択されているフォームのみ処理する。
                if ((!emptyChar.equals(op) && !emptyChar.equals(cl))
                        || !Const.HOLIDAY_CLASS_NOTHING.equals(inputForm.holidayClassCode)) {

                    //　エラーメッセージがあるかチェック
                    ArrayList<HashMap> errorMsg = checkAttendanceInputForm(inputForm);
                    //　エラーがある場合エラーメッセージのリストにメッセージを詰め込む、エラーがない場合は登録処理
                    if (!errorMsg.isEmpty()) {
                        for (HashMap msg : errorMsg) {
                            errorMsgList.add(msg);
                        }
                    } else {
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
                        // TODO その他承認区分
                        pft.other_approval_class = "";
                        pft.remarks = inputForm.remarks;
                        pft.performanceStatus = Const.PERFORMANCE_STATUS_SAVE;
                        try {
                            String date = DateUtil.getZeroPadding(inputForm.date);
                            //　登録しようとしている日のデータがある場合は更新、ない場合登録
                            if (performanceDataList.contains(date)) {
                                // FIXME
                                pft.updateUserId = inputForm.employeeNo;
                                TblPerformance.updatePerformanceData(pft);
                            } else {
                                // FIXME
                                pft.registUserId = inputForm.employeeNo;
                                pft.updateUserId = inputForm.employeeNo;
                                TblPerformance.insertPerformanceData(pft);
                            }
                        } catch (Exception e) {
                            // FIXME debug
                            System.out.println(e);

                            HashMap<String, String> map = new HashMap<>();
                            map.put(inputForm.dateId, "保存時エラーが発生しました。もう一度お試しください。");
                            errorMsgList.add(map);
                        }
                    }
                } else if (!TblPerformance.getPerformanceDataByYearMonthAndDate(
                        inputForm.employeeNo,inputForm.monthsYears,inputForm.date).isEmpty()){
                    TblPerformance.deletePerformanceData(inputForm.employeeNo,inputForm.monthsYears,inputForm.date);
                }
            }
            if (!errorMsgList.isEmpty()) {
                // FIXME debug
                System.out.println("exit save!!!!!");

                return ok(Json.toJson(
                        ImmutableMap.of(
                                "result", "ng",
                                "msg", errorMsgList
                        )
                ));
            } else {
                // FIXME debug
                System.out.println("exit save!!!!!");
                return ok(Json.toJson(ImmutableMap.of("result", "ok")));
            }
        }
    }

    /**
     * 「確定」ボタン押下処理
     * @param empNo 社員番号
     * @param yearMonth 年月(yyyyMM)
     * @return 勤怠管理画面画面
     */
    public Result fix(String empNo, String yearMonth) {

        // FIXME debug
        System.out.println("fix!!!!!");

        //　画面の入力値を取得
        AttendanceInputFormList attendanceFormList =
                formFactory.form(AttendanceInputFormList.class).bindFromRequest().get();

        //　エラーがあるかチェック
        ArrayList<HashMap> errorMsgList = new ArrayList<>();
        for (AttendanceInputForm form : attendanceFormList.attendanceInputFormList) {
            ArrayList<HashMap> errorMsg = checkAttendanceInputForm(form);
            if (!errorMsg.isEmpty()) {
                for (HashMap msg : errorMsg) {
                    errorMsgList.add(msg);
                }
            }
        }
        // エラーメッセージが1件以上存在する場合はエラーメッセージを返却して処理終了
        if (!errorMsgList.isEmpty()) {
            return ok(Json.toJson(
                    ImmutableMap.of(
                            "result", "ng",
                            "msg", errorMsgList
                    )
            ));
        }

        // TODO 月の全ての営業日にデータが登録されているかをチェック
        // TODO 承認依頼をするよ確認モーダル

        // 実績を取得して、実績別ステータスの振り分け設定
        for (AttendanceInputForm form: attendanceFormList.attendanceInputFormList) {
            // TODO 承認済のデータを修正した場合は、要承認状態に戻す
            // TODO 承認済のデータ以外を修正した場合は、承認済みのデータは要承認状態に戻さない


            // 要承認の実績
            if (isNeedApprovalPerformance(
                    form.holidayClassName, form.shiftClassCode, form.deductionNight, form.deductionOther)) {
                form.performanceStatus = Const.PERFORMANCE_STATUS_NEED_APPROVAL;
                System.out.println(form.date + "日の実績は要承認です。");
            // 承認不要の実績
            } else {
                form.performanceStatus = Const.PERFORMANCE_STATUS_NON_NEED_APPROVAL;
            }
        }
        // TODO 業績テーブル更新

        // TODO 年月属性テーブル更新

        // TODO 正常時、異常時の分岐など

        return ok(Json.toJson(ImmutableMap.of("result", "ok")));
    }

    /**
     * 実績別ステータスが03：承認依頼済となる実績かどうかを判定する。
     *
     * @param holidayClassCode 休暇区分
     * @param shiftClassCode シフト区分
     * @param deductionNight 控除（深夜）
     * @param deductionOther 控除（その他）
     *
     * @return Boolean
     */
    public Boolean isNeedApprovalPerformance(
            String holidayClassCode, String shiftClassCode,
            Double deductionNight, Double deductionOther) {

        Boolean isNeedApprovalPerformance = false;
        // 休暇区分00:なし以外
        // シフト区分00：なし以外
        // 控除時間が既定外
        if (!holidayClassCode.equals(Const.HOLIDAY_CLASS_NOTHING)
                || !shiftClassCode.equals(Const.SHIFT_CLASS_NOTHING)) {
            return true;
        }
        return false;
    }


    /**
     * 勤怠管理画面で「年月を指定して移動」時の処理をします。
     * @param empNo 社員番号
     * @param yearMonth 年月(yyyyMM)
     * @return 勤怠管理画面画面
     */
    public Result moveTargetYearMonth(String empNo, String yearMonth, String nowYearMonth) {
        SqlRow targetYearMonthAttributeTbl = TblYearMonthAttribute.getYearMonthData(empNo, yearMonth);
        // すでに指定した月年月属性テーブルがある場合はそれを表示
        // 存在しない場合、社員マスタの情報を取得して表示
//        if (targetYearMonthAttributeTbl == null) {
//            StatusAndWorkForm statusAndWorkForm = MakeModelUtil.makeStatusAndWorkForm(empNo, nowYearMonth);
//            TYearMonthAttribute ymat = MakeModelUtil.makeYearMonthAttributeTbl(empNo, yearMonth, statusAndWorkForm);
//            try {
//              TYearMonthAttribute.insertYearMonthData(ymat);
//            } catch (Exception e) {
//
//            }
//        }
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
     * 勤怠管理画面の入力値エラーチェックをします
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
            return null;
        }
    }

    /**
     * 勤怠合計を取得します。
     * @param start 開始(hh:mm)
     * @param end 終了(hh:mm)
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
        // 休暇区分が設定されている場合休暇区分の時間を足す
        if (holidayClassCode.isEmpty()) {
            salariedTime = MsGeneralCode.getSalariedTime(holidayClassCode);
        }
        double doubleDeduction = Double.parseDouble(deduction);
        String performanceTime = String.valueOf(time + salariedTime - doubleDeduction);
        String workTime = String.valueOf(time);
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
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", DateUtil.getLateNightWorkTime(endTime)
                )));
    }

    /**
     * 有給割当を取得します。
     * @return 結果
     */
    public Result getSalaried(String holidayClassCode){
        double salaried = 0.0;
        if (!Const.HOLIDAY_CLASS_NOTHING.equals(holidayClassCode)) {
            salaried = MsGeneralCode.getSalariedTime(holidayClassCode);
        }
        return ok(Json.toJson(
                ImmutableMap.of(
                        "result","ok",
                        "value", salaried
                )));
    }
}
