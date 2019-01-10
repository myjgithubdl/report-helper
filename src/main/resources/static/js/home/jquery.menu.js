
/*继承jQuery实现垂直菜单点击展开或关闭效果 start*/
(function ($) {
    $.fn.homeMenu = function (b) {
        var c, item, httpAdress;
        b = jQuery.extend({Speed: 220, autostart: 1, autohide: 1}, b);
        c = $(this);
        item = c.children("ul").parent("li").children("a");
        httpAdress = window.location;
        item.each(function () {
            if (!$(this).hasClass("active")) {
                $(this).addClass("unactive");
            }
        });
        function _item() {
            var a = $(this);
            var thisClass = a.attr("class");
            if (b.autohide) {
                a.parent().parent().find(".active").parent("li").children("ul").slideUp(b.Speed / 1.2,
                    function () {
                        $(this).parent("li").children("a").removeAttr("class");
                        $(this).parent("li").children("a").attr("class", "unactive");
                    });
            }
            if (a.hasClass("unactive")) {//隐藏、需展开子菜单
                a.parent("li").children("ul").slideDown(b.Speed,
                    function () {
                        a.removeClass("unactive").addClass("active");
                    });
            }else if (a.hasClass("active")) {
                a.removeClass("active").addClass("unactive");
                a.parent("li").children("ul").slideUp(b.Speed);
            }
        }

        //item.unbind('click').click(_item);
        item.unbind('click').click(_item);
        if (b.autostart) {
            return;
            c.children("a").each(function () {
                if (this.href == httpAdress) {
                    $(this).parent("li").parent("ul").slideDown(b.Speed,
                        function () {
                            $(this).parent("li").children(".unactive").removeAttr("class");
                            $(this).parent("li").children("a").addClass("active");
                        });
                }
            });
        }
    };
})(jQuery);
/*继承jQuery实现垂直菜单点击展开或关闭效果 end*/
