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