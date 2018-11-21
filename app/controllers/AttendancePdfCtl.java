package controllers;

import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.core.io.InputDecorator;
import com.google.inject.Inject;

import common.Const;
import it.innove.play.pdf.PdfGenerator;
import models.MsGeneralCode;
import models.form.AttendanceInputForm;
import models.form.AttendanceInputFormList;
import models.form.AttendanceSumForm;
import models.form.DateList;
import models.form.StatusAndWorkForm;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import services.utils.DateUtil;
import services.utils.MakeModelUtil;
import views.html.attendance_pdf;

import java.util.ArrayList;
import java.util.List;

import static models.TblPerformance.getPerformanceData;
import static play.mvc.Controller.session;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

/**
 * Created by suzuki-daisuke on 2017/04/21.
 */
public class AttendancePdfCtl {
    @Inject
    FormFactory formFactory;
    @Inject
    public PdfGenerator pdfGenerator;


    /**
     * 勤怠表PDFを表示します。
     * @param refEmpNo 該当社員番号
     * @param year 年
     * @param month 月
     * @param pdfKind pdf種類
     * @return
     */
    public Result index(String refEmpNo, String year, String month, int pdfKind){
        final int PDF_KIND_NORMAL = 1;
        final int MIN_MONTH = 1;//最大の月(1月)
        final int MAX_MONTH = 12;//最大の月(月)
        final String employeeNo = refEmpNo;
        final String monthsYears = year + DateUtil.getZeroPadding(month);
        List<DateList> dateList = DateUtil.getDateList(year, DateUtil.getZeroPadding(month));
        List<SqlRow> performanceData = getPerformanceData(employeeNo, monthsYears);

        AttendanceInputFormList aifl = new AttendanceInputFormList();

        //ステータスと作業実績内訳詳細をフォーム化
        StatusAndWorkForm sawf = MakeModelUtil.makeStatusAndWorkForm(employeeNo, monthsYears);
        Form<StatusAndWorkForm> statusAndWorkForm = formFactory.form(StatusAndWorkForm.class).fill(sawf);

        //登録データをフォーム化
        aifl.attendanceInputFormList = MakeModelUtil.makeAttendanceInputFormList(dateList, performanceData);
        Form<AttendanceInputFormList> inputForm = formFactory.form(AttendanceInputFormList.class).fill(aifl);

        //合計表示をフォーム化
        AttendanceSumForm asf = MakeModelUtil.makeAttendanceSumForm(aifl.attendanceInputFormList);

        //休暇区分のリスト
        List<MsGeneralCode> hcmList = MakeModelUtil.makeCodeTypeList(Const.HOLIDAY_CODE_NAME);

        ArrayList<String> fonts = new ArrayList<>();
        fonts.add("fonts/meiryo.ttc");
     // 1~12(1月~12月以外がパラメータに入ってきたらエラー)
        if (MIN_MONTH <= Integer.parseInt(month) && MAX_MONTH >= Integer.parseInt(month)) {
        if(pdfKind == PDF_KIND_NORMAL) {
            return pdfGenerator.ok(attendance_pdf.render(
            		statusAndWorkForm,
                    inputForm,
                    dateList,
                    year,
                    month,
                    employeeNo,
                    hcmList,
                    asf
            ), "", fonts);
        }else{
            return notFound();
        }
        } else {
            return notFound();
        }
    }
}
