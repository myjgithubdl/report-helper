/**
 * Created by bdf on 2019/10/10.
 */

var reportDefaultTabId = 'basic-312b';//新建或修改报表时默认的tabId

$(function () {
    ReportDesigner.init();
});

var ReportDesigner = {
    /**
     * 初始化方法
     */
    init: function () {
        ReportDesigner.initReportCommonQueryParam();
        ReportDesigner.initReportShowContentTabs();

        //加载数据库链接
        ReportDesignerMVC.Data.loadDataSourceList();
        //加载报表分类
        ReportDesignerMVC.Data.loadCategoryList();

        ReportDesigner.initReportDatagrid();
        ReportDesigner.initReportAddOrEditDlg();

        $('#btn-report-search').bind('click', ReportDesignerMVC.Controller.find);

    },

    /**
     * 实例化报表公共查询参数
     */
    initReportCommonQueryParam: function () {
        $("#report-common-query-param").append($("#report-set-template").find(".report-query-param").clone());
        var tabContentId = ReportDesignerMVC.Util.getTabContentId(reportDefaultTabId);
        //设置报表参数GridID并实例化grid
        $("#" + tabContentId).find(".report-query-param-grid-table").attr("id", ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId));
        $("#" + tabContentId).find(".report-query-param-form").attr("id", ReportDesignerMVC.Util.getReportQueryParamFormId(reportDefaultTabId));

        ReportDesigner.initQueryParamDatagrid(reportDefaultTabId);

        ReportDesignerMVC.Controller.setReportTabBtnClick(reportDefaultTabId);

    },
    /**
     * 初始化报表显示tab
     */
    initReportShowContentTabs: function () {
        $("#reportShowContentTabs").tabs({
            /*onBeforeClose:function(title,index,a){
             console.log(title,index,a)
             return confirm('确定删除吗？');
             },*/
            onBeforeClose: function (title, index) {
                return;
                var target = this;
                $.messager.confirm('确认', '确定删除吗？', function (r) {
                    if (r) {
                        var opts = $(target).tabs('options');
                        var bc = opts.onBeforeClose;
                        opts.onBeforeClose = function () {
                        };
                        $(target).tabs('close', index);
                        opts.onBeforeClose = bc;
                    }
                });
                return false;
            },
            onClose: function (title, index) {
                ReportDesignerMVC.Controller.refreshReportTabIds();
            }
        });
    },
    /**
     * 实例化指定tab下面的元数据列treegrid
     * @param tabId
     */
    initMetaColumnTreegrid: function (tabId) {
        var treegridId = ReportDesignerMVC.Util.getMetaColumnGridId(tabId);
        $('#' + treegridId).treegrid({
            method: 'post',
            idField: 'id',
            treeField: 'dataType',
            animate: false,
            state: "open",
            singleSelect: true,
            autoRowHeight: true,
            tools: [{
                iconCls: 'icon-up',
                handler: function () {
                    //EasyUIUtils.move("#report-meta-column-grid", 'up');
                    ReportDesignerMVC.Util.updateTreegridSelectRow('#' + treegridId);
                    EasyUIUtils.reSortTreegridSelectedRow('#' + treegridId, 'up');
                }
            }, '-', {
                iconCls: 'icon-down',
                handler: function () {
                    //EasyUIUtils.move("#report-meta-column-grid", 'down');
                    ReportDesignerMVC.Util.updateTreegridSelectRow('#' + treegridId);
                    EasyUIUtils.reSortTreegridSelectedRow('#' + treegridId, 'down');
                }
            }, '-', {
                iconCls: 'icon-add',
                handler: function () {
                    $.getJSON(ReportDesignerMVC.URLs.getMetaColumnScheme.url, function (result) {
                        if (result.respCode=='100') {
                            var id = CommonUtils.getUUID(2);
                            var row = result.respData;
                            row.name = row.name;
                            row.text = row.name;
                            row.id = id;
                            var dataArrar = [];
                            //$('#report-meta-column-grid').datagrid('appendRow', row);
                            var treegridIdObj = $('#' + treegridId);
                            var selectedNode = treegridIdObj.treegrid('getSelected');
                            if (selectedNode) {
                                row.pid = selectedNode.id;
                                dataArrar.push(row);
                                treegridIdObj.treegrid('append', {
                                    parent: selectedNode.id,//treegrid 父id 必须指定
                                    data: dataArrar
                                });

                            } else {//增加根节点
                                dataArrar.push(row);
                                treegridIdObj.treegrid('append', {
                                    data: dataArrar
                                });
                            }
                            treegridIdObj.treegrid('refresh', id);
                        }
                    });
                }
            }, '-', {
                iconCls: 'icon-cancel',
                handler: function () {
                    EasyUIUtils.removeTreegridSelectedRow('#' + treegridId);
                    return;
                    var row = $("#report-meta-column-grid").datagrid('getSelected');
                    if (row) {
                        var index = $("#report-meta-column-grid").datagrid('getRowIndex', row);
                        $("#report-meta-column-grid").datagrid('deleteRow', index);
                        var rows = $("#report-meta-column-grid").datagrid('getRows');
                        $("#report-meta-column-grid").datagrid('loadData', rows);
                    }
                }
            }, '-', {
                iconCls: '',
                handler: function () {

                }
            }],
            frozenColumns: [[
                {field: 'id', title: 'Id', checkbox: true, width: 180},
                {
                    field: 'dataType',
                    title: '主键',
                    width: 125,
                    formatter: function (value, row, index) {
                        return row.id;
                    }
                },
                {
                    field: 'name',
                    title: '列名',
                    width: 130,
                    formatter: function (value, row, index) {
                        var id = "name" + row.id;
                        var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="name" value="${value}" />';
                        return juicer(tmpl, {
                            id: id,
                            value: row.name
                        });
                    }
                }, {
                    field: 'text',
                    title: '标题',
                    width: 130,
                    height: 50,
                    formatter: function (value, row, index) {
                        var id = "text" + row.id;
                        var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="text" value="${value}" />';
                        return juicer(tmpl, {
                            id: id,
                            value: row.text
                        });
                    }
                },
            ]],
            columns: [[{
                field: 'type',
                title: '类型',
                width: 80,
                formatter: function (value, row, index) {
                    var id = "type" + row.id;
                    var tmpl =
                        '<select id="${id}" name=\"type\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.MetaColumnTypes
                    });
                }
            }, {
                field: 'width',
                title: '宽度',
                width: 80,
                formatter: function (value, row, index) {
                    var id = "width" + row.id;
                    if (!row.width) {
                        row.width = '120';
                    }
                    var tmpl = '<input style="width:98%;height: 20px;" onkeyup="CommonUtils.checkInteger(this,3);" type="text" id="${id}" name="width" value="${value}" />';
                    return juicer(tmpl, {
                        id: id,
                        value: row.width
                    });
                }
            }, {
                field: 'decimals',
                title: '精度',
                width: 80,
                hidden: true,
                formatter: function (value, row, index) {
                    var id = "decimals" + row.id;
                    if (!row.decimals) {
                        row.decimals = 0;
                    }
                    var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="decimals" value="${value}" />';
                    return juicer(tmpl, {
                        id: id,
                        value: row.decimals
                    });
                }
            }, {
                field: 'showDecimals',
                title: '显示精度',
                width: 60,
                formatter: function (value, row, index) {
                    var id = "showDecimals" + row.id;
                    if (!row.showDecimals) {
                        row.showDecimals = '';
                    }
                    var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="showDecimals" value="${value}" onkeyup="CommonUtils.checkInteger(this,1);" />';
                    return juicer(tmpl, {
                        id: id,
                        value: row.showDecimals
                    });
                }
            }, {
                field: 'exportDecimals',
                title: '导出精度',
                width: 60,
                formatter: function (value, row, index) {
                    var id = "exportDecimals" + row.id;
                    if (!row.exportDecimals) {
                        row.exportDecimals = '';
                    }
                    var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="exportDecimals" value="${value}" onkeyup="CommonUtils.checkInteger(this,1);" />';
                    return juicer(tmpl, {
                        id: id,
                        value: row.exportDecimals
                    });
                }
            }, {
                field: 'sortType',
                title: '排序类型',
                width: 80,
                formatter: function (value, row, index) {
                    var id = "sortType" + row.id;
                    var tmpl =
                        '<select id="${id}" name=\"sortType\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.MetaColumnSortTypes
                    });
                }
            }, {//缪应江 add
                field: 'href',
                title: '链接',
                width: 80,
                formatter: function (value, row, index) {
                    var id = "href" + row.id;
                    if (row.href == null || row.href == 'null' || row.href == 'undefined') {
                        row.href = '';
                    }
                    var tmpl = '<input style="width:98%;height: 20px;" type="text" id="${id}" name="href" value="${value}" />';
                    return juicer(tmpl, {
                        id: id,
                        value: row.href
                    });
                }
            }, {//缪应江 add  HerfTarget
                field: 'hrefTarget',
                title: '链接打开方式',
                width: 100,
                formatter: function (value, row, index) {//HerfTarget
                    var id = "hrefTarget" + row.id;
                    var tmpl =
                        '<select id="${id}" name=\"hrefTarget\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.HerfTargetList
                    });
                }
            }, {//缪应江 add  hidden
                field: 'hidden',
                title: '是否显示列',
                width: 80,
                formatter: function (value, row, index) {//HerfTarget
                    var id = "hidden" + row.id;
                    var tmpl =
                        '<select id="${id}" name=\"hidden\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.DisplayStyleList
                    });
                }
            }, {//缪应江 add  DownMergeCells
                field: 'downMergeCells',
                title: '合并等值列',
                width: 80,
                formatter: function (value, row, index) {//HerfTarget
                    var id = "downMergeCells" + row.id;
                    var tmpl =
                        '<select id="${id}" name=\"downMergeCells\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.DownMergeCells
                    });
                }
            }, {//缪应江 add  theadTextAlign
                field: 'theadTextAlign',
                title: '对齐方式',
                width: 80,
                formatter: function (value, row, index) {//theadTextAlign
                    var id = "theadTextAlign" + row.id;
                    if (!value) {
                        value = "left";
                        var dataType = row.dataType.toLocaleLowerCase();
                        if (row.dataType && (dataType.indexOf("int") != -1
                            || dataType.indexOf("numeric") != -1
                            || dataType.indexOf("number") != -1
                            || dataType.indexOf("double") != -1
                            || dataType.indexOf("float") != -1)) {
                            value = "right";
                        }
                    }
                    var tmpl =
                        '<select id="${id}" name=\"theadTextAlign\" style="width:98%;height: 24px;">' +
                        '{@each list as item}' +
                        '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                        '{@/each}' +
                        '</select>';
                    return juicer(tmpl, {
                        id: id,
                        currValue: value,
                        list: ReportDesignerMVC.Model.TextAlignList
                    });
                }
            }]],
            onClickRow: function (row) {
                if (ReportDesignerMVC.Model.metaDataGridCheckedRow.id == row.id) {//ID相同 之前选中的取消选中  未选中则设置选中
                    if (ReportDesignerMVC.Model.metaDataGridCheckedRow.state == "select") {
                        $("#report-meta-column-grid").treegrid("unselect", row.id);
                        ReportDesignerMVC.Model.metaDataGridCheckedRow.state = "unselect";
                    } else {
                        $("#report-meta-column-grid").treegrid("select", row.id);
                        ReportDesignerMVC.Model.metaDataGridCheckedRow.state = "select";
                    }
                } else {
                    ReportDesignerMVC.Model.metaDataGridCheckedRow.id = row.id;
                    ReportDesignerMVC.Model.metaDataGridCheckedRow.state = "select";
                }
            },
            onContextMenu: function (e, row) {
                e.preventDefault();
            }
        });
    },

    /**
     * 实例化tab下面的报表查询参数grid
     * @param tabId
     */
    initQueryParamDatagrid: function (tabId) {
        var datagridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);
        $('#' + datagridId).datagrid({
            method: 'get',
            //fit: true,
            singleSelect: true,
            rownumbers: true,
            tools: [{
                iconCls: 'icon-up',
                handler: function () {
                    EasyUIUtils.move('#' + datagridId, 'up');
                }
            }, '-', {
                iconCls: 'icon-down',
                handler: function () {
                    EasyUIUtils.move('#' + datagridId, 'down');
                }
            }, '-', {
                iconCls: '',
                handler: function () {

                }
            }],
            frozenColumns: [[{
                field: 'text',
                title: '标题',
                width: 100
            }, {
                field: 'name',
                title: '参数名',
                width: 100
            }
            ]],
            columns: [[{
                field: 'defaultValue',
                title: '默认值',
                width: 100
            }, {
                field: 'defaultText',
                title: '默认标题',
                width: 100
            }, {
                field: 'textWidth',
                title: '标题宽度',
                width: 100
            }, {
                field: 'nameWidth',
                title: '输入框宽度',
                width: 100
            }, {
                field: 'formElement',
                title: '表单控件',
                width: 80,
                formatter: function (value, row, index) {
                    if (value == "text") {
                        return "文本框";
                    } else if (value == "select") {
                        return "下拉单选";
                    } else if (value == "selectMul") {
                        return "下拉多选";
                    } else if (value == "date") {
                        return "日期";
                    }
                    return "错误";
                }
            }, {
                field: 'dataType',
                title: '数据类型',
                width: 80,
                formatter: function (value, row, index) {
                    if (value == "string") {
                        return "字符串";
                    } else if (value == "float") {
                        return "浮点数";
                    } else if (value == "integer") {
                        return "整数";
                    } else if (value == "date") {
                        return "日期";
                    }
                    return "错误";
                }
            }, {
                field: 'required',
                title: '是否必选',
                width: 70,
                formatter: function (value, row, index) {
                    if (value == "1") {
                        return "是";
                    }
                    return "否";
                }
            }, {
                field: 'dataSource',
                title: '值来源',
                width: 80,
                formatter: function (value, row, index) {
                    if (value == "sql") {
                        return "SQL语句";
                    }
                    if (value == "text") {
                        return "文本字符串";
                    }
                    return "无内容";
                }
            }, {
                field: 'content',
                title: '值内容',
                width: 80
            }, /* {
             field: 'width',
             title: '数据长度',
             width: 80
             }, */{
                field: 'dateFormat',
                title: '日期格式',
                width: 80
            }, {
                field: 'dataRange',
                title: '日期加减',
                width: 70
            }, {
                field: 'triggerParamName',
                title: '联动参数名',
                width: 100
            }, {
                field: 'options',
                title: '操作',
                width: 50,
                formatter: function (value, row, index) {
                    var imgPath = DesignerCommon.baseIconUrl + 'remove.png';
                    var tmpl = '<a href="#" title ="删除" ' +
                        'onclick="ReportDesignerMVC.Controller.deleteReportQueryParam(\'' + datagridId + '\',\'${index}\')"><img src="${imgPath}" ' +
                        'alt="删除"/"></a>';
                    return juicer(tmpl, {
                        index: index,
                        imgPath: imgPath
                    });
                }
            }]],
            onDblClickRow: function (index, row) {
                //双击单数行将行的数据填充到参数表单做修改
                var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
                var queryFormId = ReportDesignerMVC.Util.getReportQueryParamFormId(tabId);
                var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);

                if (row) {
                    for (var name in row) {
                        if ($("#" + queryFormId).find("input[name='" + name + "']").size() == 1) {
                            $("#" + queryFormId).find("input[name='" + name + "']").val(row[name]);
                        } else {
                            $("#" + queryFormId).find("select[name='" + name + "']").val(row[name]);
                        }
                    }
                }
                var inputId = '#' + tabContentId + ' .query-param-grid-update-row-index';
                $(inputId).val(index);
            }
        });
    },

    /**
     * 实例化报表列表
     */
    initReportDatagrid: function () {
        $('#report-datagrid').datagrid({
            url: ReportDesignerMVC.URLs.list.url,
            method: 'post',
            pageSize: 50,
            fit: true,
            pagination: true,
            rownumbers: true,
            fitColumns: true,
            singleSelect: true,
            sortName: 'sequence',
            sortOrder: 'asc',
            queryParams: {ctlVersion: 2},
            toolbar: [/*{
             text: '详细信息',
             iconCls: 'icon-info',
             handler: function () {
             ReportDesignerMVC.Controller.showDetail();
             }
             }, '-',*/ {
                text: '增加',
                iconCls: 'icon-add',
                handler: ReportDesignerMVC.Controller.add
            }, '-', {
                text: '组合',
                iconCls: 'icon-add',
                handler: ReportDesignerMVC.Controller.addMultipleReport
            }, '-', {
                text: '修改',
                iconCls: 'icon-edit1',
                handler: ReportDesignerMVC.Controller.edit
            }, /* '-', {
             text: '移动',
             iconCls: 'icon-redo',
             handler: ReportDesignerMVC.Controller.moveToCategory
             },*/ '-', {
                text: '复制',
                iconCls: 'icon-copy',
                handler: ReportDesignerMVC.Controller.copy
            }, '-', {
                text: '预览',
                iconCls: 'icon-preview',
                handler: ReportDesignerMVC.Controller.preview
            }, /*'-', {
             text: '版本',
             iconCls: 'icon-history',
             handler: ReportDesignerMVC.Controller.showHistorySql
             }, '-', */{
                text: '删除',
                iconCls: 'icon-remove',
                handler: ReportDesignerMVC.Controller.remove
            }],
            loadFilter: function (src) {
                if (src.respCode == '100') {
                    return src.respData;
                }
                $.messager.alert('失败', src.respDesc, 'error');
                return EasyUIUtils.getEmptyDatagridRows();
            },
            columns: [[{
                field: 'id',
                title: 'ID',
                width: 50,
                sortable: true
            }, {
                field: 'name',
                title: '名称',
                width: 150,
                sortable: true
            }, {
                field: 'status',
                title: '状态',
                width: 40,
                sortable: true,
                formatter: function (value, row, index) {
                    return value == 1 ? "启用" : "禁用";
                }
            }, {
                field: 'categoryName',
                title: '分类',
                width: 100,
                sortable: true
            }, {
                field: 'updateUserName',
                title: '修改人',
                width: 100,
                sortable: true
            }, /* {
             field: 'comment',
             title: '说明',
             width: 100,
             sortable: true
             },*/ {
                field: 'updateDate',
                title: '修改时间',
                width: 80,
                sortable: true
            }, {
                field: 'options',
                title: '操作',
                hidden: true,
                width: 120,
                formatter: function (value, row, index) {
                    var icons = [/*{
                     "name": "info",
                     "title": "详细信息"
                     },*/ {
                        "name": "edit",
                        "title": "编辑"
                    }, /* {
                     "name": "copy",
                     "title": "复制"
                     },*/ {
                        "name": "preview",
                        "title": "预览"
                    }, /* {
                     "name": "item1",
                     "title": "发布"
                     }, {
                     "name": "history",
                     "title": "版本"
                     },*/ {
                        "name": "remove",
                        "title": "删除"
                    }];
                    var buttons = [];
                    for (var i = 0; i < icons.length; i++) {
                        var tmpl = '<a href="#" title ="${title}" ' +
                            'onclick="ReportDesignerMVC.Controller.doOption(\'${index}\',\'${name}\')">' +
                            '<img src="${imgSrc}" alt="${title}"/"></a>';
                        var data = {
                            title: icons[i].title,
                            name: icons[i].name,
                            index: index,
                            imgSrc: DesignerCommon.baseIconUrl + icons[i].name + ".png"
                        };
                        buttons.push(juicer(tmpl, data));
                    }
                    return buttons.join(' ');
                }
            }]],
            onDblClickRow: function (rowIndex, rowData) {
                return ReportDesignerMVC.Controller.preview();
            },
            onRowContextMenu: function (e, index, row) {
            },
            loadFilter: function (src) {
                if (src.respCode == '100') {
                    console.log(src.respData)
                    return src.respData;
                }
                $.messager.alert('失败', src.respDesc, 'error');
                return EasyUIUtils.getEmptyDatagridRows();
            }
        });
    },

    /**
     * 实例化报表新增或编辑对话框
     */
    initReportAddOrEditDlg: function () {
        $('#report-designer-dlg').dialog({
            closed: true,
            modal: false,
            width: window.screen.width - 350,
            height: window.screen.height - 350,
            //maximizable: true,
            //minimizable: true,
            maximized: true,
            iconCls: 'icon-designer',
            buttons: [{
                text: '关闭',
                iconCls: 'icon-no',
                handler: function () {
                    $("#report-designer-dlg").dialog('close');
                }
            }, {
                text: '保存',
                iconCls: 'icon-save',
                handler: ReportDesignerMVC.Controller.saveReport
            }],
            onClose: function () {
                ReportDesignerMVC.Controller.resetAddOrEditDlgVal()
            }
        });
    }

}

var DesignerCommon = {
    baseUrl: ReportHelper.ctxPath + '/rest/report/designer/',
    baseHistoryUrl: ReportHelper.ctxPath + '/rest/report/history/',
    baseDsUrl: ReportHelper.ctxPath + '/rest/report/ds/',
    baseCategoryUrl: ReportHelper.ctxPath + '/rest/report/category/',
    baseIconUrl: ReportHelper.ctxPath + '/vendor/easyui-1.7.0/custom/themes/icons/',
    baseReportUrl: ReportHelper.ctxPath + '/report/'
};

var ReportDesignerMVC = {
    /**
     * 变量
     */
    Variables: {
        reportTabIds: [],//组成报表tabID的集合
        dbDataSourceIds: [],//系统设置的数据库链接is
        reportTabKindEditor: {},//组成报表tab下的报表说明富文本实例，可以为 explain-tabId，value为KindEditor实例化对象
        Categorys: [],//所有的报表分类
    },
    URLs: {
        add: {
            url: DesignerCommon.baseUrl + 'add',
            method: 'POST'
        },
        edit: {
            url: DesignerCommon.baseUrl + 'edit',
            method: 'POST'
        },
        list: {
            url: DesignerCommon.baseUrl + 'list',
            method: 'GET'
        },
        find: {
            url: DesignerCommon.baseUrl + 'find',
            method: 'GET'
        },
        remove: {
            url: DesignerCommon.baseUrl + 'remove',
            method: 'POST'
        },
        historyList: {
            url: DesignerCommon.baseHistoryUrl + 'list',
            method: 'GET'
        },
        execSqlText: {
            url: DesignerCommon.baseUrl + 'execSqlText',
            method: 'POST'
        },
        previewSqlText: {
            url: DesignerCommon.baseUrl + 'previewSqlText',
            method: 'POST'
        },
        getMetaColumnScheme: {
            url: DesignerCommon.baseUrl + 'getMetaColumnScheme',
            method: 'GET'
        },
        DataSource: {
            listAll: {
                url: DesignerCommon.baseDsUrl + 'listAll',
                method: 'GET'
            }
        },
        Report: {
            url: DesignerCommon.baseReportUrl + 'uid/',
            method: 'GET'
        },
        updateCategory: {
            url: DesignerCommon.baseUrl + 'updateCategory',
            method: 'POST'
        },
    },
    Model: {
        MetaColumnOptions: [{
            name: "optional",
            text: "可选",
            type: 1
        }, {
            name: "percent",
            text: "百分比",
            type: 1
        }, {
            name: "displayInMail",
            text: "邮件显示",
            type: 1
        }, /*{
         name : "footings",
         text : "合计",
         type : 1
         }, {
         name : "extensions",
         text : "小计",
         type : 3
         },*/ {
            name: "expression",
            text: "表达式",
            type: 4
        }, {
            name: "comment",
            text: "备注",
            type: 2
        }
            /*, {
             name: "format",
             text: "格式",
             type: 2
             }*/],
        MetaColumnTypes: [{
            text: "普通列",
            value: 0
        }, {
            text: "布局列",
            value: 1
        }, {
            text: "维度列",
            value: 2
        }, {
            text: "统计列",
            value: 3
        }/*, {
         text: "计算列",
         value: 4
         }*/],
        MetaColumnSortTypes: [{
            text: "默认",
            value: 0
        }, {
            text: "数字优先升序",
            value: 1
        }, {
            text: "数字优先降序",
            value: 2
        }, {
            text: "字符优先升序",
            value: 3
        }, {
            text: "字符优先降序",
            value: 4
        }],
        DataSourceList: [],
        HerfTargetList: [{
            text: "在当前窗口中打开",//默认
            value: "_self"
        }, {
            text: "在新窗口中打开",
            value: "_blank"
        }, {
            text: "父框架集中打开",
            value: "_parent"
        }, {
            text: "在整个窗口中打开",
            value: "_top"
        }],
        DisplayStyleList: [{
            text: "显示",//默认
            value: "false"
        }, {
            text: "隐藏",
            value: "true"
        }],
        DownMergeCells: [{
            text: "不合并",//默认
            value: "false"
        }, {
            text: "合并",
            value: "true"
        }],
        TextAlignList: [{
            text: "居中",//默认
            value: "center"
        }, {
            text: "居左",
            value: "left"
        }, {
            text: "居右",
            value: "right"
        }],
        metaDataGridCheckedRow: {//元数据列是选中的行
            id: null,//操作行的ID
            state: null//状态为选中和不选中  select   和  unselect
        }
    },
    View: {},
    Controller: {
        /**
         * 增加报表展示内容
         */
        addReportTab: function (tabId) {
            var uuid = tabId;
            if (!tabId) {
                uuid = CommonUtils.getUUID(1);
            }
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(uuid);
            $("#reportShowContentTabs").tabs('add', {
                title: '报表-' + uuid,
                //selected: true,
                closable: true,
                id: 'tab-' + uuid,
                content: '<div id="' + tabContentId + '"></div>',
            });
            ReportDesignerMVC.Controller.addReportTabContent(uuid);
            ReportDesignerMVC.Controller.refreshReportTabIds();
        },
        /**
         * 删除所有报表tab
         */
        removeReportTab: function () {
            var tabs = $("#reportShowContentTabs").tabs('tabs');
            if (tabs.length > 1) {
                for (var i = tabs.length - 1; i > 0; i--) {
                    $("#reportShowContentTabs").tabs('close', i);
                }
                //使用该方式也可以
                //$("#reportShowContentTabs").tabs('close', 1);
                //ReportDesignerMVC.Controller.removeReportTab();
            }
        },
        /**
         * 刷新报表ID集合
         */
        refreshReportTabIds: function () {
            var tabs = $("#reportShowContentTabs").tabs('tabs');
            ReportDesignerMVC.Variables.reportTabIds = [];
            if (tabs.length > 1) {
                for (var i = 1, j = tabs.length; i < j; i++) {
                    var panelOptions = $(tabs[i]).panel('options');
                    //console.log(panelOptions)
                    var id = panelOptions.id;
                    ReportDesignerMVC.Variables.reportTabIds.push(id);
                }
            }
            console.log(ReportDesignerMVC.Variables.reportTabIds)
        },
        /**
         * 在指定tab下增加报表配置内容
         * @param tabId
         */
        addReportTabContent: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            $("#" + tabContentId).append($("#report-set-template").children().clone());

            //设置基本属性表单id
            $("#" + tabContentId).find(".report-basic-attr-form").attr("id", ReportDesignerMVC.Util.getReportBasicAttrFormId(tabId));

            //设置SQL解析出的元数据列GridID并实例化grid
            $("#" + tabContentId).find(".report-meta-column-grid-table").attr("id", ReportDesignerMVC.Util.getMetaColumnGridId(tabId));
            ReportDesigner.initMetaColumnTreegrid(tabId);

            //设置报表参数GridID并实例化grid
            $("#" + tabContentId).find(".report-query-param-grid-table").attr("id", ReportDesignerMVC.Util.getReportQueryParamGridId(tabId));
            $("#" + tabContentId).find(".report-query-param-form").attr("id", ReportDesignerMVC.Util.getReportQueryParamFormId(tabId));

            ReportDesigner.initQueryParamDatagrid(tabId);

            //处理报表说明富文本编辑器
            $("#" + tabContentId).find(".report-explain-textarea").attr("id", ReportDesignerMVC.Util.getReportExplainTextareaId(tabId));
            ReportDesignerMVC.Util.initKindEditor(tabId);

            //设置点击事件搜索
            ReportDesignerMVC.Controller.setReportTabBtnClick(tabId);

        },
        /**
         * 设置报表tab上的点击事件
         * @param tabId
         */
        setReportTabBtnClick: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            //设置增加参数和修改参数按钮的点击事件
            $("#" + tabContentId).find(".query-param-add-btn").attr("onclick", 'ReportDesignerMVC.Controller.addReportQueryParam(\'' + tabId + '\')')
            $("#" + tabContentId).find(".query-param-update-btn").attr("onclick", 'ReportDesignerMVC.Controller.updateReportQueryParam(\'' + tabId + '\')')

            //设置执行SQL按钮的点击事件
            $("#" + tabContentId).find(".execute-sql-btn").attr("onclick", 'ReportDesignerMVC.Controller.executeSql(\'' + tabId + '\')')

        },
        /**
         * 删除datagridId下指定的行
         * @param datagridId
         * @param index
         */
        deleteReportQueryParam: function (datagridId, index) {
            $("#" + datagridId).datagrid('deleteRow', index);
            $("#" + datagridId).datagrid('loadData', $("#" + datagridId).datagrid('getRows'));
        },
        /**
         * 在指定的tab下增加报表参数
         * @param tabId
         */
        addReportQueryParam: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var queryFormId = ReportDesignerMVC.Util.getReportQueryParamFormId(tabId);
            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);
            var formData = ReportDesignerMVC.Controller.getReportQueryParam(tabId)
            $('#' + qeryParamGridId).datagrid('appendRow', formData);
        },
        /**
         * 修改指定的tab下的参数
         * @param tabId
         */
        updateReportQueryParam: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var queryFormId = ReportDesignerMVC.Util.getReportQueryParamFormId(tabId);
            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);
            var formData = ReportDesignerMVC.Controller.getReportQueryParam(tabId)
            var inputId = '#' + tabContentId + ' .query-param-grid-update-row-index';
            var updateRowIndex = $(inputId).val();
            if (updateRowIndex && updateRowIndex >= 0) {
                $('#' + qeryParamGridId).datagrid('updateRow', {
                    index: updateRowIndex,
                    row: formData
                });
            }

        },
        /**
         * 获取指定报表Tab下设置的参数
         * @param tabId
         * @returns {jQuery}
         */
        getReportQueryParam: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var queryFormId = ReportDesignerMVC.Util.getReportQueryParamFormId(tabId);
            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);
            var formData = $("#" + queryFormId).serializeObject()
            //检验参数
            if ($.trim(formData['text']) == '') {
                ReportDesignerMVC.Util.alterAndThrow("参数【标题】为空！", true);
            }
            if ($.trim(formData['name']) == '') {
                ReportDesignerMVC.Util.alterAndThrow("参数【参数名】为空！", true);
            }

            //校验表单控件
            if (formData['formElement'] == 'select' || formData['formElement'] == 'selectMul') {
                //值来源 只能为 SQL语句、文本字符串
                if (!(formData['dataSource'] == 'sql' || formData['dataSource'] == 'text')) {
                    ReportDesignerMVC.Util.alterAndThrow("表单控件为选择框时值来源只能为SQL语句或文本字符串！", true);
                }
                //值内容 不能为空
                if ($.trim(formData['content']) == '') {
                    ReportDesignerMVC.Util.alterAndThrow("参数【值内容】为空！", true);
                }
            } else if (formData['formElement'] == 'date') {
                //dataRange 日期加减 只能为整数
                if ($.trim(formData['dataRange']) != '' && !CommonUtils.isInteger(formData['dataRange'])) {
                    ReportDesignerMVC.Util.alterAndThrow("参数【日期加减】只能为整数！", true);
                }
            }
            return formData;
        },
        /**
         * 设置报表的数据源选择框option标签
         */
        setReportDataSourceOption: function () {
            if (ReportDesignerMVC.Variables.dbDataSourceIds) {
                var commonDsSelectObj = $("#report-attr-" + reportDefaultTabId + " select[name='dsId']");
                var selectObj = $("#report-set-template select[name='dsId']");
                commonDsSelectObj.empty();
                selectObj.empty();
                for (var index in ReportDesignerMVC.Variables.dbDataSourceIds) {
                    var ds = ReportDesignerMVC.Variables.dbDataSourceIds[index];
                    var option = '<option value="' + ds.id + '">' + ds.name + '</option>';
                    commonDsSelectObj.append(option);
                    selectObj.append(option);
                }
            }
        },
        /**
         * 设置报表分类选项
         */
        setReportCategoryOption: function () {
            if (ReportDesignerMVC.Variables.Categorys) {
                var basicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);
                var selectObj = $("#" + basicAttrFormId + " select[name='categoryId']");
                selectObj.empty();
                for (var index in ReportDesignerMVC.Variables.Categorys) {
                    var category = ReportDesignerMVC.Variables.Categorys[index];
                    var option = '<option value="' + category.id + '">' + category.name + '</option>';
                    selectObj.append(option);
                }
            }
        },
        /**
         * 执行报表组成指定tab下的SQL
         * @param tabId
         */
        executeSql: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var metaColumnGridId = ReportDesignerMVC.Util.getMetaColumnGridId(tabId);

            var sqlText = ReportDesignerMVC.Util.getReportQuerySQL(tabId);

            var dsSelect = "#" + tabContentId + " select[name='dsId']"
            var dsId = $(dsSelect).val();
            if (!dsId) {
                $.messager.alert("错误", "数据源参数为空！", "error");
                return;
            }

            ReportDesignerMVC.Util.showMessagerProgress("请稍后...", "正在执行SQL...")

            $.post(ReportDesignerMVC.URLs.execSqlText.url, {
                sqlText: sqlText,
                dsId: dsId,
                //queryParams: ReportDesignerMVC.Util.getQueryParams()
                queryParams: null
            }, function (result) {
                ReportDesignerMVC.Util.closeMessagerProgress();
                if (result.respCode == '100') {
                    $("#" + metaColumnGridId).treegrid('clearChecked');
                    var columns = ReportDesignerMVC.Util.eachMetaColumns(result.respData);
                    return ReportDesignerMVC.Util.loadMetaColumns(tabId, columns);
                }
                return $.messager.alert('错误', "执行SQL错误，请检查SQL语句", "error");
            }, 'json');
        },
        /**
         * 获取报表保存数据
         * @returns {{}}
         */
        getReportSaveData: function () {
            var data = {};//
            //查询报表基本信息、公用参数部分

            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId);
            var reportBasicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);
            //报表基本的名称
            var formData = $("#" + reportBasicAttrFormId).serializeObject()
            if ($.trim(formData['name']) == '') {
                ReportDesignerMVC.Util.alterAndThrow("参数【报表名称】为空！", true);
            }
            if ($.trim(formData['categoryId']) == '') {
                ReportDesignerMVC.Util.alterAndThrow("参数【报表分类】为空！", true);
            }
            for (var key in formData) {
                data[key] = formData[key];
            }

            //查询公共参数datagrid列表字符串
            var commonQueryParams = ReportDesignerMVC.Util.getQueryParamsGridData(reportDefaultTabId);
            data.queryParams = commonQueryParams;

            //ReportDesignerMVC.Util.updateTreegridSelectRow('#report-meta-column-grid');
            //var rows = EasyUIUtils.getTreegridRows('#report-meta-column-grid');
            //var metaColumns = ReportDesignerMVC.Util.getMetaColumns(rows);


            var tabs = $("#reportShowContentTabs").tabs('tabs');
            if (tabs.length > 1) {
                for (var i = 1, j = tabs.length; i < j; i++) {
                    var panelOptions = $(tabs[i]).panel('options');
                    var tabId = panelOptions.id.substring(4);//因为tab id是以 tab- 开头
                    var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
                    var tabBasicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(tabId)
                    var metaColumnGridId = ReportDesignerMVC.Util.getMetaColumnGridId(tabId);
                    var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);

                    var tabBasicAttrFormData = $("#" + tabBasicAttrFormId).serializeObject();//报表组成tab 属性数据
                    var showContent = tabBasicAttrFormData["showContent"];//展示方式

                    //报表查询SQL
                    var sqlText = ReportDesignerMVC.Util.getReportQuerySQL(tabId);
                    data['reportComposeList[' + (i - 1) + '].sqlText'] = sqlText

                    var metaColumns = ReportDesignerMVC.Util.getMetaColumns(tabId);//元数据列
                    console.log(tabId,metaColumns)
                    var metaColumnTypes = []//列类型对象
                    if (metaColumns.length < 1) {
                        ReportDesignerMVC.Util.alterAndThrow("报表-" + tabId + "中没有配置元数据列！", true);
                    } else {//校验名称是否为空
                        _.each(metaColumns, function (metaColumn) {
                            metaColumnTypes.push(metaColumn.type);
                            if (!metaColumn.name) {
                                ReportDesignerMVC.Util.alterAndThrow("报表-" + tabId + "中元数据列名称不能为空！", true);
                            }
                        })
                    }

                    var metaColumnTypeCountBy = _.countBy(metaColumnTypes)
                    //图表只能设置一列布局列、一列维度列、一列统计列
                    console.log(showContent)
                    console.log(metaColumnTypeCountBy)
                    console.log(_.indexOf(chartShowContents, showContent))
                    var chartShowContents = ['11', '12', '13', '14', '15', '21', '22', '23', '24', '25'];
                    if (_.indexOf(chartShowContents, showContent) >= 0
                        && (metaColumnTypeCountBy['1'] != 1 || metaColumnTypeCountBy['2'] != 1 || metaColumnTypeCountBy['3'] != 1)) {
                        ReportDesignerMVC.Util.alterAndThrow("报表-" + tabId + "中元数据列类型应有布局列、维度列、统计列应唯一！", true);
                    }
                    data['reportComposeList[' + (i - 1) + '].metaColumns'] = JSON.stringify(metaColumns)
                    data['reportComposeList[' + (i - 1) + '].name'] = tabBasicAttrFormData["name"];
                    data['reportComposeList[' + (i - 1) + '].uid'] = tabId;
                    data['reportComposeList[' + (i - 1) + '].dsId'] = tabBasicAttrFormData["dsId"];
                    data['reportComposeList[' + (i - 1) + '].status'] = tabBasicAttrFormData["status"];
                    data['reportComposeList[' + (i - 1) + '].sequence'] = tabBasicAttrFormData["sequence"];
                    data['reportComposeList[' + (i - 1) + '].comment'] = tabBasicAttrFormData["comment"];

                    var options = {
                        enablePage: tabBasicAttrFormData["enablePage"],
                        pageSize: tabBasicAttrFormData["pageSize"],
                        showContent: tabBasicAttrFormData["showContent"],
                        floatCss: tabBasicAttrFormData["floatCss"],
                        reportWidth: tabBasicAttrFormData["reportWidth"],
                        widthUnit: tabBasicAttrFormData["widthUnit"],
                        reportHeight: tabBasicAttrFormData["reportHeight"],
                        heightUnit: tabBasicAttrFormData["heightUnit"],
                        marginTop: tabBasicAttrFormData["marginTop"],
                        marginRight: tabBasicAttrFormData["marginRight"],
                        marginBottom: tabBasicAttrFormData["marginBottom"],
                        marginLeft: tabBasicAttrFormData["marginLeft"],
                        marginUnit: tabBasicAttrFormData["marginUnit"],
                        paddingTop: tabBasicAttrFormData["paddingTop"],
                        paddingRight: tabBasicAttrFormData["paddingRight"],
                        paddingBottom: tabBasicAttrFormData["paddingBottom"],
                        paddingLeft: tabBasicAttrFormData["paddingLeft"],
                        paddingUnit: tabBasicAttrFormData["paddingUnit"]
                    }


                    data['reportComposeList[' + (i - 1) + '].options'] = JSON.stringify(options);

                    //查询参数
                    var tabQueryParams = ReportDesignerMVC.Util.getQueryParamsGridData(tabId);
                    data['reportComposeList[' + (i - 1) + '].queryParams'] = tabQueryParams

                    //报表说明部分  说明位置
                    var position = $("#" + tabContentId + " select[name='position']").val();
                    //报表说明部分  说明html
                    var explainHtml = ReportDesignerMVC.Variables.reportTabKindEditor['explain-' + tabId].html()
                    //报表说明部分  说明sql
                    var explainSqlText = $("#" + tabContentId + " textarea[name='explainSqlText']").val();

                    var reportExplain = {position: position, explainHtml: explainHtml, explainSqlText: explainSqlText};
                    data['reportComposeList[' + (i - 1) + '].reportExplain'] = JSON.stringify(reportExplain)
                }
            } else {
                ReportDesignerMVC.Util.alterAndThrow("请点击增加报表内容并配置！", true);
            }
            console.log(data)
            return data;
        },
        /**
         * 保存报表
         */
        saveReport: function () {
            var reportParams = ReportDesignerMVC.Controller.getReportSaveData();
            var action = $('#modal-action').val();
            var actUrl = action === "edit" ? ReportDesignerMVC.URLs.edit.url : ReportDesignerMVC.URLs.add.url;

            $.post(actUrl, reportParams, function (result) {
                $.messager.progress("close");
                if (result.respCode = '100') {
                    return $.messager.alert('操作提示', "保存成功", 'info', function () {
                        $("#report-designer-dlg").dialog('close');

                        ReportDesignerMVC.Controller.listReports(reportParams.categoryId);
                    });
                }
                $.messager.alert('操作提示', result.respDesc, 'error');
            }, 'json');
        },
        /**
         * 打开新增报表对话框报表
         */
        add: function () {
            var node = $('#category-tree').tree('getSelected');
            if (node) {
                $('#modal-action').val('add');
                var category = node.attributes;

                $("#report-designer-dlg").dialog('open').dialog('center').dialog('setTitle', "报表设计--新增报表");
                var commQueryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId);
                $("#" + commQueryParamGridId).datagrid("resize", {})

                var basicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);
                $("#" + basicAttrFormId + " select[name='categoryId']").val(category.id);

            } else {
                $.messager.alert('警告', '请选中一个报表分类!', 'info');
            }
        },
        /**
         * 修改报表
         */
        edit: function () {
            ReportDesignerMVC.Util.isRowSelected(function (row) {
                console.log(row)

                $("#report-designer-dlg").dialog('open').dialog('center').dialog('setTitle', "报表设计--修改报表");

                var reportBasicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);
                var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId);
                var reportBasicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);

                $('#modal-action').val('edit');

                $("#" + reportBasicAttrFormId + " input[name='id']").val(row.id);
                $("#" + reportBasicAttrFormId + " input[name='uid']").val(row.uid);

                //设置报表默认信息
                for (var key in row) {
                    if ($("#" + reportBasicAttrFormId).find("input[name='" + key + "']").size() == 1) {
                        $("#" + reportBasicAttrFormId).find("input[name='" + key + "']").val(row[key]);
                    } else if ($("#" + reportBasicAttrFormId).find("select[name='" + key + "']").size() == 1) {
                        $("#" + reportBasicAttrFormId).find("select[name='" + key + "']").val(row[key]);
                    }
                }

                //初始化公共参数Grid
                var commQueryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId);
                $("#" + commQueryParamGridId).datagrid("resize", {})

                if (row.queryParams && row.queryParams.length > 0) {
                    $("#" + commQueryParamGridId).datagrid('loadData', {rows: $.toJSON(row.queryParams)});
                }

                var reportComposeList = row.reportComposeList;
                for (var index in reportComposeList) {
                    var reportCompose = reportComposeList[index];
                    var uid = reportCompose.uid;
                    ReportDesignerMVC.Controller.addReportTab(uid);

                    ReportDesignerMVC.Controller.setReportComposeVal(reportCompose);
                }
            });
        },
        /**
         * 复制报表
         */
        copy: function () {
            ReportDesignerMVC.Util.isRowSelected(function (row) {
                ReportDesignerMVC.Controller.edit();
                $('#modal-action').val('add');
            });
        },
        /**
         * 设置报表组成tab的 值
         * @param reportCompose
         */
        setReportComposeVal: function (reportCompose) {
            var tabId = reportCompose.uid;
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var basicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(tabId);
            var metaColumnGridId = ReportDesignerMVC.Util.getMetaColumnGridId(tabId);
            var queryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);

            var options = $.toJSON(reportCompose.options);

            for (var key in reportCompose) {
                if ($("#" + basicAttrFormId).find("input[name='" + key + "']").size() == 1) {
                    $("#" + basicAttrFormId).find("input[name='" + key + "']").val(reportCompose[key]);
                } else if ($("#" + basicAttrFormId).find("select[name='" + key + "']").size() == 1) {
                    $("#" + basicAttrFormId).find("select[name='" + key + "']").val(reportCompose[key]);
                }
            }
            for (var key in options) {
                if ($("#" + basicAttrFormId).find("input[name='" + key + "']").size() == 1) {
                    $("#" + basicAttrFormId).find("input[name='" + key + "']").val(options[key]);
                } else if ($("#" + basicAttrFormId).find("select[name='" + key + "']").size() == 1) {
                    $("#" + basicAttrFormId).find("select[name='" + key + "']").val(options[key]);
                }
            }
            //设置报表Tab SQL
            var sqlTextArea = "#" + tabContentId + " .report-sql-text";
            $(sqlTextArea).val(reportCompose.sqlText);

            //设置报表Tab 元数据列
            if (reportCompose.metaColumns && reportCompose.metaColumns.length > 0) {
                $("#" + metaColumnGridId).datagrid('loadData', {rows: $.toJSON(reportCompose.metaColumns)});
            }

            //设置报表Tab 查询参数
            if (reportCompose.queryParams && reportCompose.queryParams.length > 0) {
                $("#" + queryParamGridId).datagrid('loadData', {rows: $.toJSON(reportCompose.queryParams)});
            }

            //设置报表说明
            //报表说明部分  说明位置
            var reportExplain = $.toJSON(reportCompose.reportExplain);
            $("#" + tabContentId + " select[name='position']").val(reportExplain.position);
            //报表说明部分  说明html
            ReportDesignerMVC.Variables.reportTabKindEditor['explain-' + tabId].html(reportExplain.explainHtml)
            //报表说明部分  说明sql
            $("#" + tabContentId + " textarea[name='explainSqlText']").val(reportExplain.explainSqlText);

        },
        /**
         * 重设新建或编辑报表对话框里的值
         */
        resetAddOrEditDlgVal: function () {
            $('#modal-action').val('');
            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(reportDefaultTabId);
            var reportBasicAttrFormId = ReportDesignerMVC.Util.getReportBasicAttrFormId(reportDefaultTabId);
            EasyUIUtils.clearDatagrid('#' + qeryParamGridId);
            $("#" + reportBasicAttrFormId + " input").val('');
            $("#" + reportBasicAttrFormId + " select").each(function (i) {
                $(this).find('option:first').prop('selected', 'selected');
            })

            ReportDesignerMVC.Controller.removeReportTab();//清除报表设计中的报表组成tab

        },
        /**
         * 点击搜索按钮
         */
        find: function () {
            var keyword = $.trim($("#report-search-keyword").val());
            var params = {fieldName: 'name', keyword: keyword}
            var node = $('#category-tree').tree('getSelected');
            if (node) {
                params.categoryId = node.id;
            }
            params.ctlVersion = 2;
            $('#report-datagrid').datagrid('load', params)
        },
        /**
         * 查询报表
         * @param id
         */
        listReports: function (id) {
            var params = {ctlVersion: 2};
            if (id) {
                params.categoryId = id;
            }
            $('#report-datagrid').datagrid('load', params)

        },
        /**
         * 删除报表
         */
        remove: function () {
            ReportDesignerMVC.Util.isRowSelected(function (row) {
                $.messager.confirm('确认', '确定删除报表吗？', function (r) {
                    if (r) {
                        ReportDesignerMVC.Util.showMessagerProgress(null, '正在删除...');
                        $.post(ReportDesignerMVC.URLs.remove.url, {id: row.id}, function (result) {
                            ReportDesignerMVC.Util.closeMessagerProgress();
                            if (result.respCode == '100') {
                                var categorys = $('#category-tree').tree('getChecked');
                                if (categorys.length > 0) {
                                    ReportDesignerMVC.Controller.listReports(categorys[0].id);
                                } else {
                                    ReportDesignerMVC.Controller.find();
                                }
                                return $.messager.alert('操作提示', "删除成功", 'info', function () {
                                });
                            }
                            $.messager.alert('操作提示', result.respDesc, 'error');
                        }, 'json');
                    }
                });
            });
        },
        preview: function () {
            ReportDesignerMVC.Util.isRowSelected(function (row) {
                //原来实现
                //var url = DesignerMVC.URLs.Report.url + row.uid;
                //parent.HomeIndex.addTab(row.id, row.name, url, "");
                //parent.HomeIndex.selectedTab();
                //修改后
                var host = window.location.host;
                if (host.substring(0, 4) != "http") {
                    host = "http://" + host;
                }
                //  var url=host+'/bd-tool-ReportHelper/report/publish/uid/'+ row.uid;
                var url = ReportHelper.ctxPath + '/lakalaReport/publish/uid/' + row.uid;
                var url ='/report/preview/uid/' + row.uid;
                window.open(url);
            });
        },


    },
    Data: {
        /**
         * 加载数据库源
         */
        loadDataSourceList: function () {
            $.getJSON(ReportDesignerMVC.URLs.DataSource.listAll.url, function (result) {
                console.log(result)
                if (result.respCode != '100') {
                    ReportDesignerMVC.Util.alterAndThrow("加载数据源错误，原因：" + result.respDesc + "！", true);
                }
                ReportDesignerMVC.Variables.dbDataSourceIds = result.respData;

                ReportDesignerMVC.Controller.setReportDataSourceOption();

            });
        },
        /**
         * 加载报表分类
         */
        loadCategoryList: function () {
            $.getJSON(CategoryMVC.URLs.list.url, {sort: 'sequence', order: 'asc'}, function (result) {
                if (result.respCode != '100') {
                    ReportDesignerMVC.Util.alterAndThrow("加载报表分类错误，原因：" + result.respDesc + "！", true);
                }
                ReportDesignerMVC.Variables.Categorys = result.respData.rows;

                ReportDesignerMVC.Controller.setReportCategoryOption();

            });
        }
    },
    Util: {
        /**
         * 报表Tab中最顶级的ID
         * @param tabId
         * @returns {string}
         */
        getTabContentId: function (tabId) {
            return "tab-content-" + tabId;
        },
        /**
         * 报表tab中元数据列treegrid的id
         * @param tabId
         * @returns {string}
         */
        getMetaColumnGridId: function (tabId) {
            return "meta-column-grid-" + tabId;
        },
        /**
         * 报表tab中查询参数datagrid的id
         * @param tabId
         * @returns {string}
         */
        getReportQueryParamGridId: function (tabId) {
            return "query-param-grid-" + tabId;
        },
        /**
         * 报表tab中增加和修改查询参数的表单
         * @param tabId
         * @returns {string}
         */
        getReportBasicAttrFormId: function (tabId) {
            return "report-attr-" + tabId;
        },
        /**
         * 报表tab中增加和修改查询参数的表单
         * @param tabId
         * @returns {string}
         */
        getReportQueryParamFormId: function (tabId) {
            return "query-param-form-" + tabId;
        },
        /**
         * 报表tab中报表说明的文本输入框ID
         * @param tabId
         * @returns {string}
         */
        getReportExplainTextareaId: function (tabId) {
            return "report-explain-textarea-" + tabId;
        },
        updateTreegridSelectRow: function (treegridId) {/*更新元数据列选中的行*/
            console.log(treegridId)
            var rows = EasyUIUtils.getTreegridRows(treegridId)
            if (rows && rows.length > 0) {
                for (var i = 0, j = rows.length; i < j; i++) {
                    var node = rows[i];
                    var id = node.id;
                    EasyUIUtils.removeAttrAndValue(node, "_parentId");
                    node["name"] = $("#name" + id).val();
                    node["text"] = $("#text" + id).val();
                    node["type"] = $("#type" + id).val();
                    node["width"] = $("#width" + id).val();
                    node["sortType"] = $("#sortType" + id).val();
                    node["decimals"] = $("#decimals" + id).val();
                    node["href"] = $("#href" + id).val();
                    node["hrefTarget"] = $("#hrefTarget" + id).val();
                    node["hidden"] = $("#hidden" + id).val();
                    node["downMergeCells"] = $("#downMergeCells" + id).val();
                    node["theadTextAlign"] = $("#theadTextAlign" + id).val();
                    node["showDecimals"] = $("#showDecimals" + id).val();
                    node["exportDecimals"] = $("#exportDecimals" + id).val();
                    $(treegridId).treegrid('update', {
                        id: id,
                        row: node
                    });
                }
            }
        },
        /**
         * 处理执行SQL生成的元数据列
         * @param columns
         * @returns {*}
         */
        eachMetaColumns: function (columns) {
            if (columns && columns.length) {
                for (var i = 0; i < columns.length; i++) {
                    var column = columns[i];
                    column.type = ReportDesignerMVC.Util.getColumnTypeValue(column.type);
                    column.sortType = ReportDesignerMVC.Util.getColumnSortTypeValue(column.sortType);
                    if (!column.id || column.id.length < 1) {
                        column.id = CommonUtils.getUUID(2);
                    }
                }
            }
            return columns;
        },
        getColumnTypeValue: function (name) {
            if (name == "NORMAL") {
                return 0;
            }
            if (name == "LAYOUT") {
                return 1;
            }
            if (name == "DIMENSION") {
                return 2;
            }
            if (name == "STATISTICAL") {
                return 3;
            }
            if (name == "COMPUTED") {
                return 4;
            }
            return 2;
        },
        getColumnSortTypeValue: function (name) {
            if (name == "DEFAULT") {
                return 0;
            }
            if (name == "DIGIT_ASCENDING") {
                return 1;
            }
            if (name == "DIGIT_DESCENDING") {
                return 2;
            }
            if (name == "CHAR_ASCENDING") {
                return 3;
            }
            if (name == "CHAR_DESCENDING") {
                return 4;
            }
            return 0;
        },
        loadMetaColumns: function (tabId, newColumns) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);
            var metaColumnGridId = ReportDesignerMVC.Util.getMetaColumnGridId(tabId);

            var oldColumns = EasyUIUtils.getTreegridRows('#' + metaColumnGridId);
            //如果列表中没有元数据列则直接设置成新的元数据列
            if (oldColumns == null || oldColumns.length == 0) {
                //return $("#report-meta-column-grid").datagrid('loadData', newColumns);
                for (var i = 0, j = newColumns.length; i < j; i++) {
                    var tmpObj = newColumns[i];
                    if (!tmpObj.id || tmpObj.id.length < 1)
                        tmpObj.id = CommonUtils.getUUID(2);
                }
                return $('#' + metaColumnGridId).treegrid('loadData', {rows: newColumns});
            }

            //如果列表中存在旧的列则需要替换相同的列并增加新列
            oldColumns = ReportDesignerMVC.Util.getMetaColumns(tabId);
            var oldRowMap = {};
            for (var i = 0; i < oldColumns.length; i++) {
                var name = oldColumns[i].name;
                oldRowMap[name] = oldColumns[i];
            }

            for (var i = 0; i < newColumns.length; i++) {
                var name = newColumns[i].name;
                if (oldRowMap[name]) {
                    oldRowMap[name].dataType = newColumns[i].dataType;
                    oldRowMap[name].width = newColumns[i].width;
                    newColumns[i] = oldRowMap[name];
                }
            }
            console.log(newColumns)
            //return $("#report-meta-column-grid").treegrid('loadData', newColumns);
            return $('#' + metaColumnGridId).treegrid('loadData', {rows: newColumns});

        },
        /**
         * 查询报表组成tab下的元数据配置
         * @param tabId
         * @returns {*}
         */
        getMetaColumns: function (tabId) {
            var metaColumnGridId = '#' + ReportDesignerMVC.Util.getMetaColumnGridId(tabId);

            ReportDesignerMVC.Util.updateTreegridSelectRow(metaColumnGridId);
            var rows = EasyUIUtils.getTreegridRows(metaColumnGridId);

            for (var rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                var column = rows[rowIndex];
                var id = column.id;
                column["name"] = $("#name" + id).val();
                column["text"] = $("#text" + id).val();
                column["type"] = $("#type" + id).val();
                column["width"] = $("#width" + id).val();
                column["sortType"] = $("#sortType" + id).val();
                column["decimals"] = $("#decimals" + id).val();
                column["href"] = $("#href" + id).val();
                column["hrefTarget"] = $("#hrefTarget" + id).val();
                column["hidden"] = $("#hidden" + id).val();
                column["downMergeCells"] = $("#downMergeCells" + id).val();
                column["theadTextAlign"] = $("#theadTextAlign" + id).val();
                column["showDecimals"] = $("#showDecimals" + id).val();
                column["exportDecimals"] = $("#exportDecimals" + id).val();
            }
            return rows;
        },
        /**
         * 获取报表查询参数的grid数据
         * @param tabId
         * @returns {string}
         */
        getQueryParamsGridData: function (tabId) {
            var qeryParamGridId = ReportDesignerMVC.Util.getReportQueryParamGridId(tabId);
            var rows = $("#" + qeryParamGridId).datagrid('getRows');
            return rows ? JSON.stringify(rows) : "";
        },
        /**
         * 实例化报表tabId下的报表说明
         * @param tabId
         */
        initKindEditor: function (tabId) {

            /**
             * 自定义清空按钮
             */
            KindEditor.lang({
                customButton: '清空内容'
            });
            KindEditor.plugin('customButton', function (K) {
                var self = this, name = 'customButton';
                self.clickToolbar(name, function () {
                    self.html('');
                });
            });

            var items = ["source", "undo", "redo", "preview", "code", "cut", "copy", "justifyleft",
                "justifycenter", "justifyright", "justifyfull", "insertorderedlist", "insertunorderedlist",
                "indent", "outdent", "clearhtml", "quickformat", "/", "formatblock",
                "fontname", "fontsize", "forecolor", "hilitecolor", "bold", "italic", "underline", "strikethrough", "lineheight",
                "removeformat", "hr", "pagebreak", "link", "unlink",//"about",
                'customButton'];

            var textareaId = '#' + ReportDesignerMVC.Util.getReportExplainTextareaId(tabId);
            ReportDesignerMVC.Variables.reportTabKindEditor['explain-' + tabId] = KindEditor.create(textareaId, {
                width: '167px',
                height: '80px',
                //allowFileManager: true,
                items: items,
                resizeType: 0//0:不允许改变框 1：只能改变高度  2：可以改变宽高
            })

            return


        },
        /**
         * 获取报表组成tab下的查询SQL
         * @param tabId
         * @returns {*}
         */
        getReportQuerySQL: function (tabId) {
            var tabContentId = ReportDesignerMVC.Util.getTabContentId(tabId);

            var sqlTextArea = "#" + tabContentId + " .report-sql-text";
            var sqlText = $.trim($(sqlTextArea).val());

            if (!sqlText) {
                ReportDesignerMVC.Util.alterAndThrow("报表SQL参数为空！", true);

            }
            return sqlText;
        },
        /**
         * 是否选中列表中的一行
         * @param func
         */
        isRowSelected: function (func) {
            var row = $('#report-datagrid').datagrid('getSelected');
            if (row) {
                func(row);
            } else {
                $.messager.alert('警告', '请选中一条记录!', 'info');
            }
        },
        /**
         * 显示进度提示
         * @param title
         * @param text
         */
        showMessagerProgress: function (title, text) {
            title = title ? '请稍后...' : title;
            text = text ? '正在执行中...' : text;

            $.messager.progress({
                title: title,
                text: text
            });
        },
        closeMessagerProgress: function () {
            $.messager.progress("close");
        },
        /**
         * alert提示
         * @param errorTips  提示内容
         * @param isThrow  是否抛异常
         */
        alterAndThrow: function (errorTips, isThrow) {
            if (!errorTips) {
                errorTips = "操作错误";
            }
            $.messager.alert("错误", errorTips, "error");
            if (isThrow) {
                throw errorTips;
            }
        }

    }

}