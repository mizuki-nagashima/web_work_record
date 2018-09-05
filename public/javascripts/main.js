(function($) {
	$.getPjax = function(link, container) {
		if (typeof link != "string") {
			link = link.attr('href');
		}
		$.pjax({
			area  : container ? container : ['#pjax-container'],
					load  : {
						script: true,
						css   : false
					},
					callbacks: {
						ajax: {
							success : function(event, setting, data, textStatus, jqXHR) {
								//alert('success');
								//console.error(data);
								$('#wrap-contents').hide();
							},
							error : function(event, setting, jqXHR, textStatus, errorThrown) {
								$.handleSendError(jqXHR, textStatus, errorThrown);
							}
						},
						update : {
							content : {
								after : function(event, setting, srcDocument, dstDocument) {
									$('#wrap-contents').fadeIn('slow');
								}
							}
						}
					}
		}).click(link);
		return false;
	};
})(jQuery);

$(function() {
	if(localStorage.saveMsg){
		$('.success-msg').text(localStorage.saveMsg).fadeIn(1000).delay(2000).fadeOut(2000);
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

	$('.datepicker').datepicker({
		format: 'yyyy/mm',
		language: 'ja',
		autoclose: true,
		minViewMode: 'months'
	}).on({
		changeDate : function(){
			var yearMonth = $(this).val();
			var nowYearMonth = $('#monthsYears').val();
			var empNum = '@empNo';
			var url = '@routes.AttendanceCtl.moveTargetYearMonth("0","0","0")';
			url = url.substring(0, url.length - 5) + empNum + '/' + yearMonth.replace("/","") + '/' + nowYearMonth;
			$.ajax({
				type : 'GET',
				url : url,
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
		if(((start != "")&&(end  != "")) || holidayClass != 00){
			$('#' + targetTrId + '-closingTime').val(end);
			var url = '@routes.AttendanceCtl.getPerformanceTime("0","0","0","0")';
			var deductionNight = $('#' + targetTrId).children().children('.deductionNight').val();
			var deductionOther = $('#' + targetTrId).children().children('.deductionOther').val();
			var deduction = parseFloat(deductionNight) + parseFloat(deductionOther);
			deduction = deduction.toString();
			if((start == "") || (end == "")){
				start = "none"
					end = "none"
			}
			url = url.substring(0, url.length - 7) + start + '/' + end + '/' + holidayClass + '/' + deduction;
			$.ajax({
				type : 'GET',
				url : url,
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
						$('#' + targetTrId).children().children('#performanceTime').val("");
						$.totalInsert();
						if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
							$(".warn-msg").css('top','initial');
							$(".warn-msg").css('bottom','20px');
						}
					}
				}
			});
		}else{
			$('#' + targetTrId).children().children('#performanceTime').val("");
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

	/*
  $(".moveBtn").click(function () {
      var yearMonth = $(this).parent().find('.moveYearMonth').val();
      var nowYearMonth = $('#monthsYears').val();
      var empNum = '@empNo';
      var url = '@routes.AttendanceCtl.moveTargetYearMonth("0","0","0")';
      url = url.substring(0, url.length - 5) + empNum + '/' + yearMonth.replace("/","") + '/' + nowYearMonth;
      $.ajax({
        type : 'GET',
        url : url,
        success : function(data) {
          if (data.result == "ok") {
            window.location.href = data.link;
          }
        }
      });
  });
	 */

	$(".warn-msg").click(function () {
		$(this).addClass("hidden");
	});

	$(".saveBtn").click(function () {
		$(".warn-msg").addClass("hidden");
		$('.warn-msg-contents').remove();
		$('.save-warn-msg-contents').remove();
		$('#form-warning').addClass("none-form-warning");
		$('#form-warning').removeClass("form-warning");
		$.postAjax('#inputForm', function(data){
			if(data.result == "ok"){
				localStorage.saveMsg = '勤怠を保存しました。';
				location.reload();
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

	$("#statusAndWorksaveBtn").click(function () {
		$.postAjax('#statusAndWorkForm', function(data){
			if(data.result == "ok"){
				localStorage.saveMsg = 'ステータスと作業実績内訳詳細を保存しました。';
				location.reload();
			}else{
				alert('エラーが発生しました。');
			}
		});
	});

	// TODO
	$(".fix").click(function () {
		// fix処理
		var url = '@routes.AttendanceCtl.fix(empNo, year + month)'
			$.ajax({
				type:'POST',
				url:url,
				success:function(data) {
					if(data.result == "ok"){
						window.location.href = data.link;
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
				}
			});
		location.reload();
	});


	$(".pdf").click(function () {
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


	$("#logoutBtn").click(function () {
		var url = '@routes.AuthCtl.logout'
			$.ajax({
				type : 'GET',
				url : url,
				success : function(data) {
					if (data.result == "ok") {
						window.location.href = data.link;
					}
				}
			});
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
				var url = '@routes.AttendanceCtl.getNightWork("0")';
				url = url.substring(0, url.length - 1) + hiddenClosingTime;
				endTime = ""
					$(this).parent().parent().find(".hiddenClosingTime").val("");
				$.ajax({
					type : 'GET',
					url : url,
					success : function(data) {
						if (data.result == "ok") {
							var value = parseFloat(data.value);
							$(nightWork).val(value);
						}
					}
				});
			}
		});

		$(".salaried").each(function(){
			var salaried = $(this)
			var hiddensalaried = $(this).parent().parent().find(".holidayClass").val();
			var trId = $(this).parent().parent().attr('prev-tr-id');
			var url = '@routes.AttendanceCtl.getSalaried("0")';
			url = url.substring(0, url.length - 1) + hiddensalaried;
			$.ajax({
				type : 'GET',
				url : url,
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
		});

		$(".performanceTime").each(function(){
			var value = $(this).val();
			sumPerformanceTime = sumPerformanceTime + parseFloat(value);
		});
		$("#sumPerformanceTime").val($.addZero(sumPerformanceTime));

	};
	$.totalInsert();
	/*
  $.headerScroll = function() {
     var $table = $(".table");          // テーブルの要素を取得
     var $thead = $table.children("thead");  // thead取得
     var toffset = $table.offset();          // テーブルの位置情報取得
     // テーブル位置+テーブル縦幅 < スクロール位置 < テーブル位置
     if(toffset.top + $table.height()< $(window).scrollTop()
       || toffset.top > $(window).scrollTop()){
         // クローンテーブルが存在する場合は消す
         var $clone = $("#clonetable");
         if($clone.length > 0){
             $clone.css("display", "none");
         }
     }else if(toffset.top < $(window).scrollTop()){
       // クローンテーブルが存在するか確認
       var $clone = $("#clonetable");
       if($clone.length == 0){
           // 存在しない場合は、theadのクローンを作成
           $clone= $thead.clone(true);
           // idをclonetableとする
           $clone.attr("id", "clonetable");
           // body部に要素を追加
           $clone.appendTo("body");
           // theadのCSSをコピーする
           StyleCopy($clone, $thead);
           // theadの子要素(tr)分ループさせる
           var ttttt = $thead.children("tr").length;;
           for(var i = -1; i < $thead.children("tr").length; ++i)
           {
               // i番目のtrを取得
               var $theadtr = $thead.children("tr").eq(i);
               var $clonetr = $clone.children("tr").eq(i);
               // trの子要素(th)分ループさせる
               for (var j = 0; j < $theadtr.eq(i).children("th").length; j++){
                   // j番目のthを取得
                   var $theadth = $theadtr.eq(i).children("th").eq(j);
                   var $cloneth = $clonetr.eq(i).children("th").eq(j);
                   // thのCSSをコピーする
                   StyleCopy($cloneth, $theadth);
               }
           }
       }

       // コピーしたtheadの表示形式をtableに変更
       $clone.css("display", "table");
       // positionをブラウザに対し絶対値とする
       $clone.css("position", "fixed");
       $clone.css("border-collapse", "collapse");
       // positionの位置を設定(left = 元のテーブルのleftとする)
       $clone.css("left", toffset.left - $(window).scrollLeft());
       // positionの位置を設定(topをブラウザの一番上とする)
       $clone.css("top", "0px");
       $clone.css("background-color", "rgba(211, 229, 255,0.8)");
     }
  };

  // CSSのコピー
  function StyleCopy($copyTo, $copyFrom){
      $copyTo.css("width",
                  $copyFrom.css("width"));
      $copyTo.css("height",
                  $copyFrom.css("height"));

      $copyTo.css("padding-top",
                  $copyFrom.css("padding-top"));
      $copyTo.css("padding-left",
                  $copyFrom.css("padding-left"));
      $copyTo.css("padding-bottom",
                  $copyFrom.css("padding-bottom"));
      $copyTo.css("padding-right",
                  $copyFrom.css("padding-right"));

      $copyTo.css("background",
                  $copyFrom.css("background"));
      $copyTo.css("background-color",
                  $copyFrom.css("background-color"));
      $copyTo.css("vertical-align",
                  $copyFrom.css("vertical-align"));

      $copyTo.css("border-top-width",
                  $copyFrom.css("border-top-width"));
      $copyTo.css("border-top-color",
                  $copyFrom.css("border-top-color"));
      $copyTo.css("border-top-style",
                  $copyFrom.css("border-top-style"));

      $copyTo.css("border-left-width",
                  $copyFrom.css("border-left-width"));
      $copyTo.css("border-left-color",
                  $copyFrom.css("border-left-color"));
      $copyTo.css("border-left-style",
                  $copyFrom.css("border-left-style"));

      $copyTo.css("border-right-width",
                  $copyFrom.css("border-right-width"));
      $copyTo.css("border-right-color",
                  $copyFrom.css("border-right-color"));
      $copyTo.css("border-right-style",
                  $copyFrom.css("border-right-style"));

      $copyTo.css("border-bottom-width",
                  $copyFrom.css("border-bottom-width"));
      $copyTo.css("border-bottom-color",
                  $copyFrom.css("border-bottom-color"));
      $copyTo.css("border-bottom-style",
                  $copyFrom.css("border-bottom-style"));
  };
	 */
	$('[data-toggle="popover"]').popover({
		trigger: 'focus',
		placement: 'top'
	});

	$(".fa-calendar").each(function(){
		var calendarClass = $(this).parent().find(".calendar");
		var calendar = calendarClass.text();
		var value = calendar.substr(calendar.length-2);
		if(value.match('土')){
			calendarClass.addClass("text-primary");
		}else if (value.match('日')){
			calendarClass.addClass("text-danger");
		}
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

