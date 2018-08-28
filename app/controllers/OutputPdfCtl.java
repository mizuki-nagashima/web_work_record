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

import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import it.innove.play.pdf.PdfGenerator;
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
import views.html.overtimeList_pdf;


import java.util.ArrayList;
import java.util.List;

import static play.mvc.Controller.session;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

/**
 * Created by otsuka on 2018/04/03.
 */

public class OutputPdfCtl {
    /**
     * 残業時間一覧のPDF出力画面を表示します。
     * @return 残業時間一覧のPDF出力画面
     */
    @Inject
    FormFactory formFactory;
    @Inject
    public PdfGenerator pdfGenerator;
    public Result outputPdf() {
    		
    		String employeeNumber = "00229";
    		String employeeName = "大塚果穂";
    		String department = "SSC";
    		String position = "-";
    		String businessCd = "社内運用保守";
    		String businessName = "社内運用保守";
    		String teamLeader = "高橋康太";
    		String month4 = "10";
    		String month5 = "12";
    		String month6 = "20";
    		String month7 = "30";
    		String month8 = "50";
    		String month9 = "13";
    		String month10 = "32";
    		String month11 = "15";
    		String month12 = "18";
    		String month1 = "22";
    		String month2 = "20";
    		String month3 = "20";
    		String over45HourCount = "1";
    		String overtimeAverage = "20";
    		
//    		List<OutputPdfForm> outputPdfList = new ArrayList<>();
//    		OutputPdfForm outputPdfForm = new OutputPdfForm();
//    		outputPdfForm.employeeNumber = Integer.parseInt("00229");
//    		outputPdfForm.employeeName = "大塚果穂";
//    		outputPdfForm.department = "SSC";
//    		outputPdfForm.position = "-";
//    		outputPdfForm.businessCd = "社内運用保守";
//    		outputPdfForm.businessName = "社内運用保守";
//    		outputPdfForm.teamLeader = "高橋康太";
//    		outputPdfForm.over45HourCount = Integer.parseInt("1");
//    		outputPdfForm.overtimeAverage = Integer.parseInt("23.6");
//    		outputPdfList.add(outputPdfForm);

    		ArrayList<String> fonts = new ArrayList<>();
            fonts.add("fonts/meiryo.ttc");
            
//    		return pdfGenerator.ok(overtimeList_pdf.render(
//    				outputPdfList),"", fonts);
    		
//        	Form<OutputPdfForm> outputPdfForm = formFactory.form(OutputPdfForm.class);
    		return pdfGenerator.ok(overtimeList_pdf.render(
    				employeeNumber,
    				employeeName,
    				department,
    				position,
    				businessCd,
    				businessName,
    				teamLeader,
    				month4,
    				month5,
    				month6,
    				month7,
    				month8,
    				month9,
    				month10,
    				month11,
    				month12,
    				month1,
    				month2,
    				month3,
    				over45HourCount,
    				overtimeAverage),"", fonts);
    	}
}
