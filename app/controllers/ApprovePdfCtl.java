//package controllers;
//
//import com.avaje.ebean.SqlRow;
//import com.google.inject.Inject;
//import it.innove.play.pdf.PdfGenerator;
//import models.MsGeneralCode;
//import models.form.AttendanceInputFormList;
//import models.form.AttendanceSumForm;
//import models.form.DateList;
//import models.form.StatusAndWorkForm;
//import org.omg.CosNaming.NamingContextPackage.NotFound;
//import play.data.Form;
//import play.data.FormFactory;
//import play.mvc.Result;
//import services.utils.DateUtil;
//import services.utils.MakeModelUtil;
//import views.html.attendance_pdf;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static models.TblPerformance.getPerformanceData;
//import static play.mvc.Controller.session;
//import static play.mvc.Results.notFound;
//import static play.mvc.Results.ok;
//
///**
// * Created by suzuki-daisuke on 2017/04/21.
// */
//public class ApprovePdfCtl {
//    @Inject
//    FormFactory formFactory;
//    @Inject
//    public PdfGenerator pdfGenerator;
//    public Result index(String year, String month, int pdfKind){
//        final int PDF_KIND_NORMAL = 1;
//        final int MIN_MONTH = 1;//最大の月(1月)
//        final int MAX_MONTH = 12;//最大の月(月)
//        final String employeeNo = session("employeeNo");//社員番号はセッションから取ってくる
//        List<DateList> dateList = getDateList(year, month);//日付
//
//        //月が一桁の場合"0"を前に付ける　⇒　"1" →　"01"
//        if(month.length() == 1){
//            month = "0" + month;
//        }
//        String monthsYears = year + month;
//        Boolean existsDefaultValue = false;
//        List<SqlRow> performanceData = getPerformanceData(employeeNo, monthsYears);
//        AttendanceInputFormList aifl = new AttendanceInputFormList();
//
//        StatusAndWorkForm sawf = MakeModelUtil.makeStatusAndWorkForm(employeeNo, monthsYears);
//
//        //ステータスと作業実績内訳詳細を保存
//        Form<StatusAndWorkForm> statusAndWorkForm = formFactory.form(StatusAndWorkForm.class).fill(sawf);
//
//        //指定した年月の実績データが一件でもある場合は初期値をセット
//        if(performanceData.size() != 0){
//            existsDefaultValue = true;
//            aifl.attendanceInputFormList = MakeModelUtil.makeAttendanceInputFormList(dateList, performanceData);
//        }
//        Form<AttendanceInputFormList> inputForm = formFactory.form(AttendanceInputFormList.class).fill(aifl);
//
//        AttendanceSumForm asf = MakeModelUtil.makeAttendanceSumForm(aifl.attendanceInputFormList);
//
//        //休暇区分のリスト
//        List<MsGeneralCode> hcmList = MakeModelUtil.makeHolidayClassMst();
//
//        ArrayList<String> fonts = new ArrayList<>();
//        fonts.add("fonts/meiryo.ttc");
//        if(pdfKind == PDF_KIND_NORMAL) {
//            return pdfGenerator.ok(attendance_pdf.render(
//                    statusAndWorkForm,
//                    inputForm,
//                    dateList,
//                    year,
//                    month,
//                    existsDefaultValue,
//                    employeeNo,
//                    hcmList,
//                    asf
//            ), "", fonts);
//        }else{
//            return notFound();
//        }
//    }
//
//}
