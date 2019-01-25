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
            //不适用异步，否则可能会导致参数未加载就渲染数据表格
            $.ajax({
                type: "POST",
                url: QueryParameterManageMVC.URLs.QueryReportParameter.url + uid,
                data: {},
                async:false,
                success: function(result){
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
                }
            });

            return

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
        },
        /**
         * 根据xMetaColumns渲染多个单选框(单选)
         * @param xDivId div的id
         * @param xMetaColumns  元数据列
         * @param inputName 输入框的名称
         */
        createRadioInputs: function (xDivId, xMetaColumns, inputName) {
            _.each(xMetaColumns, function (metaColumns) {
                QueryParameterManageMVC.Controller.createRadioInput(xDivId, inputName, metaColumns.name, metaColumns.text);
            });
        },
        /**
         * 根据div的id创建单(单选)
         * @param xDivId
         * @param inputName 输入框的name属性值
         * @param inputValue    输入框的value属性值
         * @param inputText 输入框的text属性值
         */
        createRadioInput: function (xDivId, inputName, inputValue, inputText) {
            var label = '<label class="radio-inline ml10 pt5"><input type="radio" name="' + inputName + '" value="' + inputValue + '">' + inputText + '</label>';
            $("#" + xDivId).append(label);
        },
        /**
         * 根据div的id和布局列渲染x轴(多选)
         * @param xDivId
         * @param xMetaColumns
         */
        createCheckboxOptions: function (xDivId, xMetaColumns, inputName) {
            _.each(xMetaColumns, function (metaColumns) {
                QueryParameterManageMVC.Controller.createCheckboxOption(xDivId, inputName, metaColumns.name, metaColumns.text);
            });
        },
        /**
         * 根据div的id渲染复选框
         * @param xDivId    div的id
         * @param inputName 输入框的name属性值
         * @param inputValue    输入框的value属性值
         * @param inputText 输入框的text属性值
         */
        createCheckboxOption: function (xDivId, inputName, inputValue, inputText) {
            var label = '<label class="checkbox-inline ml10 pt5"><input type="checkbox" name="' + inputName + '" value="' + inputValue + '">' + inputText + '</label>';
            $("#" + xDivId).append(label);
        },


        /**
         * 根据div的id和布局列渲染维度轴
         * @param xDivId
         * @param xMetaColumns
         */
        createSelects: function (legendDivId, legendMetaColumns) {
            _.each(legendMetaColumns, function (metaColumn) {
                var dom = '<div class="fl mr10 legend-item"><div class="fl legend-label">' + metaColumn.text + '</div>';
                dom += '<div class="legend-value-div fl">';
                dom += '<select class="chosen-select form-control legend-value-select " id="legend-' + metaColumn.name + '" name="' + metaColumn.name + '" multiple="" '
                    + ' style="width:120px;"';
                dom += '></select>';
                dom += '</div></div>';

                //var label='<label class="radio-inline ml10 pt5"><input type="radio" name="legendName">'+metaColumns.text+'</label>';
                $("#" + legendDivId).append(dom);

                $('#legend-' + metaColumn.name).chosen({
                    no_results_text: '没有找到',    // 当检索时没有找到匹配项时显示的提示文本
                    disable_search_threshold: 10, // 10 个以下的选择项则不显示检索框
                    search_contains: true         // 从任意位置开始检索
                });

            });
        },
        /**
         * 根据div的id和布局列渲染Y轴
         * @param yDivId
         * @param yMetaColumns
         */
        setChartYOption: function (yDivId, yMetaColumns) {
            _.each(yMetaColumns, function (metaColumns) {
                var label = '<label class="radio-inline ml10 pt5"><input type="radio" name="yName" value="' + metaColumns.name + '">' + metaColumns.text + '</label>';
                $("#" + yDivId).append(label);
            });
        },

        /**
         * 设置维度列的值
         * @param legendMetaColumns  所有的维度列
         * @param allData   数据
         */
        setLegendMetaColumnsValue: function (legendMetaColumns, allData) {
            _.each(legendMetaColumns, function (metaColumn) {
                $("#legend-" + metaColumn.name).html('');
                var optionstr = '';

                _.each(_.uniq(_.map(allData, metaColumn.name)), function (i) {
                    optionstr += '<option value="' + i + '">' + i + '</option>';
                });

                $("#legend-" + metaColumn.name).html(optionstr);

                $('#legend-' + metaColumn.name).trigger('chosen:updated');

            });
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
