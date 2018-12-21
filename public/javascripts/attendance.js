  $(function() {

    if(localStorage.saveMsg){
      $(".success-msg").removeClass("hidden");
      $('.success-msg').text(" " + localStorage.saveMsg).fadeIn(1000).delay(2000).fadeOut(2000);
      localStorage.removeItem('saveMsg');
    }else if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
      $(document).ready(function () {
        setTimeout(function() { window.scrollTo(0,$('#attendanceSaveTop').offset().top); }, 1);
      });
    }

    $('.openingTime').timepicker({
      'scrollDefault': '10:00',
      'timeFormat':'H:i',
      'wrapHours': false,
      'showOnFocus': false
    });

    $('.closingTime').timepicker({
      'scrollDefault': 'now',
      'timeFormat':'H:i',
      'wrapHours': false,
      'showOnFocus': false
    });

    var nowYearMonth = $('#monthsYears').val();
    var year = nowYearMonth.substring(0,nowYearMonth.length - 2 );
    var month = nowYearMonth.substring(nowYearMonth.length - 2, nowYearMonth.length);
    var empNum = $('#refEmpNo').val();

    $('.datepicker').datepicker({
      format: 'yyyy/mm',
      language: 'ja',
      autoclose: true,
      minViewMode: 'months'
    }).on({
      changeDate : function(){
        var yearMonth = $(this).val();
        var url =jsRoutes.controllers.AttendanceCtl.moveTargetYearMonth(empNum,yearMonth.replace("/",""),nowYearMonth);
        $.ajax({
          type : 'GET',
          url : url.url,
          success : function(data) {
            if (data.result == "ok") {
              window.location.href = data.link;
            }
          }
        });
      }
    });
    var pageSaveBtn = $('#page-save');
    pageSaveBtn.hide();
    var saveBtnTop = $('#attendanceSaveTop');
    var saveBtnTopPosition = saveBtnTop.offset().top;
    $(window).scroll(function () {
        //$.headerScroll();
        if (($(this).scrollTop() > saveBtnTopPosition)) {
            pageSaveBtn.fadeIn();
        } else {
            pageSaveBtn.fadeOut();
        }
    });

    $(".changeEventForm").change(function(){
      $(".fixBtn").prop('disabled', false);
      var targetTr = $(this).closest('tr');
      var targetTrId = targetTr.attr('id');
      if(targetTrId == "behindTr"){
        targetTrId = targetTr.attr('date-id');
      }
      var start = $('#' + targetTrId).children().children('.openingTime').val();
      var end = $('#' + targetTrId).children().children('.closingTime').val();
      var holidayClass = $('#holidayClass' + targetTrId).val();
      var modalHolidayClass = $('#holidayClass-modal-' + targetTrId).val();
      if(modalHolidayClass != 00){
        holidayClass = modalHolidayClass;
      }
      //startとendがnullではない or 休暇区分が選択されている
      if(((start != "")&&(end  != "")) || holidayClass != 00){
        $('#' + targetTrId + '-closingTime').val(end);
        var deductionNight = $('#' + targetTrId).children().children('.deductionNight').val();
        var deductionOther = $('#' + targetTrId).children().children('.deductionOther').val();
        var deduction = parseFloat(deductionNight) + parseFloat(deductionOther);
        deduction = deduction.toString();
        if((start == "") || (end == "")){
          start = "none"
          end = "none"
        }
        var url = jsRoutes.controllers.AttendanceCtl.getPerformanceTime(start,end,holidayClass,deduction);
        $.ajax({
          type : 'GET',
          url : url.url,
          success : function(data) {
            var warnMsgId = 'warnMsgId' + targetTrId;
            $('#' + warnMsgId).remove();
            if(data.result == "ok"){
              var target = $('#' + targetTrId).children();
              var breakdown1 = parseFloat(target.children('#breakdown1').val());
              var breakdown2 = parseFloat(target.children('#breakdown2').val());
              var breakdown3 = parseFloat(target.children('#breakdown3').val());
              var breakdown4 = parseFloat(target.children('#breakdown4').val());
              if((breakdown1 + breakdown2 + breakdown3 + breakdown4 == 0) ||
                 (breakdown2 + breakdown3 + breakdown4 == 0)){
                target.children('#breakdown1').val(data.workTime);
              }
              // worktimeが0だったらstartとendも初期化
              if(parseFloat(data.workTime) == 0){
              	$('#' + targetTrId).children().children('.openingTime').val("");
        		$('#' + targetTrId).children().children('.closingTime').val("");
              }
              target.children('#performanceTime').val(data.performanceTime);
              $('#performanceTime-modal-' + targetTrId).val(data.performanceTime);
              target.children('#form-warning').removeClass("form-warning");
              target.children('#form-warning').addClass("none-form-warning");
              if(!$("p").hasClass('warn-msg-contents')){
                $(".saveBtn").prop('disabled', false);
                $(".saveBtn-a").removeClass("hidden");
                if($("p").hasClass('save-warn-msg-contents')){
                   //$("#warn-msg").css("left","155px");
                }else{
                  $(".warn-msg").addClass("hidden");
                }
              }
              $.totalInsert();
            }else{
               $(".saveBtn").prop('disabled', true);
               $(".saveBtn-a").addClass("hidden");
               $(".warn-msg").removeClass("hidden");
               $('#' + targetTrId).children().children('#form-warning').removeClass("none-form-warning");
               $('#' + targetTrId).children().children('#form-warning').addClass("form-warning");
               var stringDate = targetTr.attr('string-date');
               var msgContents = '<p id=' + warnMsgId + ' class=' + 'warn-msg-contents' + '>' + stringDate + '&nbsp;-&nbsp;' + data.msg + '<p>';
               $('#warn-msg-title').after(msgContents);
               $('#' + targetTrId).children().children('#performanceTime').val("0.0");
               $.totalInsert();
               if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
                $(".warn-msg").css('top','initial');
                $(".warn-msg").css('bottom','20px');
               }
            }
          }
        });
      }else{
        $('#' + targetTrId).children().children('.openingTime').val("");
        $('#' + targetTrId).children().children('.closingTime').val("");
        $('#' + targetTrId).children().children('#performanceTime').val("0.0");
      }
    });

    $(".changeDepartmentCd").change(function(){
      var departmentCode = $('#departmentCode').val();
      if(departmentCode != 00 || departmentCode != ""){
        var url = jsRoutes.controllers.AttendanceCtl.getDivisionList(departmentCode);
        $.ajax({
          type : 'GET',
          url : url.url,
          success : function(data) {
            if(data.result == "ok"){
            	var divisionList = data.value;
	            $('#divisionCode').html("");
	            $.each(divisionList, function (i, val) {
	               $('#divisionCode').append($("<option>").val(val.code).text(val.codeName));
                });
            }
          }
        });
      }else{
        $('#divisionCode').html("");
		$('#divisionCode').append($("<option>").val("00").text("該当なし"));
	  }
    });

    $(".changeBusinessCd").change(function(){
      var businessCode = $('#businessCode').val();
      if(businessCode != 00 || businessCode != ""){
        var url =jsRoutes.controllers.AttendanceCtl.getBusinessTeamList(businessCode);
        $.ajax({
          type : 'GET',
          url : url.url,
          success : function(data) {
            if(data.result == "ok"){
            	var businessTeamList = data.value;
	            $('#businessTeamCode').html("");
	            $.each(businessTeamList, function (i, val) {
	               $('#businessTeamCode').append($("<option>").val(val.code).text(val.codeName));
                });
            }
          }
        });
      }else{
        $('#businessTeamCode').html("");
		$('#businessTeamCode').append($("<option>").val("00").text("該当なし"));
	  }
    });

    $("#inputForm input").change(function(){
      if(!$("p").hasClass('warn-msg-contents')){
        $(".saveBtn").prop('disabled', false);
        $(".saveBtn-a").removeClass("hidden");
        if(!$("p").hasClass('save-warn-msg-contents')){
          $("#page-save").css("bottom","20px");
          $(".warn-msg").addClass("hidden");
        }
      }
      $.totalInsert();
    });

    $("#inputForm select").change(function(){
      if(!$("p").hasClass('warn-msg-contents')){
        $(".saveBtn").prop('disabled', false);
        $(".saveBtn-a").removeClass("hidden");
        if(!$("p").hasClass('save-warn-msg-contents')){
          //$("#page-save").css("top","20px");
          $(".warn-msg").addClass("hidden");
        }
      }
      $.totalInsert();
    });

    $(".warn-msg").click(function () {
      $(this).addClass("hidden");
    });

    $(".saveBtn").click(function () {
       $(".warn-msg").addClass("hidden");
       $('.warn-msg-contents').remove();
       $('.save-warn-msg-contents').remove();
       $('#form-warning').addClass("none-form-warning");
       $('#form-warning').removeClass("form-warning");
       $('#inputForm').attr("action","/attendance/saveCheck/"+empNum+"/"+nowYearMonth);
       $.totalInsert();
       $('.work-time-contents').remove();
       $.postAjax('#inputForm', function(data){
	        if(data.result == "ok"){
		       var sumPerformanceTime = $("#sumPerformanceTime").val();
		       var msgContents = '<p id="sumMonthlyWorkTime" class="work-time-contents">・今月作業実績： ' + sumPerformanceTime +' 時間（有休等を除く）</p>';
		       $('#defaultWorkTime').after(msgContents);
		　　　　　　$('#saveModal').modal('show');
	        }else if(data.result == "ng"){
	          $(".warn-msg").removeClass("hidden");
	          var errorMsgList = data.msg;
	          var alertMsgContents = "";
	          $.each(errorMsgList, function(key, obj) {
	            $.each(obj, function(dId, msg) {
	              var dateId = dId;
	              var dataMsg = msg;
	              var stringDate = $('#' + dateId).attr('string-date');
	              var msgContents = '<p id=' + dateId + ' class=' + 'save-warn-msg-contents' + '>' + stringDate + '&nbsp;-&nbsp;' + dataMsg + '<p>';
	              alertMsgContents = msgContents + '\n'
	              $('#' + dateId).children().children('#form-warning').removeClass("none-form-warning");
	              $('#' + dateId).children().children('#form-warning').addClass("form-warning");
	              $('#warn-msg-title').after(msgContents);
	              if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
	                $(".warn-msg").removeAttr('style');
	                $(".warn-msg").css('bottom','20px');
	              }
	            });
	          });
	        }else{
	          alert('エラーが発生しました。');
	        }
       });
    });

    $(".saveModal").click(function () {
      $(".warn-msg").addClass("hidden");
      $('.warn-msg-contents').remove();
      $('.save-warn-msg-contents').remove();
      $('#form-warning').addClass("none-form-warning");
      $('#form-warning').removeClass("form-warning");
      $('#inputForm').attr("action", "/attendance/save/"+empNum+"/"+nowYearMonth);
      $.postAjax('#inputForm', function(data){
        if(data.result == "ok"){
		  localStorage.saveMsg = '勤怠を保存しました。';
          location.reload();
        }else if(data.result == "ng"){
	          $(".warn-msg").removeClass("hidden");
		      var msgContents = '<p id="warnStatusSave" class= "save-warn-msg-contents">' + data.msg + '<p>';
		      $(".warn-msg").removeClass("hidden");
		      $('#warn-msg-title').after(msgContents);
        }else{
          alert('エラーが発生しました。');
        }
      });
    });

    $("#statusAndWorksaveBtn").click(function () {
      $(".warn-msg").addClass("hidden");
      $('.warn-msg-contents').remove();
      $('.save-warn-msg-contents').remove();
      $('#form-warning').addClass("none-form-warning");
      $('#form-warning').removeClass("form-warning");
      $.postAjax('#statusAndWorkForm', function(data){
        if(data.result == "ok"){
          localStorage.saveMsg = 'ステータスと作業実績内訳詳細を保存しました。';
          location.reload();
        }else if(data.result == "ng"){
          var msgContents = '<p id="warnStatusSave" class= "save-warn-msg-contents">ステータスが保存できません。 <p>';
          $(".warn-msg").removeClass("hidden");
          $('#warn-msg-title').after(msgContents);
        }else{
          alert('エラーが発生しました。');
        }
      });
    });

	// TODO 成功時msgを取得する
    $(".fixBtn").click(function () {
       $.totalInsert();
       $(".warn-msg").addClass("hidden");
       $('.warn-msg-contents').remove();
       $('.save-warn-msg-contents').remove();
       $('#form-warning').addClass("none-form-warning");
       $('#form-warning').removeClass("form-warning");
       $('.fix-contents').remove();
       $('#inputForm').attr("action", "/attendance/fixCheck/"+empNum+"/"+year +"/"+month);
       $.postAjax('#inputForm', function(data){
            if(data.result == "ok"){
              var msgContents = '<p id="approveBody" class="fix-contents">てすと</p>';
		      $('#approveTitle').after(msgContents);
		　　　　　 $('#fixModal').modal('show');
            }else if(data.result == "ng"){
	          $(".warn-msg").removeClass("hidden");
	          var errorMsgList = data.msg;
	          var alertMsgContents = "";
	          $.each(errorMsgList, function(key, obj) {
	            $.each(obj, function(dId, msg) {
	              var dateId = dId;
	              var dataMsg = msg;
	              var stringDate = dateId + '日';
	              var msgContents = '<p id=' + dateId + ' class=' + 'save-warn-msg-contents' + '>' + stringDate + '&nbsp;-&nbsp;' + dataMsg + '<p>';
	              alertMsgContents = msgContents + '\n'
	              $('#' + dateId).children().children('#form-warning').removeClass("none-form-warning");
	              $('#' + dateId).children().children('#form-warning').addClass("form-warning");
	              $('#warn-msg-title').after(msgContents);
	              if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
	                $(".warn-msg").removeAttr('style');
	                $(".warn-msg").css('bottom','20px');
	              }
	            });
	          });
	            if(!$("p").hasClass('warn-msg-contents')){
                $(".saveBtn").prop('disabled', false);
                $(".saveBtn-a").removeClass("hidden");
                if(!$("p").hasClass('save-warn-msg-contents')){
                  $(".warn-msg").addClass("hidden");
                }
              }
            }else{
               alert('エラーが発生しました。');
            }
      });
    });

    $(".fixModal").click(function () {
      $(".warn-msg").addClass("hidden");
      $('.warn-msg-contents').remove();
      $('.fix-warn-msg-contents').remove();
      $('#form-warning').addClass("none-form-warning");
      $('#form-warning').removeClass("form-warning");
      $('#inputForm').attr("action", "/attendance/fix/"+empNum+"/"+nowYearMonth);
      $.postAjax('#inputForm', function(data){
            if(data.result == "ok"){
              var yearMonth = year + '年' + month + '月';
              localStorage.saveMsg = yearMonth +'の勤怠情報を確定しました。';
          	  location.reload();
            }else if(data.result == "ng"){
	          $(".warn-msg").removeClass("hidden");
		      var msgContents = '<p id="warnStatusSave" class= "save-warn-msg-contents">' + data.msg + '<p>';
		      $('#warn-msg-title').after(msgContents);
            }else{
               alert('エラーが発生しました。');
            }
      });
    });

    $(".pdfBtn").click(function () {
　　　　　$('#pdfModal').modal('show');
    });

    $(".otherInputModalBtn").click(function () {
       var dateId = $(this).attr('date-id');
       $('#breakdown1-modal-' + dateId).val($('.breakdown1-' + dateId).val());
       $('#breakdown2-modal-' + dateId).val($('.breakdown2-' + dateId).val());
       $('#breakdown3-modal-' + dateId).val($('.breakdown3-' + dateId).val());
       $('#breakdown4-modal-' + dateId).val($('.breakdown4-' + dateId).val());
       $('#deductionNight-modal-' + dateId).val($('.deductionNight-' + dateId).val());
       $('#deductionOther-modal-' + dateId).val($('.deductionOther-' + dateId).val());
       $('#holidayClass-modal-' + dateId).val($('.holidayClass-' + dateId).val());
       $('#remarks-modal-' + dateId).val($('.remarks-' + dateId).val());
       $('#performanceTime-modal-' + dateId).val($('.performanceTime-' + dateId).val());
       $('#otherInputModal' + dateId).modal('show');
    });

    $(".otherInputModalCloseBtn").click(function () {
      var dateId = $(this).attr('date-id');
      $('.breakdown1-' + dateId).val($('#breakdown1-modal-' + dateId).val());
      $('.breakdown2-' + dateId).val($('#breakdown2-modal-' + dateId).val());
      $('.breakdown3-' + dateId).val($('#breakdown3-modal-' + dateId).val());
      $('.breakdown4-' + dateId).val($('#breakdown4-modal-' + dateId).val());
      $('.deductionNight-' + dateId).val($('#deductionNight-modal-' + dateId).val());
      $('.deductionOther-' + dateId).val($('#deductionOther-modal-' + dateId).val());
      $('.holidayClass-' + dateId).val($('#holidayClass-modal-' + dateId).val());
      $('.remarks-' + dateId).val($('#remarks-modal-' + dateId).val());
      $.totalInsert();
    });

    $.addZero = function(num) {
      var number = String(num);
      if(number.substr( -2, 2 ) != ".5"){
        return number + ".0"
      }else{
        return number
      }
    };

    $.totalInsert = function() {
       var sumBreakdown1 = 0.0;
       var sumBreakdown2 = 0.0;
       var sumBreakdown3 = 0.0;
       var sumBreakdown4 = 0.0;
       var sumDeductionNight = 0.0;
       var sumDeductionOther = 0.0;
       var sumPerformanceTime = 0.0;
       var sumNightWork = 0.0;
       var sumSalaried = 0.0;

       $(".breakdown1").each(function(){
         var value = $(this).val();
         sumBreakdown1 = sumBreakdown1 + parseFloat(value);
       });
       $("#sumBreakdown1").val($.addZero(sumBreakdown1));

       $(".breakdown2").each(function(){
         var value = $(this).val();
         sumBreakdown2 = sumBreakdown2 + parseFloat(value);
       });
       $("#sumBreakdown2").val($.addZero(sumBreakdown2));

       $(".breakdown3").each(function(){
         var value = $(this).val();
         sumBreakdown3 = sumBreakdown3 + parseFloat(value);
       });
       $("#sumBreakdown3").val($.addZero(sumBreakdown3));

       $(".breakdown4").each(function(){
         var value = $(this).val();
         sumBreakdown4 = sumBreakdown4 + parseFloat(value);
       });
       $("#sumBreakdown4").val($.addZero(sumBreakdown4));

       $(".deductionNight").each(function(){
         var value = $(this).val();
         sumDeductionNight = sumDeductionNight + parseFloat(value);
       });
       $("#sumDeductionNight").val($.addZero(sumDeductionNight));

       $(".deductionOther").each(function(){
         var value = $(this).val();
         sumDeductionOther = sumDeductionOther + parseFloat(value);
       });
       $("#sumDeductionOther").val($.addZero(sumDeductionOther));

       $(".nightWork").each(function(){
         var nightWork = $(this)
         hiddenClosingTime = $(this).parent().parent().find(".hiddenClosingTime").val();

         if(hiddenClosingTime != ""){
           var url = jsRoutes.controllers.AttendanceCtl.getNightWork(hiddenClosingTime);
           $.ajax({
             type : 'GET',
             url : url.url,
             success : function(data) {
               if (data.result == "ok") {
                 var value = parseFloat(data.value);
                 $(nightWork).val($.addZero(value));
               }
             }
           });
         }
         var value = $(this).val();
         sumNightWork = sumNightWork + parseFloat(value);
       });
       $("#sumNightWork").val($.addZero(sumNightWork));

       $(".salaried").each(function(){
         var salaried = $(this)
         var hiddensalaried = $(this).parent().parent().find(".holidayClass").val();
         var trId = $(this).parent().parent().attr('prev-tr-id');
         var url = jsRoutes.controllers.AttendanceCtl.getSalaried(hiddensalaried);
         $.ajax({
           type : 'GET',
           url : url.url,
           success : function(data) {
             if (data.result == "ok") {
               var beforeHolidayClassValue = $('#beforeHolidayClass' + trId).val();
               var performanceTime = $('#performanceTime' + trId);
               var sumPerformanceTime =  $("#sumPerformanceTime").val();
               var value = data.value;
               var sumValue = parseFloat(performanceTime) + parseFloat(value) - parseFloat(beforeHolidayClassValue);
               $(salaried).val($.addZero(value));
               $('#beforeHolidayClass' + trId).val(value);
               $('#performanceTime' + trId).val(sumValue);
             }
           }
         });
         var value = $(this).val();
         sumSalaried = sumSalaried + parseFloat(value);
       });
       $("#sumSalaried").val($.addZero(sumSalaried));

       $(".performanceTime").each(function(){
         var value = $(this).val();
         sumPerformanceTime = sumPerformanceTime + parseFloat(value);
       });
       $("#sumPerformanceTime").val($.addZero(sumPerformanceTime));

    };
    $.totalInsert();

    $('[data-toggle="popover"]').popover({
      trigger: 'focus',
      placement: 'top'
     });

    $(".holiday").each(function(){
      var calendarClass = $(this).parent().find(".calendar");
      var calendar = calendarClass.text();
      var value = calendar.substr(calendar.length-2);
      if(value.match('土')){
        calendarClass.css('color', 'blue');
      }else{
        calendarClass.css('color', 'red');
      }
    });

    // TODO 差戻し時に保存データを差戻しだけにする
    $(".remandLavel").each(function(){
  	  var trId = $(this).parent().find(".calendar").text();
      var msgContents = '<p class= "save-warn-msg-contents">' + trId + ' - 差し戻しがあります。修正して再度確定してください。</p>';
	  $(".warn-msg").removeClass("hidden");
	  $(".saveBtn").addClass("hidden");
	  $(".fixBtn").prop('disabled', true);
	  $('#warn-msg-title').after(msgContents);
    });

    $(".requestLavel").each(function(){
	  $(".saveBtn").addClass("hidden");
    });

    $(".approveLabel").each(function(){
      var targetTr = $(this).closest('tr');
      var targetTrId = targetTr.attr('id');
      $('#' + targetTrId).children().children('.openingTime').prop('disabled', true);
      $('#' + targetTrId).children().children('.closingTime').prop('disabled', true);
      $('#' + targetTrId).children().children('.breakdown1').prop('disabled', true);
      $('#' + targetTrId).children().children('.breakdown2').prop('disabled', true);
      $('#' + targetTrId).children().children('.breakdown3').prop('disabled', true);
      $('#' + targetTrId).children().children('.breakdown4').prop('disabled', true);
      $('#' + targetTrId).children().children('.deductionNight').prop('disabled', true);
      $('#' + targetTrId).children().children('.deductionOther').prop('disabled', true);
      $('#holidayClass'+ targetTrId).prop('disabled', true);
      $('#shiftClass'+ targetTrId).prop('disabled', true);
      $('#remarks' + targetTrId).prop('disabled', true);
    });

    $.postAjax = function(formId, callBack, url) {
      var form = (typeof formId == "string" ? $(formId) : formId);
      var accessUrl = null;
      try {
        if(form && form[0]) {
          accessUrl = url ? url : form.attr('action')
          $.ajax({
            url     : accessUrl,
            data    : form.serialize(),
            type    : "POST",
            success : function(data) {
              callBack(data);
            },
            complete : function() {
            }
          });
        }
      } catch(e) {
        alert('エラーが発生しました。');
      }
    };
  });
