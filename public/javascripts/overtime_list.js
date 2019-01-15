  $(function(){
    if(localStorage.saveMsg){
      $(".success-msg").removeClass("hidden");
      $('.success-msg').text(" " + localStorage.saveMsg).fadeIn(1000).delay(2000).fadeOut(2000);
      localStorage.removeItem('saveMsg');
    }else if(navigator.userAgent.match(/(iPhone|iPod|Android)/)){
      $(document).ready(function () {
        setTimeout(function() { window.scrollTo(0,$('#attendanceSaveTop').offset().top); }, 1);
      });
    }

    // プルダウン変更時に遷移
    $('select[name=moveYear]').change(function() {
      if ($(this).val() != '') {
    	var year = $(this).val();
    	var url = jsRoutes.controllers.OvertimeListCtl.index(year);
        window.location.href = url.url;
      }
    });

    $(".pdfBtn").click(function () {
    	$('#pdfModal').modal('show');
    });

	$('#tabelData').DataTable({
	    stateSave: true,
		paging: false,
		searching: false,
		"autoWidth": false,
		columnDefs: [
			{"orderable": false ,"targets": [1,3,5,6,9,10,11] }
		]
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