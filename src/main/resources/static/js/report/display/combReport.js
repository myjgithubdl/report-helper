$(function () {
    CombReportManage.init();
});


var CombReportManage = {
    init: function () {
        CombReportManageMVC.View.initControl();
        CombReportManageMVC.View.bindEvent();
        CombReportManageMVC.View.bindValidate();
    }
};


var CombReportManageMVC = {
    /**
     *变量
     */
    Variable: {},
    URLs: {},
    View: {
        /**
         * 初始化操作
         */
        initControl: function () {

            CombReportManageMVC.Controller.initCombReportTab();
        },

        /**
         * 绑定事件
         */
        bindEvent: function () {


        },
        bindValidate: function () {

        }
    },
    Controller: {

        initCombReportTab: function () {
            if (combReportMetaColumns && combReportMetaColumns.length > 0) {
                var tabs = [];
                for (var index in combReportMetaColumns) {
                    var report = combReportMetaColumns[index];
                    var name = report.name;
                    var href = report.href;

                    var tab = {
                        title: name,
                        url: href,
                        type: 'iframe',
                        //type: 'ajax',
                        forbidClose: true,
                        defaultTabIcon: false
                    }
                    tabs.push(tab);
                }
                console.log(tabs)
                // 初始化标签页管理器
                $('#combReportTabs').tabs({tabs: tabs});

            }

        }

    },
    Service: {}
}
