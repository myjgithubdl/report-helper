$(function () {
    ReportRoleManage.init();
});


var ReportRoleManage = {
    init: function () {
        ReportRoleManageMVC.View.initControl();
        ReportRoleManageMVC.View.bindEvent();
        ReportRoleManageMVC.View.bindValidate();
    }
};

var ReportRoleCommon = {
    baseUrl: ReportHelper.ctxPath + '/rest/member/reportRole/',
    baseIconUrl: ReportHelper.ctxPath + '/vendor/easyui-1.7.0/custom/themes/icons/'
};

var ReportRoleManageMVC = {
    URLs: {
        add: {
            url: ReportRoleCommon.baseUrl + 'add',
            method: 'POST'
        },
        edit: {
            url: ReportRoleCommon.baseUrl + 'edit',
            method: 'POST'
        },
        list: {
            url: ReportRoleCommon.baseUrl + 'list',
            method: 'GET'
        },
        remove: {
            url: ReportRoleCommon.baseUrl + 'remove',
            method: 'POST'
        },
        authorize: {
            url: ReportRoleCommon.baseUrl + 'authorize',
            method: 'GET'
        },
        getAllCategoryAndReport: {
            url: ReportRoleCommon.baseUrl + 'getAllCategoryAndReport',
            method: 'GET'
        },
        listPermissionTree: {
            url: ReportRoleCommon.baseUrl + 'listPermissionTree',
            method: 'GET'
        },
        isSuperAdmin: {
            url: ReportRoleCommon.baseUrl + 'isSuperAdmin',
            method: 'GET'
        }
    },
    View: {
        initControl: function () {
            $('#reportRole-datagrid').datagrid({
                method: 'get',
                fit: true,
                fitColumns: true,
                singleSelect: true,
                pagination: true,
                rownumbers: true,
                pageSize: 50,
                url: ReportRoleManageMVC.URLs.list.url,
                toolbar: [{
                    iconCls: 'icon-add',
                    handler: function () {
                        ReportRoleManageMVC.Controller.add();
                    }
                }, '-', {
                    iconCls: 'icon-edit1',
                    handler: function () {
                        ReportRoleManageMVC.Controller.edit();
                    }
                }, '-', {
                    iconCls: 'icon-perm',
                    handler: function () {
                        ReportRoleManageMVC.Controller.authorize();
                    }
                }, '-', {
                    iconCls: 'icon-remove1',
                    handler: function () {
                        ReportRoleManageMVC.Controller.remove();
                    }
                }, '-', {
                    iconCls: 'icon-reload',
                    handler: function () {
                        EasyUIUtils.reloadDatagrid('#reportRole-datagrid');
                    }
                }],
                loadFilter: function (src) {
                    if (src.respCode == '100') {
                        return src.respData;
                    }
                    return $.messager.alert('失败', src.msg, 'error');
                },
                columns: [[{
                    field: 'id',
                    title: '角色ID',
                    width: 50,
                    sortable: true
                }, {
                    field: 'name',
                    title: '名称',
                    width: 80,
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
                    field: 'comment',
                    title: '说明',
                    width: 80,
                    sortable: true
                },{
                    field: 'sequence',
                    title: '顺序',
                    width: 40,
                    sortable: true
                },  {
                    field: 'updateUserName',
                    title: '修改者',
                    width: 50,
                    sortable: true
                }, {
                    field: 'update_date',
                    title: '修改时间',
                    width: 80,
                    sortable: true
                }, {
                    field: 'options',
                    title: '操作',
                    width: 80,
                    formatter: function (value, row, index) {
                        var icons = [{
                            "name": "edit",
                            "title": "编辑"
                        }, {
                            "name": "perm",
                            "title": "授权"
                        }, {
                            "name": "remove",
                            "title": "删除"
                        }];
                        var buttons = [];
                        for (var i = 0; i < icons.length; i++) {
                            var tmpl = '<a href="#" title ="${title}" '
                                + 'onclick="ReportRoleManageMVC.Controller.doOption(\'${index}\',\'${name}\')">'
                                + '<img src="${imgSrc}" alt="${title}"/"></a>';
                            var data = {
                                title: icons[i].title,
                                name: icons[i].name,
                                index: index,
                                imgSrc: ReportRoleCommon.baseIconUrl + icons[i].name + ".png"
                            };
                            buttons.push(juicer(tmpl, data));
                        }
                        return buttons.join(' ');
                    }
                }]],
                onDblClickRow: function (rowIndex, rowData) {
                    return ReportRoleManageMVC.Controller.edit();
                }
            });

            // dialogs
            $('#role-dlg').dialog({
                closed: true,
                modal: true,
                width: 600,
                height: 300,
                iconCls: 'icon-add',
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#role-dlg").dialog('close');
                    }
                }, {
                    text: '保存',
                    iconCls: 'icon-save',
                    handler: ReportRoleManageMVC.Controller.save
                }]
            });

            $('#perm-tree-dlg').dialog({
                closed: true,
                modal: true,
                width: 560,
                height: 460,
                iconCls: 'icon-perm',
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#perm-tree-dlg").dialog('close');
                    }
                }, {
                    text: '保存',
                    iconCls: 'icon-save',
                    handler: ReportRoleManageMVC.Controller.save
                }]
            });

            $('#perm-tree').tree({
                checkbox: true,
                method: 'get',
                cascadeCheck: true,
                onClick: function (node) {
                    $('#perm-tree').tree('expandAll', node.target);
                },
                loadFilter: function (src, parent) {
                    if (!src.code) {
                        return src.respData;
                    }
                    return $.messager.alert('失败', src.msg, 'error');
                }
            });
        },
        bindEvent: function () {
            $('#btn-search').bind('click', ReportRoleManageMVC.Controller.find);
        },
        bindValidate: function () {
        }
    },
    Controller: {
        doOption: function (index, name) {
            $('#reportRole-datagrid').datagrid('selectRow', index);
            if (name == "edit") {
                return ReportRoleManageMVC.Controller.edit();
            }
            if (name == "remove") {
                return ReportRoleManageMVC.Controller.remove();
            }
            if (name == "perm") {
                return ReportRoleManageMVC.Controller.authorize();
            }
        },
        add: function () {
            var options = ReportRoleManageMVC.Util.getOptions();
            options.title = '新增角色';
            EasyUIUtils.openAddDlg(options);
            $('#sequence').textbox('setValue', "10");
            $('#status').combobox('setValue', "1");
            $('#isSystem').combobox('setValue', "0");
        },
        edit: function () {
            var row = $('#reportRole-datagrid').datagrid('getSelected');
            if (row) {
                var options = ReportRoleManageMVC.Util.getOptions();
                options.iconCls = 'icon-edit1';
                options.data = row;
                options.title = '修改[' + options.data.name + ']角色';
                EasyUIUtils.openEditDlg(options);
            } else {
                $.messager.alert('警告', '请选中一条记录!', 'info');
            }
        },
        authorize: function () {
            var row = $('#reportRole-datagrid').datagrid('getSelected');
            if (row) {
                $('#perm-tree-dlg').dialog('open').dialog('center');
                $('#perm-tree-dlg-layout').css({
                    top: "2px",
                    left: "2px"
                });
                $("#modal-action").val("authorize");
                $('#perm-role-id').val(row.id);
                $('#perm-tree').tree('options').url = ReportRoleManageMVC.URLs.getAllCategoryAndReport.url + '?reportRoleId=' + row.id;
                $("#perm-tree").tree('reload');
            } else {
                $.messager.alert('警告', '请选中一条记录!', 'info');
            }
        },
        find: function () {
            var fieldName = 'name';
            var keyword = $("#keyword").val();
            var url = ReportRoleManageMVC.URLs.list.url + '?&queryKeyword=' + keyword;
            EasyUIUtils.loadToDatagrid('#reportRole-datagrid', url)
        },
        remove: function () {
            var row = $('#reportRole-datagrid').datagrid('getSelected');
            if (row) {
                var options = {
                    rows: [row],
                    url: ReportRoleManageMVC.URLs.remove.url,
                    data: {
                        id: row.id
                    },
                    gridId: '#reportRole-datagrid',
                    gridUrl: ReportRoleManageMVC.URLs.list.url,
                    callback: function (rows) {
                    }
                };
                EasyUIUtils.remove(options);
            }
        },
        save: function () {
            var action = $('#modal-action').val();
            var options = {
                gridId: null,
                gridUrl: ReportRoleManageMVC.URLs.list.url,
                dlgId: "#role-dlg",
                formId: "#role-form",
                url: null,
                callback: function () {
                }
            };

            if (action === "authorize") {
                $('#reportIds').val(ReportRoleManageMVC.Util.getReportIds());
                options.dlgId = '#perm-tree-dlg';
                options.formId = '#perm-tree-form';
                options.url = ReportRoleManageMVC.URLs.authorize.url;
            } else {
                options.url = (action === "edit" ? ReportRoleManageMVC.URLs.edit.url : ReportRoleManageMVC.URLs.add.url);
                options.gridId = '#reportRole-datagrid';
            }
            return EasyUIUtils.save(options);
        }
    },
    Util: {
        getOptions: function () {
            return {
                dlgId: '#role-dlg',
                formId: '#role-form',
                actId: '#modal-action',
                rowId: '#roleId',
                title: '',
                iconCls: 'icon-add',
                data: {},
                callback: function (arg) {
                },
                gridId: null,
            };
        },
        getReportIds: function () {
            var nodes = $('#perm-tree').tree('getChecked');
            var ids = $.map(nodes, function (node) {
                console.log(node)
                if (node.attributes.type == 'report')
                    return node.id.replace("report-", "");
            });
            return ids.join(',');
        }
    }
};