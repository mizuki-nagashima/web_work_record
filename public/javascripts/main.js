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
});

(function($) {

    // 読み込みloading表示
	$(function() {
		var h = $(window).height();
		$('#contents').css('display','none');
		$('#loader-bg ,#loader').height(h).css('display','block');
	});
	$(window).load(function () {
		$('#loader-bg').delay(600).fadeOut(500);
		$('#loader').delay(300).fadeOut(100);
		$('#contens').css('display', 'block');
	});
	$(function(){
		setTimeout('stopload()',5000);
	});
	function stopload(){
		$('#contens').css('display','block');
		$('#loader-bg').delay(600).fadeOut(500);
		$('#loader').delay(300).fadeOut(100);
	}
	// リロード TODO: ?が無い場合対応
	function keep_scroll_reload() {
	    var re = /&page_x=(\d+)&page_y=(\d+)/;
	    var position = '&page_x=' + document.body.scrollLeft + '&page_y=' + document.body.scrollTop;
	    if(!window.location.href.match(re)) {
	        //初回
	        window.location.href = window.location.href + position;
	    } else {
	        //2回目以降
	        window.location.href = window.location.href.replace(/&page_x=(\d+)&page_y=(\d+)/,position);
	    }
	}

	// スクロール位置を復元
	function restore_scroll() {
	    var re = /&page_x=(\d+)&page_y=(\d+)/;
	    if(window.location.href.match(re)) {
	        var position = window.location.href.match(re)
	        window.scrollTo(position[1],position[2]);
	    }
	}
	restore_scroll();
});