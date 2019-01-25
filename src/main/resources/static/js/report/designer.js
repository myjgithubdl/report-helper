$(function () {
    MetaDataDesigner.init();
});

var MetaDataDesigner = {
    init: function () {
        DesignerMVC.View.initControl();
        DesignerMVC.View.resizeDesignerElments();
        DesignerMVC.View.initSqlEditor();
        DesignerMVC.View.initHistorySqlEditor();
        DesignerMVC.View.initPreviewSqlEditor();
        DesignerMVC.View.initKindEditor();
        DesignerMVC.View.bindEvent();
        DesignerMVC.View.bindValidate();
        DesignerMVC.View.initData();
    },
    listReports: function (category) {
        DesignerMVC.Controller.listReports(category.id);
    },
    addReport: function () {
        DesignerMVC.Controller.add();
    },
    showMetaColumnOption: function (index, name) {
        DesignerMVC.Controller.showMetaColumnOption(index, name);
    },
    deleteQueryParam: function (index) {
        DesignerMVC.Controller.deleteQueryParam(index);
    }
};

var DesignerCommon = {
    baseUrl: ReportHelper.ctxPath + '/rest/report/designer/',
    baseHistoryUrl: ReportHelper.ctxPath + '/rest/report/history/',
    baseDsUrl: ReportHelper.ctxPath + '/rest/report/ds/',
    baseCategoryUrl: ReportHelper.ctxPath + '/rest/report/category/',
    baseIconUrl: ReportHelper.ctxPath + '/vendor/easyui-1.7.0/custom/themes/icons/',
    baseReportUrl: ReportHelper.ctxPath + '/report/'
};

var DesignerMVC = {
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
        Category: {
            listAll: {
                url: DesignerCommon.baseCategoryUrl + 'listAll',
                method: 'GET'
            }
        },
        Report: {
            url: ReportHelper.ctxPath + '/report/preview/uid/',
            method: 'GET'
        }
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
        }],
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
        IsSort: [{
            text: "否",//默认
            value: "false"
        }, {
            text: "是",
            value: "true"
        }],
        DownMergeCellsList: [
            {
                text: "否",//默认
                value: "false"
            }, {
                text: "是",
                value: "true"
            }
        ],
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
        MetaDataGridInputHeight: '25px',
        MetaDataGridCheckedRow: {//元数据列是选中的行
            id: null,//操作行的ID
            state: null//状态为选中和不选中  select   和  unselect
        },
        DataSourceList: [],
        CategoryList: [],
    },
    View: {
        SqlEditor: null,
        ReportTipContentSqlEditor: null,
        PreviewSqlEditor: null,
        HistorySqlEditor: null,
        initControl: function () {
            $('#report-datagrid').datagrid({
                method: 'get',
                pageSize: 50,
                fit: true,
                pagination: true,
                rownumbers: true,
                fitColumns: true,
                singleSelect: true,
                toolbar: [{
                    text: '详细信息',
                    iconCls: 'icon-info',
                    handler: function () {
                        DesignerMVC.Controller.showDetail();
                    }
                }, '-', {
                    text: '增加',
                    iconCls: 'icon-add',
                    handler: DesignerMVC.Controller.add
                }, '-', {
                    text: '修改',
                    iconCls: 'icon-edit1',
                    handler: DesignerMVC.Controller.edit
                }, '-', {
                    text: '复制',
                    iconCls: 'icon-copy',
                    handler: DesignerMVC.Controller.copy
                }, '-', {
                    text: '预览',
                    iconCls: 'icon-preview',
                    handler: DesignerMVC.Controller.preview
                }, '-', {
                    text: '版本',
                    iconCls: 'icon-history',
                    handler: DesignerMVC.Controller.showHistorySql
                }, '-', {
                    text: '删除',
                    iconCls: 'icon-remove',
                    handler: DesignerMVC.Controller.remove
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
                    field: 'dsName',
                    title: '数据源',
                    width: 100,
                    sortable: true
                }, {
                    field: 'status',
                    title: '状态',
                    width: 50,
                    sortable: true,
                    formatter: function (value, row, index) {
                        return value == 1 ? "启用" : "禁用";
                    }
                }, {
                    field: 'updateUserName',
                    title: '修改者',
                    width: 100,
                    sortable: true
                }, {
                    field: 'createDate',
                    title: '创建时间',
                    width: 80,
                    sortable: true
                }, {
                    field: 'updateDate',
                    title: '修改时间',
                    width: 80,
                    sortable: true
                }, {
                    field: 'options',
                    title: '操作',
                    width: 100,
                    formatter: function (value, row, index) {
                        var icons = [{
                            "name": "info",
                            "title": "详细信息"
                        }, {
                            "name": "edit",
                            "title": "编辑"
                        }, {
                            "name": "copy",
                            "title": "复制"
                        }, {
                            "name": "preview",
                            "title": "预览"
                        }, {
                            "name": "history",
                            "title": "版本"
                        }, {
                            "name": "remove",
                            "title": "删除"
                        }];
                        var buttons = [];
                        for (var i = 0; i < icons.length; i++) {
                            var tmpl = '<a href="#" title ="${title}" ' +
                                'onclick="DesignerMVC.Controller.doOption(\'${index}\',\'${name}\')">' +
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
                    return DesignerMVC.Controller.preview();
                },
                onRowContextMenu: function (e, index, row) {
                    e.preventDefault();
                    $('#report-datagrid-ctx-menu').menu('show', {
                        left: e.pageX,
                        top: e.pageY
                    });
                }
            });

            $('#report-datagrid-ctx-menu').menu({
                onClick: function (item) {
                    if (item.name == "preview") {
                        return DesignerMVC.Controller.preview();
                    }
                    if (item.name == "window") {
                        return DesignerMVC.Controller.previewInNewWindow();
                    }
                    if (item.name == "add") {
                        return DesignerMVC.Controller.add();
                    }
                    if (item.name == "edit") {
                        return DesignerMVC.Controller.edit();
                    }
                    if (item.name == "remove") {
                        return DesignerMVC.Controller.remove();
                    }
                    if (item.name == "copy") {
                        return DesignerMVC.Controller.copy();
                    }
                    if (item.name == "info") {
                        return DesignerMVC.Controller.showDetail();
                    }
                    if (item.name == "history") {
                        return DesignerMVC.Controller.showHistorySql();
                    }
                    if (item.name == "refresh") {
                        return DesignerMVC.Controller.reload();
                    }
                }
            });

            //$('#report-meta-column-grid').datagrid({
            $('#report-meta-column-grid').treegrid({
                method: 'post',
                idField: 'id',
                treeField: 'id',
                animate: false,
                state: "open",
                singleSelect: true,
                autoRowHeight: true,
                tools: [{
                    iconCls: 'icon-up',
                    handler: function () {
                        EasyUIUtils.move("#report-meta-column-grid", 'up');
                        DesignerMVC.Util.updateTreegridSelectRow('#report-meta-column-grid');
                        EasyUIUtils.reSortTreegridSelectedRow("#report-meta-column-grid", 'up');
                    }
                }, '-', {
                    iconCls: 'icon-down',
                    handler: function () {
                        //EasyUIUtils.move("#report-meta-column-grid", 'down');
                        DesignerMVC.Util.updateTreegridSelectRow('#report-meta-column-grid');
                        EasyUIUtils.reSortTreegridSelectedRow("#report-meta-column-grid", 'down');
                    }
                }, '-', {
                    iconCls: 'icon-add',
                    handler: function () {
                        $.getJSON(DesignerMVC.URLs.getMetaColumnScheme.url, function (result) {
                            if (result.respCode == '100') {
                                var id = DesignerMVC.Util.getUUID();
                                var row = result.respData;
                                row.id = id;
                                var dataArrar = [];
                                //$('#report-meta-column-grid').datagrid('appendRow', row);
                                var treegridIdObj = $('#report-meta-column-grid');
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
                        EasyUIUtils.removeTreegridSelectedRow("#report-meta-column-grid");
                        return;
                        var row = $("#report-meta-column-grid").datagrid('getSelected');
                        if (row) {
                            var index = $("#report-meta-column-grid").datagrid('getRowIndex', row);
                            $("#report-meta-column-grid").datagrid('deleteRow', index);
                            var rows = $("#report-meta-column-grid").datagrid('getRows');
                            $("#report-meta-column-grid").datagrid('loadData', rows);
                        }
                    }
                }],
                onClickRow: function (row) {
                    if (DesignerMVC.Model.MetaDataGridCheckedRow.id == row.id) {//ID相同 之前选中的取消选中  未选中则设置选中
                        if (DesignerMVC.Model.MetaDataGridCheckedRow.state == "select") {
                            $("#report-meta-column-grid").treegrid("unselect", row.id);
                            DesignerMVC.Model.MetaDataGridCheckedRow.state = "unselect";
                        } else {
                            $("#report-meta-column-grid").treegrid("select", row.id);
                            DesignerMVC.Model.MetaDataGridCheckedRow.state = "select";
                        }
                    } else {
                        DesignerMVC.Model.MetaDataGridCheckedRow.id = row.id;
                        DesignerMVC.Model.MetaDataGridCheckedRow.state = "select";
                    }
                },
                frozenColumns: [[
                    {
                        field: 'id',
                        title: '主键',
                        //checkbox:true,
                        width: 125,
                        formatter: function (value, row, index) {
                            if (row.id == undefined) {
                                row.id = DesignerMVC.Util.getUUID();
                            }
                            return row.id;
                        }
                    }, {
                        field: 'name',
                        title: '列名',
                        width: 100,
                        formatter: function (value, row, index) {
                            var id = "name" + row.id;
                            if (row.name == undefined) {
                                row.name = '';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="name" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.name
                            });
                        }
                    }, {
                        field: 'text',
                        title: '标题',
                        width: 100,
                        height: 50,
                        formatter: function (value, row, index) {
                            var id = "text" + row.id;
                            if (row.text == undefined) {
                                row.text = '';
                            }
                            var text = row.text && row.text != 'null' ? row.text : '';
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="text" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.text
                            });
                        }
                    }

                ]],
                columns: [[
                    {
                        field: 'dataType',
                        title: '数据类型',
                        width: 90,
                        height: 50,
                        formatter: function (value, row, index) {
                            var id = "dataType" + row.id;
                            if (row.dataType == undefined) {
                                row.dataType = '';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="dataType" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.dataType
                            });
                        }
                    }, {
                        field: 'defaultValue',
                        title: '默认值',
                        width: 70,
                        height: 50,
                        formatter: function (value, row, index) {
                            var id = "defaultValue" + row.id;
                            if (row.defaultValue == undefined) {
                                row.defaultValue = '';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="defaultValue" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.defaultValue
                            });
                        }
                    },
                    {
                        field: 'metaColumnType',
                        title: '列类型',
                        width: 80,
                        formatter: function (value, row, index) {
                            var id = "metaColumnType" + row.id;
                            if (row.metaColumnType == undefined) {
                                row.metaColumnType = DesignerMVC.Model.MetaColumnTypes[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name=\"metaColumnType\" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.metaColumnType,
                                list: DesignerMVC.Model.MetaColumnTypes
                            });
                        }
                    }, {
                        field: 'columnWidth',
                        title: '宽度',
                        width: 50,
                        formatter: function (value, row, index) {
                            var id = "columnWidth" + row.id;
                            if (!row.columnWidth) {
                                row.columnWidth = '120';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" onkeyup="DesignerMVC.Util.checkInteger(this,3);" type="text" id="${id}" name="columnWidth" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.columnWidth
                            });
                        }
                    }, {
                        field: 'precision',
                        title: '显示精度',
                        width: 50,
                        formatter: function (value, row, index) {
                            var id = "precision" + row.id;
                            if (row.precision == undefined) {
                                row.precision = '';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="precision" value="${value}" onkeyup="DesignerMVC.Util.checkInteger(this,1);" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.precision
                            });
                        }
                    }, {
                        field: 'href',
                        title: '链接',
                        width: 80,
                        formatter: function (value, row, index) {
                            var id = "href" + row.id;
                            if (row.href == undefined) {
                                row.href = '';
                            }
                            var tmpl = '<input style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';" type="text" id="${id}" name="href" value="${value}" />';
                            return juicer(tmpl, {
                                id: id,
                                value: row.href
                            });
                        }
                    }, {
                        field: 'hrefTarget',
                        title: '打开方式',
                        width: 60,
                        formatter: function (value, row, index) {//HerfTarget
                            var id = "hrefTarget" + row.id;
                            if (value == undefined) {
                                row.hrefTarget = DesignerMVC.Model.HerfTargetList[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name=\"hrefTarget\" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.hrefTarget,
                                list: DesignerMVC.Model.HerfTargetList
                            });
                        }
                    }, {
                        field: 'hidden',
                        title: '显示列',
                        width: 60,
                        formatter: function (value, row, index) {//HerfTarget
                            var id = "hidden" + row.id;
                            if (value == undefined) {
                                row.hidden = DesignerMVC.Model.DisplayStyleList[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name=\"hidden\" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.hidden,
                                list: DesignerMVC.Model.DisplayStyleList
                            });
                        }
                    }, {
                        field: 'sort',
                        title: '排序',
                        width: 60,
                        formatter: function (value, row, index) {//
                            var id = "sort" + row.id;
                            if (value == undefined) {
                                row.sort = DesignerMVC.Model.IsSort[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name=\"sort\" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.sort,
                                list: DesignerMVC.Model.IsSort
                            });
                        }
                    }, {
                        field: 'downMergeCells',
                        title: '合并等值列',
                        width: 80,
                        formatter: function (value, row, index) {
                            var id = "downMergeCells" + row.id;
                            if (value == undefined) {
                                row.downMergeCells = DesignerMVC.Model.DownMergeCellsList[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name=\"downMergeCells\" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.downMergeCells,
                                list: DesignerMVC.Model.DownMergeCellsList
                            });
                        }
                    }, {
                        field: 'theadTextAlign',
                        title: '对齐方式',
                        width: 80,
                        formatter: function (value, row, index) {//textAlign
                            var id = "theadTextAlign" + row.id;
                            if (value == undefined) {
                                row.theadTextAlign = DesignerMVC.Model.TextAlignList[0].value;
                            }
                            var tmpl =
                                '<select id="${id}" name="theadTextAlign" style="width:98%;height:' + DesignerMVC.Model.MetaDataGridInputHeight + ';">' +
                                '{@each list as item}' +
                                '<option value="${item.value}" {@if item.value == currValue} selected {@/if}>${item.text}</option>' +
                                '{@/each}' +
                                '</select>';
                            return juicer(tmpl, {
                                id: id,
                                currValue: row.theadTextAlign,
                                list: DesignerMVC.Model.TextAlignList
                            });
                        }
                    }]]
            });

            $('#report-query-param-grid').datagrid({
                method: 'get',
                fit: true,
                singleSelect: true,
                rownumbers: true,
                tools: [{
                    iconCls: 'icon-up',
                    handler: function () {
                        EasyUIUtils.move('#report-query-param-grid', 'up');
                    }
                }, '-', {
                    iconCls: 'icon-down',
                    handler: function () {
                        EasyUIUtils.move('#report-query-param-grid', 'down');
                    }
                }],
                columns: [[{
                    field: 'text',
                    title: '标题',
                    width: 80
                }, {
                    field: 'name',
                    title: '参数名',
                    width: 80
                }, {
                    field: 'defaultText',
                    title: '默认标题',
                    width: 80
                }, {
                    field: 'defaultValue',
                    title: '默认值',
                    width: 80
                }, {
                    field: 'textWidth',
                    title: '标题宽度',
                    width: 70
                }, {
                    field: 'nameWidth',
                    title: '输入宽度',
                    width: 70
                }, {
                    field: 'dataType',
                    title: '数据类型',
                    width: 70,
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
                        return "";
                    }
                }, {
                    field: 'dataLength',
                    title: '数据长度',
                    width: 70
                }, {
                    field: 'isRequired',
                    title: '是否必选',
                    width: 70
                }, {
                    field: 'formElement',
                    title: '表单控件',
                    width: 80,
                    formatter: function (value, row, index) {
                        if (value == "select") {
                            return "下拉单选";
                        } else if (value == "selectMul") {
                            return "下拉多选";
                        } else if (value == "text") {
                            return "文本框";
                        } else if (value == "date") {
                            return "日期";
                        }
                        return "";
                    }
                }, {
                    field: 'dataSource',
                    title: '来源类型',
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
                    field: 'dateFormat',
                    title: '日期格式',
                    width: 100
                }, {
                    field: 'dateRange',
                    title: '默认日期',
                    width: 70
                }, {
                    field: 'triggerParamName',
                    title: '联动参数',
                    width: 60
                }, {
                    field: 'content',
                    title: '内容',
                    width: 80
                }, {
                    field: 'options',
                    title: '操作',
                    width: 50,
                    formatter: function (value, row, index) {
                        var imgPath = DesignerCommon.baseIconUrl + 'remove.png';
                        var tmpl = '<a href="#" title ="删除" ' +
                            'onclick="MetaDataDesigner.deleteQueryParam(\'${index}\')"><img src="${imgPath}" ' +
                            'alt="删除"/"></a>';
                        return juicer(tmpl, {
                            index: index,
                            imgPath: imgPath
                        });
                    }
                }]],
                onDblClickRow: function (index, row) {
                    $('#report-query-param-form').form('load', row);
                    $("#report-query-param-required").prop("checked", row.required);
                    $("#report-query-param-autoComplete").prop("checked", row.autoComplete);
                    $("#report-query-param-gridIndex").val(index);
                }
            });

            $('#report-history-sql-grid').datagrid({
                method: 'get',
                fit: true,
                pagination: true,
                rownumbers: true,
                fitColumns: true,
                singleSelect: true,
                pageSize: 30,
                loadFilter: function (src) {
                    if (src.respCode == '100') {
                        return src.respData;
                    }
                    return EasyUIUtils.getEmptyDatagridRows();
                },
                columns: [[{
                    field: 'gmtCreated',
                    title: '日期',
                    width: 100
                }, {
                    field: 'author',
                    title: '作者',
                    width: 100
                }]],
                loadFilter: function (src) {
                    if (src.respCode == '100') {
                        return src.respData;
                    }
                    $.messager.alert('失败', src.msg, 'error');
                    return EasyUIUtils.getEmptyDatagridRows();
                },
                onClickRow: function (index, row) {
                    DesignerMVC.Controller.showHistorySqlDetail(row);
                },
                onSelect: function (index, row) {
                    DesignerMVC.Controller.showHistorySqlDetail(row);
                }
            });

            $('#report-history-sql-pgrid').propertygrid({
                scrollbarSize: 0,
                columns: [[
                    {field: 'name', title: '属性项', width: 80, sortable: true},
                    {field: 'value', title: '属性值', width: 300, resizable: false}
                ]]
            });

            $('#report-designer-dlg').dialog({
                closed: true,
                modal: false,
                width: window.screen.width - 350,
                height: window.screen.height - 350,
                maximizable: true,
                minimizable: true,
                maximized: true,
                iconCls: 'icon-designer',
                buttons: [/*{
                    text: '编辑器放大/缩小',
                    iconCls: 'icon-fullscreen',
                    handler: DesignerMVC.Util.fullscreenEdit
                }, */
                    {
                        text: '关闭',
                        iconCls: 'icon-no',
                        handler: function () {
                            $("#report-designer-dlg").dialog('close');
                        }
                    }, {
                        text: '保存',
                        iconCls: 'icon-save',
                        handler: DesignerMVC.Controller.save
                    }],
                onResize: function (width, height) {
                    DesignerMVC.Util.resizeDesignerDlgElments();
                }
            });

            $('#report-history-sql-dlg').dialog({
                closed: true,
                modal: false,
                width: window.screen.width - 350,
                height: window.screen.height - 350,
                maximizable: true,
                iconCls: 'icon-history',
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#report-history-sql-dlg").dialog('close');
                    }
                }]
            });

            $('#report-detail-dlg').dialog({
                closed: true,
                modal: true,
                width: window.screen.width - 350,
                height: window.screen.height - 350,
                maximizable: true,
                iconCls: 'icon-info',
                buttons: [{
                    text: '上一条',
                    iconCls: 'icon-prev',
                    handler: function () {
                        EasyUIUtils.cursor('#report-datagrid',
                            '#current-row-index',
                            'prev', function (row) {
                                DesignerMVC.Controller.showDetail(row);
                            });
                    }
                }, {
                    text: '下一条',
                    iconCls: 'icon-next',
                    handler: function () {
                        EasyUIUtils.cursor('#report-datagrid',
                            '#current-row-index',
                            'next', function (row) {
                                DesignerMVC.Controller.showDetail(row);
                            });
                    }
                }, {
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#report-detail-dlg").dialog('close');
                    }
                }]
            });

            $('#report-preview-sql-dlg').dialog({
                closed: true,
                modal: true,
                maximizable: true,
                iconCls: 'icon-sql',
                width: window.screen.width - 350,
                height: window.screen.height - 350,
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#report-preview-sql-dlg").dialog('close');
                    }
                }]
            });

            $('#report-column-expression-dlg').dialog({
                closed: true,
                modal: true,
                iconCls: 'icon-formula',
                top: (screen.height - 500) / 2,
                left: (screen.width - 300) / 2,
                width: 500,
                height: 310,
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#report-column-expression-dlg").dialog('close');
                    }
                }, {
                    text: '保存',
                    iconCls: 'icon-save',
                    handler: function () {
                        DesignerMVC.Controller.saveMetaColumnOption('expression');
                    }
                }]
            });

            $('#report-column-comment-dlg').dialog({
                closed: true,
                modal: true,
                iconCls: 'icon-comment',
                top: (screen.height - 500) / 2,
                left: (screen.width - 300) / 2,
                width: 500,
                height: 310,
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#report-column-comment-dlg").dialog('close');
                    }
                }, {
                    text: '保存',
                    iconCls: 'icon-save',
                    handler: function () {
                        DesignerMVC.Controller.saveMetaColumnOption('comment');
                    }
                }]
            });

            $('#report-query-param-formElement').combobox({
                onSelect: function (rec) {
                    var value = "sql";
                    if (rec.value == "text" || rec.value == "date") {
                        value = 'none';
                    }
                    $('#report-query-param-dataSource').combobox('setValue', value);
                }
            });
        },
        resizeDesignerElments: function () {
            DesignerMVC.Util.resizeDesignerDlgElments();
        },
        initSqlEditor: function () {
            var dom = document.getElementById("report-sqlText");
            DesignerMVC.View.SqlEditor = CodeMirror.fromTextArea(dom, {
                mode: 'text/x-mysql',
                theme: 'rubyblue',
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                styleActiveLine: true,
                matchBrackets: true,
                autofocus: true,

                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) {
                            cm.setOption("fullScreen", false);
                        }
                    },
                    "Tab": "autocomplete"
                }
            });
            DesignerMVC.View.SqlEditor.on("change", function (cm, obj) {
                if (obj.origin == "setValue") {
                    $('#report-sqlTextIsChange').val(0);
                } else {
                    $('#report-sqlTextIsChange').val(1);
                }
            });
            DesignerMVC.View.SqlEditor.setSize('auto', '200px');

            var reportTipContentSqlTextDom = document.getElementById("report-tip-content-sqlText");
            DesignerMVC.View.ReportTipContentSqlEditor = CodeMirror.fromTextArea(reportTipContentSqlTextDom, {
                mode: 'text/x-mysql',
                theme: 'rubyblue',
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets: true,
                autofocus: true
            });

            DesignerMVC.View.ReportTipContentSqlEditor.setSize('auto', '120px');


        },
        initHistorySqlEditor: function () {
            var dom = document.getElementById("report-history-sqlText");
            DesignerMVC.View.HistorySqlEditor = CodeMirror.fromTextArea(dom, {
                mode: 'text/x-mysql',
                theme: 'rubyblue',
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets: true,
                autofocus: true,
                extraKeys: {
                    "F11": function (cm) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    },
                    "Esc": function (cm) {
                        if (cm.getOption("fullScreen")) {
                            cm.setOption("fullScreen", false);
                        }
                    }
                }
            });
        },
        initPreviewSqlEditor: function () {
            var dom = document.getElementById("report-preview-sqlText");
            DesignerMVC.View.PreviewSqlEditor = CodeMirror.fromTextArea(dom, {
                mode: 'text/x-mysql',
                theme: 'rubyblue',
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets: true,
                autofocus: true
            });
        },

        initKindEditor: function () {
            DesignerMVC.View.editor = null;
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

            KindEditor.ready(function (K) {
                var items = ["source", "|", "undo", "redo", "|", "preview", "print", "template", "code", "cut", "copy", "paste", "plainpaste",
                    "wordpaste", "|", "justifyleft", "justifycenter", "justifyright", "justifyfull", "insertorderedlist", "insertunorderedlist",
                    "indent", "outdent", "subscript", "superscript", "clearhtml", "quickformat", "selectall", "|", "fullscreen", "/", "formatblock",
                    "fontname", "fontsize", "|", "forecolor", "hilitecolor", "bold", "italic", "underline", "strikethrough", "lineheight",
                    "removeformat", "|", "image", "multiimage", "flash", "media", "insertfile", "table", "hr", "emoticons", "baidumap",
                    "pagebreak", "anchor", "link", "unlink", "|",//"about",
                    'customButton'];
                DesignerMVC.View.editor = K.create('#report-tip-content', {
                    allowFileManager: true,
                    items: items
                });

                DesignerMVC.View.multipleReportEditor = K.create('#multiple-report-composition-tip-content', {
                    allowFileManager: true,
                    items: items
                });
            });
        },

        bindEvent: function () {
            $('#btn-report-search').bind('click', DesignerMVC.Controller.find);
            $('#btn-report-exec-sql').bind('click', DesignerMVC.Controller.executeSql);
            $('#btn-report-preview-sql').bind('click', DesignerMVC.Controller.previewSql);
            $('#btn-report-query-param-add').bind('click', function (e) {
                DesignerMVC.Controller.addOrEditQueryParam('add');
            });
            $('#btn-report-query-param-edit').bind('click', function (e) {
                DesignerMVC.Controller.addOrEditQueryParam('edit');
            });
        },
        bindValidate: function () {
        },
        initData: function () {
            DesignerMVC.Data.loadDataSourceList();
            DesignerMVC.Data.loadCategoryList();
        }
    },
    Controller: {
        doOption: function (index, name) {
            $('#report-datagrid').datagrid('selectRow', index);
            if (name == "info") {
                return DesignerMVC.Controller.showDetail();
            }
            if (name == "edit") {
                return DesignerMVC.Controller.edit();
            }
            if (name == "copy") {
                return DesignerMVC.Controller.copy();
            }
            if (name == "preview") {
                return DesignerMVC.Controller.preview();
            }
            if (name == "remove") {
                return DesignerMVC.Controller.remove();
            }
            if (name == "history") {
                return DesignerMVC.Controller.showHistorySql();
            }
        },
        add: function () {
            var node = $('#category-tree').tree('getSelected');
            if (node) {
                var dsId = $('#report-dsId').combobox('getValue');
                var category = node.attributes;
                $('#report-categoryId').combobox('setValue', node.id);
                var options = DesignerMVC.Util.getOptions();
                options.title = '报表设计器--新增报表';
                EasyUIUtils.openAddDlg(options);
                DesignerMVC.Util.clearSqlEditor();
                EasyUIUtils.clearDatagrid('#report-meta-column-grid');
                var row = {
                    name: "",
                    categoryId: category.id,
                    dsId: dsId,
                    status: 1,
                    sequence: 10,
                    showContent: 1,
                    enablePage: 1,
                    pageSize: 10,
                    options: {
                        isStatistics: 'false',
                        statisticsRowPosition: 'bottom',
                    }
                };
                DesignerMVC.Util.fillReportBasicConfForm(row, row.options);
            } else {
                $.messager.alert('警告', '请选中一个报表分类!', 'info');
            }
        },
        edit: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                var options = DesignerMVC.Util.getOptions();
                options.iconCls = 'icon-designer';
                options.data = row;
                options.title = '报表设计器--修改[' + options.data.name + ']报表';
                DesignerMVC.Util.editOrCopy(options, 'edit');
            });
        },
        copy: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                var options = DesignerMVC.Util.getOptions();
                options.iconCls = 'icon-designer';
                options.data = row;
                options.title = '报表设计器--复制[' + options.data.name + ']报表';
                DesignerMVC.Util.editOrCopy(options, 'copy');
                $('#modal-action').val("add");
            });
        },
        remove: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                var options = {
                    rows: [row],
                    url: DesignerMVC.URLs.remove.url,
                    data: {
                        id: row.id
                    },
                    gridId: '#report-datagrid',
                    gridUrl: DesignerMVC.URLs.list.url + '?id=' + row.categoryId,
                    callback: function (rows) {
                    }
                };
                EasyUIUtils.remove(options);
            });
        },
        preview: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                var windowHref = window.location.href;
                console.log(windowHref);
                var url = ReportHelper.ctxPath + '/report/preview/uid/' + row.uid;
                console.log(url)
                if (parent.IndexMVC) {
                    parent.IndexMVC.Controller.openMentContent(row.uid, url, row.name, 'iframe')
                } else {
                    window.open(url);
                }
            });
        },
        previewInNewWindow: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                var url = DesignerMVC.URLs.Report.url + row.uid;
                var win = window.open(url, '_blank');
                win.focus();
            });
        },
        showDetail: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                $('#report-detail-dlg').dialog('open').dialog('center');
                DesignerMVC.Util.fillDetailLabels(row);
            });
        },
        showHistorySql: function () {
            DesignerMVC.Util.isRowSelected(function (row) {
                $('#report-history-sql-dlg').dialog('open').dialog('center');
                DesignerMVC.View.HistorySqlEditor.setValue('');
                DesignerMVC.View.HistorySqlEditor.refresh();
                var url = DesignerMVC.URLs.historyList.url + '?reportId=' + row.id;
                EasyUIUtils.loadDataWithCallback('#report-history-sql-grid', url, function () {
                    $('#report-history-sql-grid').datagrid('selectRow', 0);
                });
            });
        },
        showHistorySqlDetail: function (row) {
            DesignerMVC.View.HistorySqlEditor.setValue(row.sqlText || "");
            var data = EasyUIUtils.toPropertygridRows(row);
            $('#report-history-sql-pgrid').propertygrid('loadData', data);
        },
        find: function () {
            var keyword = $("#report-search-keyword").val();
            var url = DesignerMVC.URLs.find.url + '?fieldName=name&keyword=' + keyword;
            EasyUIUtils.loadToDatagrid('#report-datagrid', url)
        },
        executeSql: function () {
            if (!DesignerMVC.Util.checkBasicConfParam()) return;

            $.messager.progress({
                title: '请稍后...',
                text: '正在执行中...'
            });

            $.post(DesignerMVC.URLs.execSqlText.url, {
                sqlText: DesignerMVC.View.SqlEditor.getValue(),
                dsId: $('#report-dsId').combobox('getValue'),
                dataRange: $('#report-dataRange').val(),
                queryParams: DesignerMVC.Util.getQueryParams()
            }, function (result) {
                $.messager.progress("close");
                if (result.respCode == '100') {
                    $("#report-meta-column-grid").treegrid('clearChecked');
                    var columns = DesignerMVC.Util.eachMetaColumns(result.respData);
                    return DesignerMVC.Util.loadMetaColumns(columns);
                }
                return $.messager.alert('错误', result.respDesc);
            }, 'json');
        },
        previewSql: function () {
            if (!DesignerMVC.Util.checkBasicConfParam()) return;

            $.messager.progress({
                title: '请稍后...',
                text: '正在生成预览SQL...',
            });

            $.post(DesignerMVC.URLs.previewSqlText.url, {
                dsId: $('#report-dsId').combobox('getValue'),
                sqlText: DesignerMVC.View.SqlEditor.getValue(),
                dataRange: $('#report-dataRange').val(),
                queryParams: DesignerMVC.Util.getQueryParams()
            }, function (result) {
                $.messager.progress("close");
                if (!result.code) {
                    $('#report-preview-sql-dlg').dialog('open');
                    $('#report-preview-sql-dlg .CodeMirror').css("height", "99%");
                    return DesignerMVC.View.PreviewSqlEditor.setValue(result.data);
                }
                return $.messager.alert('错误', result.msg);
            }, 'json');
        },
        save: function () {
            if (!DesignerMVC.Util.checkBasicConfParam()) return;

            //元数据行
            DesignerMVC.Util.updateTreegridSelectRow('#report-meta-column-grid');
            var rows = EasyUIUtils.getTreegridRows('#report-meta-column-grid');
            var metaColumns = DesignerMVC.Util.getMetaColumns(rows);
            if (!DesignerMVC.Util.checkMetaColumns(metaColumns)) {
                return;
            }

            //报表展示内容
            var showContent = $("#report-showContent").combobox('getValue');
            if (!DesignerMVC.Util.checkReportShowContent(showContent, metaColumns)) {
                return;
            }

            //报表查询参数
            $('#report-queryParams').val(DesignerMVC.Util.getQueryParams());
            //报表说明参数
            $("#report-explain").val(DesignerMVC.Util.getReportExplain());

            var action = $('#modal-action').val();
            var actUrl = action === "edit" ? DesignerMVC.URLs.edit.url : DesignerMVC.URLs.add.url;
            var data = $('#report-basic-conf-form').serializeObject();

            data.isChange = $('#report-sqlTextIsChange').val() != 0;
            data.sqlText = DesignerMVC.View.SqlEditor.getValue();
            data["options"] = JSON.stringify({
                enablePage: data.enablePage,
                pageSize: data.pageSize,
                showContent: data.showContent,
                isStatistics: data.isStatistics,
                statisticsRowPosition: data.statisticsRowPosition
            });
            data["metaColumns"] = JSON.stringify(metaColumns);

            console.log(data);

            $.messager.progress({
                title: '请稍后...',
                text: '正在处理中...',
            });

            $.post(actUrl, data, function (result) {
                $.messager.progress("close");
                if (!result.code) {
                    $('#report-sqlTextIsChange').val('0');
                    var id = $("#report-categoryId").val();
                    return $.messager.alert('操作提示', "操作成功", 'info', function () {
                        $('#report-designer-dlg').dialog('close');
                        DesignerMVC.Controller.listReports(id);
                    });
                }
                $.messager.alert('操作提示', result.msg, 'error');
            }, 'json');
        },
        reload: function () {
            EasyUIUtils.reloadDatagrid('#report-datagrid');
        },
        deleteQueryParam: function (index) {
            $("#report-query-param-grid").datagrid('deleteRow', index);
            $("#report-query-param-grid").datagrid('reload', $("#report-query-param-grid").datagrid('getRows'));
        },
        addOrEditQueryParam: function (act) {
            if ($("#report-query-param-form").form('validate')) {
                var row = $('#report-query-param-form').serializeObject();
                var formElement = row.formElement;
                if ((formElement == 'select' || formElement == 'selectMul') && $.trim(row.content).length == 0) {
                    $("#report-query-param-content").focus();
                    return $.messager.alert('提示', "内容不能为空", 'error');
                }

                if (act == "add") {
                    $('#report-query-param-grid').datagrid('appendRow', row);
                } else if (act == "edit") {
                    var index = $("#report-query-param-gridIndex").val();
                    $('#report-query-param-grid').datagrid('updateRow', {
                        index: index,
                        row: row
                    });
                }
                $('#report-query-param-form').form('reset');
            }
        },
        showMetaColumnOption: function (index, name) {
            $("#report-meta-column-grid").treegrid('selectRow', index);
            var row = $("#report-meta-column-grid").treegrid('getSelected');
            if (name == "expression") {
                $('#report-column-expression-dlg').dialog('open');
                return $("#report-column-expression").val(row.expression);
            }
            if (name == "comment") {
                $('#report-column-comment-dlg').dialog('open');
                return $("#report-column-comment").val(row.comment);
            }
        },
        saveMetaColumnOption: function (name) {
            var row = $("#report-meta-column-grid").treegrid('getSelected');
            if (name == "expression") {
                row.expression = $("#report-column-expression").val();
                return $('#report-column-expression-dlg').dialog('close');
            }
            if (name == "comment") {
                row.comment = $("#report-column-comment").val();
                return $('#report-column-comment-dlg').dialog('close');
            }
        },
        listReports: function (id) {
            var gridUrl = DesignerMVC.URLs.list.url + '?id=' + id;
            EasyUIUtils.loadDataWithUrl('#report-datagrid', gridUrl);
        }
    },
    Util: {
        getOptions: function () {
            return {
                dlgId: '#report-designer-dlg',
                formId: '#report-basic-conf-form',
                actId: '#modal-action',
                rowId: '#report-id',
                title: '',
                iconCls: 'icon-add',
                data: {},
                callback: function (arg) {
                },
                gridId: null,
            };
        },
        isRowSelected: function (func) {
            var row = $('#report-datagrid').datagrid('getSelected');
            if (row) {
                func(row);
            } else {
                $.messager.alert('警告', '请选中一条记录!', 'info');
            }
        },
        editOrCopy: function (options, act) {
            DesignerMVC.Util.clearSqlEditor();
            EasyUIUtils.openEditDlg(options);
            EasyUIUtils.clearTreegrid("#report-meta-column-grid")


            var row = options.data;
            if (act === 'copy') {
                row.name = '';
            }
            DesignerMVC.Util.fillReportBasicConfForm(row, $.toJSON(row.options));

            DesignerMVC.View.SqlEditor.setValue(row.sqlText || "");


            DesignerMVC.Util.loadMetaColumns($.toJSON(row.metaColumns));
            DesignerMVC.Util.loadQueryParams($.toJSON(row.queryParams));
            DesignerMVC.Util.loadReportExplain($.toJSON(row.reportExplain));
        },
        clearSqlEditor: function () {
            DesignerMVC.View.SqlEditor.setValue('');
            DesignerMVC.View.SqlEditor.refresh();

            DesignerMVC.View.PreviewSqlEditor.setValue('');
            DesignerMVC.View.PreviewSqlEditor.refresh();

            DesignerMVC.View.HistorySqlEditor.setValue('');
            DesignerMVC.View.HistorySqlEditor.refresh();

            DesignerMVC.View.ReportTipContentSqlEditor.setValue('');
            DesignerMVC.View.ReportTipContentSqlEditor.refresh();

        },
        resizeDesignerDlgElments: function () {
            var panelOptions = $('#report-designer-dlg').panel('options');
            $('#report-sqlText-td>.CodeMirror').css({"width": panelOptions.width - 230});

            var tabHeight = panelOptions.height - 160;
            var confFormDivHeight = $('#report-basic-conf-form-div').height();
            var metaColumnDivHeight = (tabHeight - confFormDivHeight);
            if (metaColumnDivHeight <= 180) metaColumnDivHeight = 180;
            $('#report-meta-column-div').css({
                "height": metaColumnDivHeight
            });

            var queryParamFormDivHeight = $('#report-query-param-form-div').height();
            var queryParamDivHeight = (tabHeight - queryParamFormDivHeight - 200);
            if (queryParamDivHeight <= 180) queryParamDivHeight = 180;
            $('#report-query-param-div').css({
                "height": queryParamDivHeight
            });
        },
        fullscreenEdit: function () {
            DesignerMVC.View.SqlEditor.setOption("fullScreen", !DesignerMVC.View.SqlEditor.getOption("fullScreen"));
        },
        fillReportBasicConfForm: function (row, options) {
            $('#report-basic-conf-form').form('load', row);
            $('#report-basic-conf-form').form('load', options);
        },
        fillDetailLabels: function (data) {
            $('#report-detail-dlg label').each(function (i) {
                $(this).text("");
            });

            for (var name in data) {
                var id = "#report-detail-" + name;
                var value = DesignerMVC.Util.getPropertyValue(name, data);
                $(id).text(value);
            }

            var options = $.toJSON(data.options);
            for (var name in options) {
                var id = "#report-detail-" + name;
                var value = DesignerMVC.Util.getPropertyValue(name, options);
                $(id).text(value);
            }
        },
        getPropertyValue: function (name, object) {
            var value = object[name];
            if (name == "layout" ||
                name == "statColumnLayout") {
                return DesignerMVC.Util.getLayoutName(value);
            }
            if (name == "status") {
                return value == 1 ? "启用" : "禁用";
            }
            return value;
        },
        getLayoutName: function (layout) {
            if (layout == 1) {
                return "横向布局";
            }
            if (layout == 2) {
                return "纵向布局";
            }
            return "无";
        },
        checkBasicConfParam: function () {
            if (DesignerMVC.View.SqlEditor.getValue() == "") {
                $.messager.alert('失败', "SQL语句为空！", 'error');
                return false;
            }
            return $('#report-basic-conf-form').form('validate');
        },
        checkMetaColumns: function (metaColumns) {
            if (!metaColumns || metaColumns.length < 1) {
                $.messager.alert('失败', "元数据列未配置！", 'error');
                return false
            }
            //检验元数据列
            for (var index in metaColumns) {
                var row = metaColumns[index];
                if (row.text.length < 1) {
                    $("#text" + row.id).focus();
                    $.messager.alert('失败', "元数据列配置中标题不能为空！", 'error');
                    return false;
                }
                if (row.text.length < 1) {
                    $("#report-meta-column-grid").treegrid('checkNode', row.id);
                    $.messager.alert('失败', "元数据列配置中标题不能为空！", 'error');
                    return false;
                }
                //没有孩子的列名称不能为空
                if (row.hasChild == 'N' && row.name.length < 1) {
                    $("#report-meta-column-grid").treegrid('checkNode', row.id);
                    $.messager.alert('失败', "元数据列配置中列名不能为空！", 'error');
                    return false
                }
            }
            return true
        },
        checkReportShowContent: function (showContent, metaColumns) {
            console.log("报表展示内容：" + showContent);
            //检验图表类型
            if (showContent >= 2 && showContent <= 6) {
                var columnTypeMap = DesignerMVC.Util.getColumnTypeMap(metaColumns);
                console.log(columnTypeMap)
                if ((showContent == 2 || showContent == 3 || showContent == 6) && (columnTypeMap.layout == 0
                    || columnTypeMap.dim == 0 || columnTypeMap.stat == 0)) {//折线图、柱状图
                    $.messager.alert('失败', "您没有设置布局列、维度列、统计列", 'error');
                    return false;
                } else if ((showContent == 4 || showContent == 5) && (columnTypeMap.layout == 0
                    || columnTypeMap.stat == 0)) {//饼图、漏斗图
                    $.messager.alert('失败', "您没有设置布局列、统计列", 'error');
                    return false;
                }
            }
            return true;
        },
        loadMetaColumns: function (newColumns) {
            var oldColumns = EasyUIUtils.getTreegridRows("#report-meta-column-grid");
            //如果列表中没有元数据列则直接设置成新的元数据列
            if (oldColumns == null || oldColumns.length == 0) {
                return $("#report-meta-column-grid").treegrid('loadData', {rows: newColumns});
            }

            //如果列表中存在旧的列则需要替换相同的列并增加新列
            oldColumns = DesignerMVC.Util.getMetaColumns(oldColumns);
            var oldRowMap = {};
            for (var i = 0; i < oldColumns.length; i++) {
                var name = oldColumns[i].name;
                oldRowMap[name] = oldColumns[i];
            }

            for (var i = 0; i < newColumns.length; i++) {
                var name = newColumns[i].name;
                if (oldRowMap[name]) {
                    oldRowMap[name].dataType = newColumns[i].dataType;
                    oldRowMap[name].columnWidth = newColumns[i].columnWidth;
                    newColumns[i] = oldRowMap[name];
                }
            }
            return $("#report-meta-column-grid").treegrid('loadData', newColumns);
        },
        getMetaColumns: function (columns) {
            if (columns == null || columns.length < 1) {
                return null;
            }

            for (var rowIndex = 0; rowIndex < columns.length; rowIndex++) {
                var column = columns[rowIndex];
                console.log(column)
                var id = column.id;
                for (var key in column) {
                    if (key == 'precision') {
                        console.log(("#" + key + id))
                        console.log($("#" + key + id).val())
                    }

                    if ($("#" + key + id).size() == 1) {
                        column[key] = $.trim($("#" + key + id).val());
                    }
                }
            }
            return columns;
        },
        eachMetaColumns: function (columns) {
            if (columns && columns.length) {
                for (var i = 0; i < columns.length; i++) {
                    var column = columns[i];
                    column.metaColumnType = 0;
                }
            }
            return columns;
        },
        getColumnTypeMap: function (rows) {
            var typeMap = {
                "layout": 0,
                "dim": 0,
                "stat": 0,
            };
            for (var i = 0; i < rows.length; i++) {
                if (rows[i].metaColumnType == 1) {
                    typeMap.layout += 1;
                } else if (rows[i].metaColumnType == 2) {
                    typeMap.dim += 1;
                } else if (rows[i].metaColumnType == 3) {
                    typeMap.stat += 1;
                }
            }
            return typeMap;
        },
        loadQueryParams: function (params) {
            EasyUIUtils.clearDatagrid('#report-query-param-grid');
            var jsonText = JSON.stringify(params);
            if (params instanceof Array) {
                $("#report-query-param-grid").datagrid('loadData', params);
            } else {
                jsonText = "";
            }
            $("#report-query-param-json").val(jsonText);
        },
        loadReportExplain: function (params) {
            $("#report-tip-position").combobox('setValue', params.position);
            DesignerMVC.View.editor.html(params.html);
            DesignerMVC.View.ReportTipContentSqlEditor.setValue(params.sqlText);
            DesignerMVC.View.ReportTipContentSqlEditor.refresh();
        },
        getQueryParams: function () {
            var rows = $("#report-query-param-grid").datagrid('getRows');
            return rows ? JSON.stringify(rows) : "";
        },
        getReportExplain: function () {
            //报表提示内容
            var reportTipPosition = $("#report-tip-position").combobox('getValue');
            var reportTipHtml = DesignerMVC.View.editor.html();
            var reportTipSqlText = DesignerMVC.View.ReportTipContentSqlEditor.getValue()
            var reportExplain = {position: reportTipPosition, html: reportTipHtml, sqlText: reportTipSqlText};
            return JSON.stringify(reportExplain);
        },
        checkInteger: function (obj, integerLength) {
            if (!obj) return;
            var value = $(obj).val();
            if (value != "" && !DesignerMVC.Util.isNumber(value)) {
                value = value.substring(0, value.length - 1);
                $(obj).val(value);
                if (!DesignerMVC.Util.isNumber(value)) {//再次检查，如果不符合数字，继续循环
                    DesignerMVC.Util.checkInteger(obj);
                }
                return;
            }
            if (integerLength && integerLength > 0) {
                if (value.length > integerLength) {
                    $(obj).val(value.substring(0, integerLength));
                }
            }
        },
        isNumber: function (num) {
            var numExp = /^[0-9]\d*$/; //数字正则表达式
            var reg = new RegExp(numExp);
            return reg.test(num);
        },
        isInteger: function (num) {//
            var numExp = new RegExp("^-?\\d+$"); //整数
            var reg = new RegExp(numExp);
            return reg.test(num);
        },
        S4: function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        },
        getUUID: function () {
            //var uuid = (DesignerMVC.Util.S4() + DesignerMVC.Util.S4() + "-" + DesignerMVC.Util.S4() + "-" +
            //    DesignerMVC.Util.S4() + "-" + DesignerMVC.Util.S4() + "-" + DesignerMVC.Util.S4() + DesignerMVC.Util.S4() + DesignerMVC.Util.S4());
            var uuid = DesignerMVC.Util.S4();
            return uuid;
        },
        updateTreegridSelectRow: function (treegridId) {/*更新元数据列选中的行*/
            var rows = EasyUIUtils.getTreegridRows(treegridId)
            if (rows && rows.length > 0) {
                for (var i = 0, j = rows.length; i < j; i++) {
                    var node = rows[i];
                    var id = node.id;
                    EasyUIUtils.removeAttrAndValue(node, "_parentId");
                    for (var key in node) {
                        var jqId = "#" + key + id;
                        if ($(jqId).size() == 1) {
                            node[key] = $(jqId).val();
                        }
                    }
                    $(treegridId).treegrid('update', {
                        id: id,
                        row: node
                    });
                }
            }
        },

    },
    Data: {
        loadDataSourceList: function () {
            $.getJSON(DesignerMVC.URLs.DataSource.listAll.url, function (result) {
                if (result.respCode == '100') {

                }
                DesignerMVC.Model.DataSourceList = result.respData;
                EasyUIUtils.fillCombox("#report-dsId", "add", result.respData, "");
            });
        },
        loadCategoryList: function () {
            $.getJSON(DesignerMVC.URLs.Category.listAll.url, function (result) {
                if (result.respCode == '100') {
                    DesignerMVC.Model.CategoryList = result.respData;
                    EasyUIUtils.fillCombox("#report-categoryId", "add", result.respData, "");
                }
            });
        }
    }
};
