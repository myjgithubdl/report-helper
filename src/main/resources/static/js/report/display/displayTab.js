$(function () {
    DisplayTabManage.init();
});


var DisplayTabManage = {
    init: function () {
        DisplayTabManageMVC.Variable.bodyHeight = parseInt($("#displayTabBody").css("height"));

        DisplayTabManageMVC.Controller.initReportTab();
    }
};


var DisplayTabManageMVC = {
    /**
     *变量
     */
    Variable: {
        bodyHeight: 1000,
        reportComposeList:[],
        reportOptions:{},
        reportComposeOptions:{},
    },
    URLs: {},
    View: {},
    Controller: {
        /**
         *
         */
        initReportTab: function () {
            var reportComposeList = reportObj.reportComposeList;
            var options=JSON.parse(reportObj.options);
            DisplayTabManageMVC.Variable.reportOptions=options

            var iframeHeight = DisplayTabManageMVC.Util.getIframeHeight();
            //垂直tab
            if(options.showContent == '103'){
                $("#displayTabs").addClass("row").css({'margin':'0px'});
                $("#nav-tabs-div").addClass("col-xs-2");
                $("#nav-tabs-ul").addClass("nav-stacked");
                $("#tab-content-div").addClass("col-xs-10");
                $("#tab-content").css({'height':iframeHeight+'px'});
            }

            if (reportComposeList) {
                _.each(reportComposeList, function (item, index) {
                    var tabTitle=item.name ? item.name : item.uid;
                    var tabId = DisplayTabManageMVC.Util.getTabId(item.uid);
                    var navActive = '',contentActive = '';
                    if (index == 0) {
                        navActive = ' active ';
                        contentActive = ' active in ';
                    }
                    $("#nav-tabs-ul").append('<li class="' + navActive + '"><a href="javascript:void(0)" report-uid="' + item.uid + '" data-target="#' +tabId + '" data-toggle="tab">' + tabTitle + '</a></li>');
                    $("#tab-content").append('<div class="tab-pane fade ' + contentActive + '" id="' +tabId + '"></div>');

                    if(options.showContent == '103'){
                        $("#"+tabId).css({'height':iframeHeight+'px'});
                    }

                    //为了保证系统性能先初始化第一个tab，后面的tab在打开tab时通过时间监听
                    if (index == 0) {
                        DisplayTabManageMVC.Util.tabAppendIframe(item.uid);
                    }
                })

                //监听tab显示事件，即点击tab时
                $('#displayTabs').on('show.zui.tab', function (e) {
                    var reportComposeUid = $(e.target).attr("report-uid");
                    DisplayTabManageMVC.Util.tabAppendIframe(reportComposeUid);
                });
            }
        }
    },
    Service: {},
    Util: {
        /**
         * 计算Tab选项卡中iframe
         * @returns {number}
         */
        getIframe: function (reportComposeUid) {
            var iframeHeight = DisplayTabManageMVC.Util.getIframeHeight();
            var iframeId = DisplayTabManageMVC.Util.getIframeId(reportComposeUid);
            var src = DisplayTabManageMVC.Util.getIframeSrc(reportComposeUid);
            console.log("tab iframe height:"+iframeHeight)
            var iframe = '<iframe id="' + iframeId + '" class="br-none wp100" src="' + src + '" style="height: ' + iframeHeight + 'px"></iframe>';
            return iframe;
        },
        getIframeSrc: function (reportComposeUid) {
            var host = window.location.host;
            if (host.substring(0, 4) != "http") {
                host = "http://" + host;
            }
            var url = host + ReportHelper.ctxPath + '/report/preview/uid/' + reportObj.uid + "?queryReportComposeUid=" + reportComposeUid;
            return url;
        },
        /**
         * 计算Tab选项卡中iframe的高度
         * @returns {number}
         */
        getIframeHeight: function () {
            var iframeHeight = DisplayTabManageMVC.Variable.bodyHeight;
            var options=DisplayTabManageMVC.Variable.reportOptions;
            //垂直tab 不用减去tab的高度
            if(options.showContent == '103'){
                iframeHeight=iframeHeight-10;
            }else{
                iframeHeight=iframeHeight-  parseInt($("#nav-tabs-ul").css("height")) - 10;
            }
            return iframeHeight;
        },
        /**
         * 获取Tab中iframe的id
         * @param reportComposeId
         * @returns {string}
         */
        getTabId: function (reportComposeUid) {
            return "tab-" + reportComposeUid;
        },
        /**
         * 获取Tab中iframe的id
         * @param reportComposeId
         * @returns {string}
         */
        getIframeId: function (reportComposeUid) {
            return "iframe-" + reportComposeUid;
        },
        /**
         * 在tab中追加iframe标签
         * @param reportComposeUid
         */
        tabAppendIframe:function (reportComposeUid) {
            if(reportComposeUid){
                var iframeId = DisplayTabManageMVC.Util.getIframeId(reportComposeUid);
                if ($("#" + iframeId).size() == 0) {
                    var tabId = DisplayTabManageMVC.Util.getTabId(reportComposeUid);
                    var iframe = DisplayTabManageMVC.Util.getIframe(reportComposeUid);
                    $("#"+tabId).append(iframe);
                }
            }
        }
    }
}
