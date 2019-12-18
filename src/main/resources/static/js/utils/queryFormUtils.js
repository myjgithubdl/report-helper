/**
 * Created by bdf on 2019/10/21.
 */
/**
 * 查询表单
 * @type {{createQueryForm: queryFormUtils.createQueryForm}}
 */
var queryFormUtils={
    /**
     * 在指定的form下创建查询表单
     * @param formId
     * @param queryParams
     */
    createQueryForm:function (formId,queryParams) {
        if(formId && queryParams.length > 0 ){
            _.each(queryParams,function (queryParam) {
                var type = queryParam.formElement;
                switch(type){
                    case 'text'://文本输入框
                        queryFormUtils.createTextDom(formId,queryParam);
                        break;
                    case 'textbox'://文本输入框
                        queryFormUtils.createTextDom(formId,queryParam);
                        break;
                    case 'select'://选择框
                        queryFormUtils.createComboboxDom(formId,queryParam);
                        break;
                    case 'selectMul'://选择框
                        queryFormUtils.createComboboxDom(formId,queryParam);
                        break;
                    case 'checkbox'://复选框
                        queryFormUtils.createCheckboxDom(formId,queryParam);
                        break;
                    case 'date'://日期选择框
                        queryFormUtils.createDateboxDom(formId,queryParam);
                        break;
                    default:
                        console.log("未处理类型："+type);
                        break;
                }
            })
        }
    },
    /**
     * 在指定表单下创建文本框参数
     * @param formId
     * @param queryParam
     */
    createTextDom:function (formId,queryParam) {
        var labelStyle=queryFormUtils.getLabelStyle(queryParam);
        var valueStyle=queryFormUtils.getValueStyle(queryParam);
        var html='<div class="fl mt10 mr20 br-1-s-e6e6e6">'+
            '   <div class="fl f14 bg-FBFBFB br-r-1-s-e6e6e6 tc" style="'+labelStyle+'">'+queryParam.text+'</div>'+
            '   <div class="fl">'+
            '       <input class="br-none pl5" name="'+queryParam.name+'" value="'+queryParam.defaultValue+'" style="'+valueStyle+'"/>'+
            '   </div>'+
            '</div>';
        $("#"+formId).append(html);
    },
    /**
     * 在指定表单下创建选择框参数
     * @param formId
     * @param queryParam
     */
    createComboboxDom:function (formId,queryParam) {
        var labelStyle=queryFormUtils.getLabelStyle(queryParam);
        var valueStyle=queryFormUtils.getValueStyle(queryParam);
        var values=queryParam.optionList;
        var options='';
        _.each(values,function (value) {
            var selected=value.selected?' selected ':'';
            options +='<option  value="'+value.value+'"'+selected+'>'+value.text+'</option>';
        })

        var multiple=queryParam.multiple?' multiple="" ':'';

        var html='<div class="fl mt10 mr20 br-1-s-e6e6e6">'+
            '   <div class="fl f14 bg-FBFBFB br-r-1-s-e6e6e6 tc" style="'+labelStyle+'">'+queryParam.text+'</div>'+
            '   <div class="fl">'+
            '       <select class="br-none pl5" name="'+queryParam.name+'" style="'+valueStyle+'"'+multiple+'>'+options+'</select>'+
            '   </div>'+
            '</div>';
        $("#"+formId).append(html);

        var selectObj=$("#"+formId).find("select[name='"+queryParam.name+"']");
        if(queryParam.multiple){//多选
            selectObj.chosen({
                disable_search:true
            })
        }
        //校验值改变时是否会引起其他选择框值得变化
        if(queryParam.triggerParamName){
            selectObj.on('change',function () {
                var thisVal=$(this).val();
                console.log(thisVal)
                PublishReportMVC.Controller.triggerParamReloadSelectOption(formId,queryParam.triggerParamName,thisVal)
            })
        }
    },
    /**
     * 在form表单下创建时间选择框
     * @param formId
     * @param queryParam
     */
    createDateboxDom:function (formId,queryParam) {
        var labelStyle=queryFormUtils.getLabelStyle(queryParam);
        var valueStyle=queryFormUtils.getValueStyle(queryParam);
        var value=queryParam.defaultValue ? queryParam.defaultValue : '';
        var html='<div class="fl mt10 mr20 br-1-s-e6e6e6">'+
            '   <div class="fl f14 bg-FBFBFB br-r-1-s-e6e6e6 tc" style="'+labelStyle+'">'+queryParam.text+'</div>'+
            '   <div class="fl">'+
            '       <input type="text" readonly class="br-none pl5" name="'+queryParam.name+'" value="'+value+'" style="'+valueStyle+'"/>'+
            '   </div>'+
            '</div>';
        $("#"+formId).append(html);

        var format=queryParam.dateFormat.toLocaleLowerCase();
        var op={format:format};

        queryFormUtils.initDateInput($("#"+formId).find("input[name='"+queryParam.name+"']").get(0),op );

        if(queryParam.value && format=='yyyymmdd' && value.length==8){//解决yyyymmdd显示的时候1899问题
            var date=new Date(value.substring(0,4),parseInt(value.substring(4,6)-1),value.substring(6))
            $($("#"+formId).find("input[name='"+queryParam.name+"']").get(0)).datetimepicker('update',date)
        }
    },
    /**
     * 获取参数文字样式
     * @param queryParam
     * @returns {string}
     */
    getLabelStyle:function (queryParam) {
        var width=queryParam.textWidth?queryParam.textWidth:80;
        var height=queryParam.height?queryParam.height:26;
        var lineHeight=height;
        var labelStyle='width:'+width+'px;height:'+height+'px;line-height: '+lineHeight+'px;';
        return labelStyle;
    },
    /**
     * 获取参数输入框样式
     * @param queryParam
     * @returns {string}
     */
    getValueStyle:function (queryParam) {
        var width=queryParam.nameWidth?queryParam.nameWidth:120;
        var height=(queryParam.height?queryParam.height:26)-1;
        var valueStyle='width:'+width+'px;height:'+height+'px;';
        return valueStyle;
    },
    initDateInput:function (jqObj,options) {
        var op={
            language:'zh-CN',
            weekStart:1,
            todayBtn:1,
            autoclose:1,
            todayHighlight:1,
            startView:3,
            minView:3, //4:10年内，3：显示1至12月，2：一月内，1:1天内，0:1小时内
            forceParse:1,//强制分析
            format:'yyyy-mm-dd'
        };
        if(options){
            _.each(options,function (v,k) {
                op[k]=v;
            })
        }
        if(op.format == 'yyyy-mm-dd' || op.format == 'yyyymmdd'){
            op["startView"]=2;
            op["minView"]=2;
        }else if(op.format == 'yyyy-mm' || op.format == 'yyyymm'){
            op["startView"]=3;
            op["minView"]=3;
        }else if(op.format == 'yyyy' ){
            op["startView"]=4;
            op["minView"]=4;
        }

        $(jqObj).datetimepicker(op);

        //解决yyyymmdd yyyymm 显示的时候1899问题
        $(jqObj).datetimepicker(op).on('show',function () {
            var val=$(this).val();
            if(val){
                if(val.length==8){
                    var date=new Date(val.substring(0,4),parseInt(val.substring(4,6)-1),val.substring(6))
                    $(this).datetimepicker('update',date)
                }else if(val.length==6){
                    var date=new Date(val.substring(0,4),parseInt(val.substring(4,6)-1),1)
                    $(this).datetimepicker('update',date)
                }
            }

        });
    }

}