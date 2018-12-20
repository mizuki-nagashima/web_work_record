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

     // 登録ボタン
      $("#registEmpBtn").click(function () {
    	  $(".warn-msg").addClass("hidden");
    	  $('.warn-msg-contents').remove();
    	  $(".messageContents").remove();
    	  $.postAjax('#registEmpForm', function(data){
    			  if(data.result == "ok"){
    				  var msgContents = '<p class=' + 'warn-msg-contents' + '>' + data.msg + '</p>';
    				  $('.messageTitle').after(msgContents);
    				  $('#registModal').modal('show');
    			  }else if(data.result == "ng"){
    				  $(".warn-msg").removeClass("hidden");
    				  $('.warn-msg-contents').remove();
    				  var errorMsgList = data.msg;
    				  var alertMsgContents = "";
    				  $.each(errorMsgList, function(key, obj) {
    					  $.each(obj, function(dId, msg) {
    						  var dateId = dId;
    						  var dataMsg = msg;
    						  var msgContents = '<p id=' + dateId + ' class=' + 'warn-msg-contents' + '>' + dataMsg + '<p>';
    						  alertMsgContents = msgContents + '\n'
    						  $('#warn-msg-title').after(msgContents);
    					  });
    				  });
    			  }else{
    				  alert('エラーが発生しました。');
    			  }
    	  });
      });

      // 登録時モーダル→OKボタン押下
      $(".registModalBtn").click(function () {
		  $('#registEmpForm').attr("action", '/user/regist');
    	  $.postAjax('#registEmpForm', function(data){
    		  if(data.result == "ok"){
    			  localStorage.saveMsg = '社員情報を登録しました。';
    			  location.reload();
    		  }else if(data.result == "ng"){
				  $(".warn-msg").removeClass("hidden");
				  var msgContents = '<p class=' + 'warn-msg-contents' + '>' + data.msg + '<p>';
				  $('#warn-msg-title').after(msgContents);
    		  }else{
    			  alert('エラーが発生しました。');
    		  }
    	  });
      });

    // 編集ボタン
    $(".editButton").click(function () {
        $(".warn-msg").addClass("hidden");
        $('.warn-msg-contents').remove();
	    var targetTr = $(this).closest('tr');
        var targetTrId = targetTr.attr('id');
        $('input[name="employeeNo"]').val($('#employeeNo-' + targetTrId).val());
		$('input[name="employeeName"]').val($('#employeeName-' + targetTrId).val());
		$('input[name="employeeNameKana"]').val($('#employeeNameKana-' + targetTrId).val());
		$('#empClass' + $('#employment-' + targetTrId).val()).attr('checked',true);
		$('#authClass' + $('#authority-' + targetTrId).val()).attr('checked',true);
		$('select[name="positionCode"]').val($('#position-' + targetTrId).val());
		$('select[name="departmentCode"]').val($('#department-' + targetTrId).val());
		$('select[name="divisionCode"]').val($('#division-' + targetTrId).val());
		$('select[name="businessCode"]').val($('#business-' + targetTrId).val());
		$('select[name="businessTeamCode"]').val($('#businessTeam-' + targetTrId).val());
    });

    // TODO 削除前モーダル
    var url = "";
    $(".deleteButton").click(function () {
        $(".warn-msg").addClass("hidden");
        $('.warn-msg-contents').remove();
        $(".messageContents").remove();
	    var targetTr = $(this).closest('tr');
        var targetTrId = targetTr.attr('id');
        var empNo = $('#employeeNo' + targetTrId).val();
        url = jsRoutes.controllers.RegistEmpCtl.deleteEmp(empNo);
    	var msgContents =  "<p class= messageContents>社員番号："+empNo + '</p>';
        $('.messageTitle').after(msgContents);
    	$('#registEmpForm').attr("action", '/user/regist');
    	$('#deleteModal').modal('show');
    });

    $(".deleteModalBtn").click(function () {
   	 	$.ajax({
          type : 'POST',
          url : url.url,
     	  success : function(data) {
            if(data.result == "ok"){
              localStorage.saveMsg = data.msg;
          	  location.reload();
          	}else if(data.result == "ng"){
              $(".warn-msg").removeClass("hidden");
	          var errorMsgList = data.msg;
	          var alertMsgContents = "";
	          $.each(errorMsgList, function(key, obj) {
	 	         $.each(obj, function(dId, msg) {
		              var dateId = dId;
		              var dataMsg = msg;
		              var msgContents = '<p id=' + dateId + ' class=' + 'warn-msg-contents' + '>' + dataMsg + '<p>';
		              alertMsgContents = msgContents + '\n'
		              $('#warn-msg-title').after(msgContents);
		         });
	          });
            }else{
               alert('エラーが発生しました。');
            }
          }
      });
    });

    $(".changeDepartmentCd").change(function(){
      var departmentCode = $('#departmentCode').val();
      if(departmentCode != 00 || departmentCode != ""){
        var url =  jsRoutes.controllers.AttendanceCtl.getDivisionList(departmentCode);
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

    $(".warn-msg").click(function () {
      $(this).addClass("hidden");
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
    if(!navigator.userAgent.match(/(iPhone|iPod|Android)/)){
      $(".registEmp-box").css("width", "500px");
    }
  });