var QueryParameterManageMVC = {
    URLs: {
        QueryReportParameter: {
            url: ReportHelper.ctxPath + '/report/preview/queryReportParameter/uid/',
            method: 'post'
        },
        ReloadSelectParamOption: {
            url: ReportHelper.ctxPath + '/report/preview/reloadSelectParamOption/uid/',
            method: 'post'
        }
    },
    View: {
        /**
         * 初始化操作
         */
        initControl: function () {


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
        /**
         * 创建选择框参数
         * @param formId  需要将查询参数生成的表单插入到的jQuery dom对象
         * @param uid   报表uid
         * @param options   参数对象
         *   selectParamChangeFunName : 选择框值改变时触发的方法名称，如options.selectParamChangeFunName=abc，则在select的onchange=abc('name');
         */
        createReportQueryParameter: function (formId, uid, options) {
            $.post(QueryParameterManageMVC.URLs.QueryReportParameter.url + uid, {}, function (result) {
                console.log(result)
                if (result.respCode == '100') {
                    for (var index in result.respData) {
                        var queryParamItem = result.respData[index];
                        var formElement = queryParamItem.formElement;
                        console.log(queryParamItem)
                        switch (formElement) {
                            case 'text':
                                QueryParameterManageMVC.Service.createTextParam(formId, queryParamItem)
                                break;
                            case 'select':
                                QueryParameterManageMVC.Service.createSelectParam(formId, queryParamItem, options)
                                break;
                            case 'selectMul':
                                QueryParameterManageMVC.Service.createSelectParam(formId, queryParamItem, options)
                                break;
                            case 'date':
                                QueryParameterManageMVC.Service.createDateParam(formId, queryParamItem)
                                break;
                            default:
                                console.log("未处理类型：" + type);
                                break;
                        }
                    }
                }
            }, 'json');
        },
        /**
         * 加载选择框参数的options
         * @param name
         */
        reloadSelectParamOption: function (name, formId) {
            var params = QueryParameterManageMVC.Service.getQueryParams($("#" + formId), true);
            params.triggerParamName = name;
            console.log(params)
            $.post(QueryParameterManageMVC.URLs.ReloadSelectParamOption.url + uid, params, function (result) {
                    console.log(result)
                    if (result.respCode == '100') {
                        var options = '', respData = result.respData;
                        if (result.respData && result.respData.length > 0) {
                            for (var index in respData) {
                                options += '<option value="' + respData[index].value + '">' + respData[index].text + '</option>';
                            }
                            $('#param-' + name).html(options);
                            $('#param-' + name).trigger('chosen:updated');
                        }
                    }
                }
                ,
                'json'
            );
        }
    },
    Service: {
        /**
         * 创建选择框查询参数
         * @param queryParamItem
         */
        createSelectParam: function (formId, queryParamItem, options) {
            var labelWidth = QueryParameterManageMVC.Service.getLabelWidth(queryParamItem);
            var inputWidth = QueryParameterManageMVC.Service.getInputWidth(queryParamItem);

            var multipledText = '';

            if (queryParamItem.multiple) {
                multipledText = ' multiple="" ';
                //multipledText = ' multiple ';
            }
            var triggerParamName = '';//是否需要触发执行参数
            if (queryParamItem.triggerParamName
                && queryParamItem.triggerParamName.length > 0
                && options.selectParamChangeFunName) {
                triggerParamName = ' onchange="' + options.selectParamChangeFunName + '(\'' + queryParamItem.triggerParamName + '\',\'' + formId + '\',\'' + uid + '\')" ';
            }

            var optionstr = "";
            for (var i in queryParamItem.optionList) {
                var selected = "";
                if (queryParamItem.optionList[i].selected) {
                    selected = 'selected="selected"'
                }
                optionstr += "<option value='" + queryParamItem.optionList[i].value + "' " + selected + ">" + queryParamItem.optionList[i].text + "</option>";
            }
            var dom = '<div class="fl param-div mr10"><div class="fl param-label" style="' + labelWidth + '">' + queryParamItem.text + '</div>';
            dom += '<div class="param-value-div fl">';
            dom += '<select class="chosen-select form-control" id="param-' + queryParamItem.name + '" ' + multipledText + ' name="' + queryParamItem.name + '" '
                + ' style="' + inputWidth + '"';
            dom += triggerParamName;
            dom += '>' + optionstr + '</select>';
            dom += '</div></div>';

            $("#" + formId).append(dom);

            $('#param-' + queryParamItem.name).chosen({
                no_results_text: '没有找到',    // 当检索时没有找到匹配项时显示的提示文本
                disable_search_threshold: 10, // 10 个以下的选择项则不显示检索框
                search_contains: true         // 从任意位置开始检索
            });
        },

        /**
         * 创建文本输入框
         * @param formId
         * @param queryParamItem
         */
        createTextParam: function (formId, queryParamItem) {
            var labelWidth = QueryParameterManageMVC.Service.getLabelWidth(queryParamItem);
            var inputWidth = QueryParameterManageMVC.Service.getInputWidth(queryParamItem);
            console.log(labelWidth)
            var dom = '<div class="fl param-div mr10">';

            dom += '<div class="fl param-label" style="' + labelWidth + '">' + queryParamItem.text + '</div>';

            dom += '<div class="param-value-div fl">'
                + '<input id="param-' + queryParamItem.name + '" class="param-input" name="' + queryParamItem.name + '" style="' + inputWidth + '" />';
            +'</div>';

            dom += '<div>';
            $("#" + formId).append(dom);
        },

        /**
         * 创建日期选择框
         * @param formId
         * @param queryParamItem
         */
        createDateParam: function (formId, queryParamItem) {
            var labelWidth = QueryParameterManageMVC.Service.getLabelWidth(queryParamItem);
            var inputWidth = QueryParameterManageMVC.Service.getInputWidth(queryParamItem);

            var dom = '<div class="fl param-div mr10">';

            dom += '<div class="fl param-label" style="' + labelWidth + '">' + queryParamItem.text + '</div>';

            dom += '<div class="param-value-div fl">'
                + '<input id="param-' + queryParamItem.name + '" class="jeinput param-input" name="' + queryParamItem.name + '" value="' + queryParamItem.defaultValue + '" style="' + inputWidth + '" />';
            +'</div>';

            dom += '<div>';
            $("#" + formId).append(dom);

            //常规选择
            jeDate("#param-" + queryParamItem.name, {
                //festival:false,
                //multiPane:false,
                //isYes:false,
                format: queryParamItem.dateFormat
            });
        },

        /**
         * 获取文本宽度
         * @param queryParamItem
         * @returns {string}
         */
        getLabelWidth: function (queryParamItem) {
            var labelWith = '';
            if (queryParamItem.textWidth && queryParamItem.textWidth > 40) {
                labelWith += 'width:' + queryParamItem.textWidth + 'px;';
            }
            return labelWith;
        },

        /**
         * 获取输入框宽度
         * @param queryParamItem
         * @returns {string}
         */
        getInputWidth: function (queryParamItem) {
            var inputWidth = '';
            if (queryParamItem.nameWidth && queryParamItem.nameWidth > 60) {
                inputWidth += 'width:' + queryParamItem.nameWidth + 'px;';
            }
            return inputWidth;
        },
        /**
         * 获取表单的值
         * @param jqFormObj  jquery表单对象
         * @param isRejectEmptyStr    当值为空字符串是否剔除
         */
        getQueryParams: function (jqFormObj, isRejectEmptyStr) {
            var params = jqFormObj.serializeObject();
            if (isRejectEmptyStr) {
                var rp = {};
                for (var key in params) {
                    if (params[key]) {
                        rp[key] = params[key];
                    }
                }
                return rp;
            }
            return params;
        }
    }
}
