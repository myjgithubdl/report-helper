/*继承jQuery实现垂直菜单点击展开或关闭效果 start*/
(function ($) {
    $.fn.homeMenu = function (b) {
        var c, item, httpAdress;
        b = jQuery.extend({Speed: 220, autostart: 1, autohide: 1, menuItemClass: 'menu-item'}, b);
        c = $(this);
        item = c.children("ul").parent("li").children("."+b.menuItemClass);
        httpAdress = window.location;
        item.each(function () {
            if (!$(this).hasClass("active")) {
                $(this).addClass("unactive");
            }
        });

        function _item() {
            console.log("." + b.menuItemClass)
            var menuItemObj = $(this);
            if (b.autohide) {
                menuItemObj.parent().parent().find(".active").parent("li").children("ul").slideUp(b.Speed / 1.2,
                    function () {
                        $(this).parent("li").children("." + b.menuItemClass).removeAttr("class");
                        $(this).parent("li").children("." + b.menuItemClass).attr("class", "unactive");
                    });
            }

            if (menuItemObj.hasClass("unactive")) {//隐藏、需展开子菜单
                menuItemObj.parent("li").children("ul").slideDown(b.Speed,
                    function () {
                        menuItemObj.removeClass("unactive").addClass("active");

                        menuItemObj.find(".menu-right-icon").find("i").removeClass("icon-angle-right").addClass("icon-angle-down");
                    });
            } else if (menuItemObj.hasClass("active")) {
                menuItemObj.removeClass("active").addClass("unactive");

                menuItemObj.find(".menu-right-icon").find("i").removeClass("icon-angle-down").addClass("icon-angle-right");

                menuItemObj.parent("li").children("ul").slideUp(b.Speed);
            }
        }

        //item.unbind('click').click(_item);
        item.unbind('click').click(_item);
        if (b.autostart) {
            return;
            c.children("." + b.menuItemClass).each(function () {
                if (this.href == httpAdress) {
                    $(this).parent("li").parent("ul").slideDown(b.Speed,
                        function () {
                            $(this).parent("li").children(".unactive").removeAttr("class");
                            $(this).parent("li").children("." + b.menuItemClass).addClass("active");
                        });
                }
            });
        }
    };
})(jQuery);
/*继承jQuery实现垂直菜单点击展开或关闭效果 end*/
