var upDatePassZuiMessager;

$(function () {
    Index.init();
});


var Index = {
    init: function () {
        IndexMVC.View.initControl();
        IndexMVC.View.bindEvent();
        IndexMVC.View.bindValidate();
    }
};


var IndexMVC = {
        URLs: {
            //获取用户的菜单URL
            getSysMenus:{
                //url: ReportHelper.ctxPath + '/home/getSysMenus',
                url: ReportHelper.ctxPath + '/home/getSysMenusAndReport',
                method: 'POST'
            },
            updatePwd: {
                url: ReportHelper.ctxPath + '/user/updatePwd',
                method: 'POST'
            }
        },
        UserInfo: {
            user: null
        },
        Data: {
            /*左边菜单的宽度*/
            westLayoutWidth: {
                width1: 200,
                width2: 40,
                curWidth: 200
            },
            /*用户的菜单数据*/
            userMenuData: []
        },
        View: {
            /**
             * 初始化操作
             */
            initControl: function () {

                IndexMVC.UserInfo.user =user;

                IndexMVC.View.initIndexPageTab();
                IndexMVC.View.initUserMenu();

            },

            /**
             * 绑定事件
             */
            bindEvent: function () {


            },
            bindValidate: function () {

            },
            initIndexPageTab: function () {
                $('#indexPageTabs').tabs({
                    border: false,
                    onSelect: function (title, index) {
                        var tabSize = $("#indexPageTabs ul li").size();
                        var liHeight = $("#indexPageTabs .tabs li").eq(1).css("height");
                        if (tabSize > 1) {
                            //$("#indexPageTabs .tabs").css({"height":(parseFloat(liHeight)+1)+"px"})
                        }
                        $("#indexPageTabs .tabs").css({"border-color": "#0098CA"});
                        IndexMVC.Controller.setReplaceEasyUIStyle(index);
                    },
                    onClose: function (title, index) {
                        var tabSize = $("#indexPageTabs ul li").size();
                        if (tabSize == 0) {
                            $("#indexPageTabs .tabs").css({"border-color": "#FFFFFF"});
                        }

                        var tab = $('#indexPageTabs').tabs('getSelected');
                        var selectedIndex = $('#indexPageTabs').tabs('getTabIndex', tab);
                        if (selectedIndex == -1) {//选中的tab被删除，需选中另一个tab
                            if (index == 0) {//如果删除最前面的tab、则选中后一个tab
                                $('#indexPageTabs').tabs('select', 0);
                            } else {//选中删除tab之前的tab
                                $('#indexPageTabs').tabs('select', index - 1);
                            }
                        }
                    },
                    onAdd: function () {
                    }
                });

                //增加固定主页
                var mid = "00";
                var screenBtn = '<div id="indexPageFullScrrenBtn' + mid + '" class="icon_homepage fl mt5 mr4"' +
                    'style="width: 16px; height: 16px; position: absolute; top: 45%; margin-top: -8px; margin-left: -4px;z-index: 99;"></div>';

                $('#indexPageTabs').tabs('add', {
                    id: "indexPageFixedTab",
                    //title: menuName,
                    title: screenBtn + '<div class="fl ml15 pl5 pr10">主页</div>',
                    //content: content,
                    content: $("#indexPageHomeTabContent"),
                    closable: false
                });

            },
            /**
             * 加载用户菜单
             */
            initUserMenu: function () {
                var params = {};
                $.ajax({
                    url: IndexMVC.URLs.getSysMenus.url,
                    type: "post",
                    dataType: "json",
                    data: params,
                    //headers: {'Content-Type': 'application/json'},
                    success: function (data) {
                        console.log(data)
                        if ("100" == data.respCode) {
                            var respData = data.respData;
                            if (respData && respData.length > 0) {
                                var ulTempl = $("#indexPageMenuTempl").clone().empty();
                                var liTempl = $("#indexPageMenuLiTempl").clone();


                                for (var index in respData) {
                                    var menuData = respData[index];

                                    var id = menuData.id;
                                    var menuName = menuData.name;
                                    var menuLevel = menuData.level;
                                    var menuParentId = menuData.pid;
                                    var menuPath = menuData.url;
                                    var hasChild = menuData.hasChild;
                                    var menuIcon = menuData.icon;
                                    var openMode = menuData.target;

                                    //创建li标签
                                    var menuLi = liTempl.clone();
                                    var liId = "indexPageMenuLi" + id;
                                    menuLi.attr("id", liId);
                                    menuLi.find(".menu-name").html(menuName).attr("title",menuName);
                                    //是否点击打开tab
                                    if (menuPath && menuPath.length > 0) {
                                        menuLi.find("a").attr("onclick", "IndexMVC.Controller.openMentContent('" + id + "','" + menuPath + "','" + menuName + "','"+openMode+"')");
                                    } else {
                                        menuLi.find("a").removeAttr("onclick");
                                    }
                                    menuLi.find("a").css("padding-left", ((menuLevel - 1) * 18) + 'px');

                                    //设置图标
                                    if (menuIcon && menuIcon.length > 0) {
                                        menuLi.find(".index-page-menu-icon").css("background-image", "url('" + menuIcon + "')");
                                    } else {
                                        menuLi.find(".index-page-menu-icon").css("background-image", "");
                                    }

                                    $("#indexPageMenuUl" + menuParentId).append(menuLi);
                                    if (hasChild == 1) {//有孩子节点
                                        //创建ul标签
                                        var menuUl = ulTempl.clone();
                                        menuUl.attr("id", "indexPageMenuUl" + id);
                                        $("#" + liId).addClass("has-child");
                                        $("#" + liId).append(menuUl);
                                    }


                                }

                            }

                            $("#index-layout-west  li").homeMenu({autohide: 0});
                        }
                    },
                    error: function (error) {
                        console.log("向服务器发送请求失败");
                    }
                });


            }
        },
        Controller: {
            switchWestLayout: function () {

                var westPanel = $('#indexlayout').layout('panel', 'west');
                var curWidth = IndexMVC.Data.westLayoutWidth.curWidth;
                if (curWidth == IndexMVC.Data.westLayoutWidth.width1) {
                    IndexMVC.Data.westLayoutWidth.curWidth = IndexMVC.Data.westLayoutWidth.width2;
                } else {
                    IndexMVC.Data.westLayoutWidth.curWidth = IndexMVC.Data.westLayoutWidth.width1;
                }

                westPanel.panel('resize', {
                    width: IndexMVC.Data.westLayoutWidth.curWidth
                });

                $('#indexlayout').layout('resize', {})
            },
            /**
             * 打开报表
             */
            openMentContent: function (mid, menuPath, menuName,openMode) {
                if($.trim(menuPath) ==''){
                    return;
                }

                menuPath = IndexMVC.Controller.extMenuUrl(menuPath);
                console.log(menuPath)

                //新窗口打开
                if(openMode == 'blank' && menuPath && menuPath.length > 0){
                    window.open(menuPath);
                }else if(openMode == 'none'){

                }else if(openMode == 'iframe'){
                    var screenBtn = '<div id="indexPageFullScrrenBtn' + mid + '" class="icon_fullscrren fl mt5 mr4 cursor-p"' +
                        'style="width: 13px; height: 13px; position: absolute; top: 55%; margin-top: -8px; margin-left: -4px;z-index: 99;" ' +
                        'onclick="IndexMVC.Controller.openFullScrren(\'' + mid + '\');"></div>';
                    var content = '<iframe id=tabIframe' + mid + ' scrolling="auto" frameborder="0"  src="' + menuPath + '" style="width:100%;height:99%;"></iframe>';

                    if ($("#indexPageFullScrrenBtn" + mid).size() == 1) {//已经打开页面
                        //查找tab指标 , 因为tab的title是一段html，所以不能用easyUI的方法查找，只能县查找指标在选中tab
                        var li = $("#indexPageFullScrrenBtn" + mid).next().parents("li");
                        var index = $("#indexPageTabs ul li").index($(li));
                        $('#indexPageTabs').tabs('select', index);
                        return;
                    }


                    $('#indexPageTabs').tabs('add', {
                        id: "indexPageTab" + mid,
                        //title: menuName,
                        title: screenBtn + '<div class="fl ml15 pl5 pr10" style="line-height: 30px;">' + menuName + '</div>',
                        content: content,
                        //iconCls: "icon_fullscrren",
                        closable: true
//            tools:[{
//                iconCls:'icon_fullscrren',
//                handler:function(){
//                	setTimeout(self.openFullScrren(mid) , 500)
//                }
//            }]
                    });
                    //隐藏左边菜单
                    //self.hideMenuList();

                    //添加用户使用菜单信息
                    //self.addUserUseMenuInfo(mid);

                    IndexMVC.Controller.saveUserUseMenuRecord(mid);
                }


            },
            /**
             * 扩展URL
             */
            extMenuUrl: function (menuPath) {
                var returnUrl = menuPath;
                if(menuPath.indexOf("http") == -1){
                    returnUrl=ReportHelper.ctxPath + '/' +menuPath;
                }
                return returnUrl;
            },
            /**
             * 打开全屏
             */
            openFullScrren: function (mid) {
                var element = document.getElementById("tabIframe" + mid);
                //var h1 = document.getElementById("h1");
                //requestFullScreen(elem);// 某个页面元素

                // 判断各种浏览器，找到正确的方法
                var requestMethod = element.requestFullScreen || //W3C
                    element.webkitRequestFullScreen ||    //Chrome等
                    element.mozRequestFullScreen || //FireFox
                    element.msRequestFullScreen; //IE11

                if (requestMethod) {
                    //Chrome、FireFox
                    requestMethod.call(element);
                    //}else if (typeof window.ActiveXObject !== "undefined") {
                    //for Internet Explorer

                    //console.log("window.ActiveXObject-->" + window.ActiveXObject);

                    //	var wscript = new ActiveXObject("WScript.Shell");
                    //    if (wscript !== null) {
                    //        wscript.SendKeys("{F11}");
                    //    }
                } else if (requestMethod == undefined) {
                    console.log("浏览器不支持");
                    alert("您使用的浏览器不支持该功能，请切换为火狐、360、Google Chrome等浏览器");
                }


            },
            /**
             * 设置替换easyUI样式
             * tabIndex  选中的tab的索引号
             */
            setReplaceEasyUIStyle: function (tabIndex) {
                console.info("tabIndex:" + tabIndex)
                if (tabIndex == 0) {
                    $('#indexPageTabs ul li').eq(tabIndex).removeClass("li-tabs-unselected").addClass("li-tabs-selected").siblings().removeClass("li-tabs-selected").addClass("li-tabs-unselected");
                } else {
                    $('#indexPageTabs ul li').eq(tabIndex).removeClass("li-tabs-unselected").addClass("li-tabs-selected").siblings().removeClass("li-tabs-selected").addClass("li-tabs-unselected");
                }
            },
            /**
             * 修改密码
             */
            checkOldPassIsCorrect: function () {
                $("#indexPageUpdatePassErrorP1").html(" ");
                $("#indexPageUpdatePassErrorP2").html(" ");
                $("#indexPageUpdatePassErrorP3").html(" ");
                $("#indexPageUpdatePassErrorP4").html(" ");
                var oldPassword = $("#indexPageUpdatePassOldPassword").val();
                var password = $("#indexPageUpdatePassPassword").val();
                var againPassword = $("#indexPageUpdatePassPasswordAgainPassword").val();
                var mobile = $("#indexPageUpdatePassPhone").val();
                if (mobile == null || mobile == "") {
                    $("#indexPageUpdatePassErrorP3").html("电话号码不能为空");
                    return;
                } else {
                    var regx = /^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/;
                    if (!regx.test(mobile)) {
                        $("#indexPageUpdatePassErrorP3").html("手机号码格式错误");
                        return;
                    }
                }
                if (oldPassword == null || oldPassword == "") {
                    $("#indexPageUpdatePassErrorP4").html("旧密码不能为空");
                    return;
                }
                if (password == null || password == "") {
                    $("#indexPageUpdatePassErrorP1").html("新密码不能为空");
                    return;
                } else {
                    var numExp = /^[0-9]+$/;
                    var wordExp = /^[a-zA-Z]+$/;
                    if (password.length < 8 || password.length > 18 ||
                        numExp.test(password) || wordExp.test(password)) {
                        $("#indexPageUpdatePassErrorP1").html("密码8-18位，数字和字母的组合");
                        return;
                    }
                }
                if (againPassword == null || againPassword == "") {
                    $("#indexPageUpdatePassErrorP2").html("请再次输入密码");
                    return;
                }
                if (password != againPassword) {
                    $("#indexPageUpdatePassErrorP2").html("两次密码输入不一致");
                    return;
                }

                if (oldPassword == null || oldPassword == "") {
                    $("#indexPageUpdatePassErrorP4").html("旧密码不能为空");
                    return;
                } else {
                    if (oldPassword.length < 4
                        || oldPassword.length > 18) {
                        $("#indexPageUpdatePassErrorP4").html("旧密码长度应为4-18位");
                        return;
                    }
                }

                if (oldPassword == password) {
                    $("#indexPageUpdatePassErrorP4").html("新密码与旧密码一致");
                    return;
                }

                IndexMVC.Controller.savePass();
                return;

                var params = {};
                params.account = IndexMVC.UserInfo.user.account;
                params.password = oldPassword;
                params.method = 'm001_12';
                //验证旧密码是否正确
                $.ajax({
                    url: CommonUrls.getCount.url,
                    type: "post",
                    dataType: "json",
                    data: params,
                    success: function (data) {
                        if (data.respCode != "100") {
                            $("#indexPageUpdatePassErrorP4").html("旧密码输入错误");
                        } else {
                            IndexMVC.Controller.savePass();
                        }
                    },
                    error: function (error) {
                        console.log(JSON.stringify(error));
                        console.log("向服务器发送请求失败");
                    }
                });
            },
            /**
             * 保存密码
             */
            savePass: function () {
                var password = $("#indexPageUpdatePassPassword").val();
                var mobile = $("#indexPageUpdatePassPhone").val();
                var oldPassword = $("#indexPageUpdatePassOldPassword").val();

                var params = {};
                params.account =$("#indexPageUpdatePassUserAccount").val();
                params.id = IndexMVC.UserInfo.user.userId;
                params.password = password;
                params.oldPassword = oldPassword;
                params.phone = phone;
                params.updateUser =IndexMVC.UserInfo.user.userId;
                //params.method = "m001_3";
                $.ajax({
                    url: IndexMVC.URLs.updatePwd.url,
                    type: "post",
                    dataType: "json",
                    data: params,
                    success: function (data) {
                        if(upDatePassZuiMessager != null) upDatePassZuiMessager.destroy();
                        if (data.respCode != "100") {
                            $("#indexPageUpdatePassTipsContent").text(data.respDesc);
                            //common.failMsg(data.respDesc);

                            upDatePassZuiMessager = new $.zui.Messager(data.respDesc, {
                                placement: 'center',
                                type: 'danger',
                                time: 0
                            });
                            upDatePassZuiMessager.show();

                            return
                        }
                        $("#indexPageUpdatePassTipsContent").text("修改密码成功!");
                        $("#indexPageUpdatePass").modal("hide");
                        //$("#indexPageUpdatePassTips").modal("show");
                        $("#indexPageUpdatePassTips").modal({
                            backdrop: 'static'
                        });
                        upDatePassZuiMessager=new $.zui.Messager( "修改密码成功", {
                            placement: 'center',
                            type: 'success',
                            time: 0
                        });
                        upDatePassZuiMessager.show();

                        allUserInfo.mobile=mobile;
                    },
                    error: function (error) {
                        console.log(JSON.stringify(error));
                        console.log("向服务器发送请求失败");
                    }
                });

            },


            openUpdatePassDialog:function () {
                $("#forceUpdatePwdTips").hide();
                $("#passwordCancelBtn").show();
                $("#indexPageUpdatePassPhone").val(user.mobile);
                $("#indexPageUpdatePassOldPassword").val('');
                $("#indexPageUpdatePassPassword").val('');
                $("#indexPageUpdatePassPasswordAgainPassword").val('');


                $('#indexPageUpdatePass').modal({backdrop: 'static'});
            }
        }

    }
;




