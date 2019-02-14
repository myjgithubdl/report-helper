$(function () {
    MembershipMenu.init();
});

var MembershipMenu = {
    init: function () {
        MenuMVC.View.initControl();
        MenuMVC.View.bindEvent();
        MenuMVC.View.bindValidate();
        MenuMVC.Controller.loadMenuData();
    }
};

var MenuCommon = {
    baseUrl: ReportHelper.ctxPath + '/rest/member/menu/',
    baseIconUrl: ReportHelper.ctxPath + '/vendor/easyui-1.7.0/custom/themes/icons/',
};

var MenuMVC = {
    URLs: {
        add: {
            url: MenuCommon.baseUrl + 'add',
            method: 'POST'
        },
        edit: {
            url: MenuCommon.baseUrl + 'edit',
            method: 'POST'
        },
        list: {
            url: MenuCommon.baseUrl + 'list',
            method: 'GET'
        },
        move: {
            url: MenuCommon.baseUrl + 'move',
            method: 'POST'
        },
        remove: {
            url: MenuCommon.baseUrl + 'remove',
            method: 'POST'
        },
        getMenuTree: {
            url: MenuCommon.baseUrl + 'getSysMenuTree',
            method: 'GET'
        }
    },
    View: {
        initControl: function () {
            // 左边字典树
            $('#west').panel({
                tools: [{
                    iconCls: 'icon-add',
                    handler: MenuMVC.Controller.addRoot
                }, {
                    iconCls: 'icon-reload',
                    handler: function () {
                        MenuMVC.Controller.reloadTree();
                        EasyUIUtils.loadDataWithUrl('#menu-datagrid', MenuMVC.URLs.list.url + '?id=0');
                    }
                }]
            });

            $('#menu-tree').tree({
                checkbox: false,
                method: 'get',
                animate: true,
                dnd: true,
                onClick: function (node) {
                    console.log(node)
                    $('#menu-tree').tree('expand', node.target);
                    EasyUIUtils.loadDataWithUrl('#menu-datagrid', MenuMVC.URLs.list.url + '?id=' + node.id);
                },
                onDrop: function (target, source, point) {
                    var targetNode = $('#menu-tree').tree('getNode', target);
                    if (targetNode) {
                        $.post(MenuMVC.URLs.move.url, {
                            sourceId: source.id,
                            targetId: targetNode.id,
                            sourcePid: source.attributes.pid,
                            sourcePath: source.attributes.path
                        }, function (data) {
                            if (data.code) {
                                $.messager.alert('失败', data.msg, 'error');
                            }
                        }, 'json');
                    }
                },
                onContextMenu: function (e, node) {
                    e.preventDefault();
                    $('#menu-tree').tree('select', node.target);
                    $('#tree_ctx_menu').menu('show', {
                        left: e.pageX,
                        top: e.pageY
                    });
                }
            });

            $('#tree_ctx_menu').menu({
                onClick: function (item) {
                    if (item.name == "add") {
                        return MenuMVC.Controller.add();
                    }
                    if (item.name == "edit") {
                        return MenuMVC.Controller.editNode();
                    }
                    if (item.name == "remove") {
                        return MenuMVC.Controller.remove();
                    }
                }
            });

            $('#menu-datagrid').datagrid({
                method: 'get',
                fit: true,
                fitColumns: true,
                singleSelect: true,
                pagination: true,
                rownumbers: true,
                pageSize: 50,
                url: MenuMVC.URLs.list.url,
                toolbar: [{
                    iconCls: 'icon-add',
                    handler: function () {
                        MenuMVC.Controller.add();
                    }
                }, '-', {
                    iconCls: 'icon-edit1',
                    handler: function () {
                        MenuMVC.Controller.edit();
                    }
                }, '-', {
                    iconCls: 'icon-remove1',
                    handler: function () {
                        MenuMVC.Controller.remove();
                    }
                }, '-', {
                    iconCls: 'icon-reload',
                    handler: function () {
                        var node = $('#menu-tree').tree('getSelected');
                        if (node) {
                            EasyUIUtils.loadDataWithUrl('#menu-datagrid', MenuMVC.URLs.list.url + '?id=' + node.id);
                        }
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
                    title: 'id',
                    width: 50,
                    sortable: true
                }, {
                    field: 'pid',
                    title: 'pid',
                    width: 50,
                    sortable: true
                }, {
                    field: 'name',
                    title: '名称',
                    width: 50,
                    sortable: true,
                }, {
                    field: 'code',
                    title: '编号',
                    width: 100,
                    sortable: true
                }, {
                    field: 'icon',
                    title: '图标',
                    width: 20,
                    formatter: function (value, row, index) {
                        var fileName = value.replace("icon-", "");
                        var imgSrc = MenuCommon.baseIconUrl + fileName;
                        return '<img src="' + imgSrc + '.png" alt="图标"/">'
                    }
                }, {
                    field: 'url',
                    title: 'URL',
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
                    field: 'sequence',
                    title: '顺序',
                    width: 50,
                    sortable: true
                }, {
                    field: 'comment',
                    title: '说明',
                    width: 50,
                    sortable: true
                }, {
                    field: 'createDate',
                    title: '创建时间',
                    width: 85,
                    sortable: true
                }]],
                onDblClickRow: function (rowIndex, rowData) {
                    return MenuMVC.Controller.edit(rowIndex, rowData);
                }
            });

            // dialogs
            $('#menu-dlg').dialog({
                closed: true,
                modal: false,
                width: 560,
                height: 480,
                iconCls: 'icon-add',
                buttons: [{
                    text: '关闭',
                    iconCls: 'icon-no',
                    handler: function () {
                        $("#menu-dlg").dialog('close');
                    }
                }, {
                    text: '保存',
                    iconCls: 'icon-save',
                    handler: MenuMVC.Controller.save
                }]
            });
        },
        bindEvent: function () {
        },
        bindValidate: function () {
        }
    },
    Controller: {
        addRoot: function () {
            var name = "根模块";
            var id = "0";
            MenuMVC.Util.initAdd(id, name);
        },
        add: function () {
            var node = $('#menu-tree').tree('getSelected');
            if (node) {
                var name = node.attributes.name;
                var id = node.id;
                MenuMVC.Util.initAdd(id, name);
            } else {
                $.messager.alert('警告', '请选中一个父模块!', 'info');
            }
        },
        editNode: function () {
            var node = $('#menu-tree').tree('getSelected');
            if (node) {
                MenuMVC.Util.edit(node.attributes);
            }
        },
        edit: function () {
            var row = $('#menu-datagrid').datagrid('getSelected');
            MenuMVC.Util.edit(row);
        },
        remove: function () {
            var row = $('#menu-datagrid').datagrid('getSelected');
            var node = $('#menu-tree').tree('getSelected');
            node = node ? node.attributes : null;
            row = row || node;

            var data = {
                id: row.id,
                pid: row.pid
            };
            var options = {
                rows: [row],
                url: MenuMVC.URLs.remove.url,
                data: data,
                callback: function (rows) {
                    var row = rows[0];
                    MenuMVC.Controller.refreshNode(row.pid);
                    if(row.pid){
                        EasyUIUtils.loadDataWithUrl('#menu-datagrid', MenuMVC.URLs.list.url + '?id=' + row.pid);
                    }else{
                        MenuMVC.Controller.reloadTree();
                        EasyUIUtils.loadDataWithUrl('#menu-datagrid', MenuMVC.URLs.list.url + '?id=0');
                    }
                }
            };
            EasyUIUtils.remove(options);
        },
        save: function () {
            var action = $('#modal-action').val();
            var gridUrl = MenuMVC.URLs.list.url + '?id=' + $("#pid").val();
            var actUrl = action === "edit" ? MenuMVC.URLs.edit.url : MenuMVC.URLs.add.url;
            var options = {
                dlgId: "#menu-dlg",
                formId: "#menu-form",
                url: actUrl,
                callback: function () {
                    MenuMVC.Controller.reloadTree();
                    EasyUIUtils.loadDataWithUrl('#menu-datagrid', gridUrl);
                }
            };
            EasyUIUtils.save(options);
        },
        refreshNode: function (pid) {
            if (pid == "0") {
                this.reloadTree();
            } else {
                var node = $('#menu-tree').tree('find', pid);
                if (node) {
                    $('#menu-tree').tree('select', node.target);
                    $('#menu-tree').tree('reload', node.target);
                }
            }
        },
        reloadTree: function () {
            $('#menu-tree').tree('reload');
            MenuMVC.Controller.loadMenuData();
        },
        loadMenuData: function () {
            $.getJSON(MenuMVC.URLs.getMenuTree.url, function (src) {
                if (src.respCode=='100') {
                    $('#menu-tree').tree('loadData', src.respData);
                }
            });
        }
    },
    Util: {
        initAdd: function (id, name) {
            var options = MenuMVC.Util.getOptions();
            options.title = '新增[' + name + ']的子模块';
            EasyUIUtils.openAddDlg(options);

            $('#tr-parent-menu-name').show();
            $("#pid").val(id);
            $("#parent-menu-name").text(name);
            $("#sequence").textbox('setValue', 10);
            $('#linkType').combobox('setValue', '0');
            $('#target').combobox('setValue', '0');
            $('#status').combobox('setValue', '1');
        },
        edit: function (data) {
            $('#tr-parent-menu-name').hide();
            var options = MenuMVC.Util.getOptions();
            options.iconCls = 'icon-edit1';
            options.data = data;
            options.title = '修改[' + options.data.name + ']模块';
            EasyUIUtils.openEditDlg(options);
        },
        getOptions: function () {
            return {
                dlgId: '#menu-dlg',
                formId: '#menu-form',
                actId: '#modal-action',
                rowId: '#menuId',
                title: '',
                iconCls: 'icon-add',
                data: {},
                callback: function (arg) {
                },
                gridId: null,
            };
        }
    }
};
