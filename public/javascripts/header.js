  $(function() {
	// ログアウト
	$("#logoutBtn").click(function () {
		var url = jsRoutes.controllers.AuthCtl.logout();
			$.ajax({
				type : 'GET',
				url : url.url,
				success : function(data) {
					if (data.result == "ok") {
						window.location.href = data.link;
					}
				}
			});
	});
	// 管理者ページへ
	$("#pageoutBtn").click(function () {
		var url = jsRoutes.controllers.AuthCtl.menuRedirect();
			$.ajax({
				type : 'GET',
				url : url.url,
				success : function(data) {
					if (data.result == "ok") {
						window.location.href = data.link;
					}
				}
			});
	});
	// 差戻しデータの表示
	$(function() {
		$('span').hover(function() {
			$(this).next('p').show();
		}, function(){
			$(this).next('p').hide();
		});
	});
	$(".fixRemand").each(function () {
		var url = jsRoutes.controllers.AttendanceCtl.isApproveRemand();
		$.ajax({
			type : 'GET',
			url : url.url,
			success : function(data) {
				if(data.result == "ok"){
					$(".fixRemand").removeClass("hidden");
					var dataList = data.value;
					var count = 0;
					$.each(dataList, function(key, obj) {
						$.each(obj, function(monthyears, date) {
							count = count+1;
							var msgContents = '<p class= "arrow_box">' + monthyears + '　' + count + '件';
							$(".fixRemand").after(msgContents);
						});
					});
				}else if(data.result == "ng"){
					$(".fixRemand").addClass("hidden");
				}else{
					alert('却下データ取得中にエラーが発生しました');
				}
			}
		});
	});
});