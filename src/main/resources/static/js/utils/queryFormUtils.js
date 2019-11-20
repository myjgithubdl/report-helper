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
        var html='<div class="fl mt10 mr20 br-1-s-e6e6e6">'+
            '   <div class="fl f14 bg-FBFBFB br-r-1-s-e6e6e6 tc" style="'+labelStyle+'">'+queryParam.text+'</div>'+
            '   <div class="fl">'+
            '       <input type="text" readonly class="br-none pl5" name="'+queryParam.name+'" value="'+queryParam.value+'" style="'+valueStyle+'"/>'+
            '   </div>'+
            '</div>';
        $("#"+formId).append(html);

        var format=queryParam.dateFormat.toLocaleLowerCase();
        var op={format:format};

        queryFormUtils.initDateInput($("#"+formId).find("input[name='"+queryParam.name+"']").get(0),op );

        if(queryParam.value && format=='yyyymmdd' && queryParam.value.length==8){//解决yyyymmdd显示的时候1899问题
            var date=new Date(queryParam.value.substring(0,4),parseInt(queryParam.value.substring(4,6)-1),queryParam.value.substring(6))
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
            startView:2,
            minView:2,
            forceParse:0,
            format:'yyyy-mm-dd'
        };
        if(options){
            _.each(options,function (v,k) {
                op[k]=v;
            })
        }
        $(jqObj).datetimepicker(op)
        //解决yyyymmdd显示的时候1899问题
        /*$(jqObj).datetimepicker(op).on('show',function () {
            var val=$(this).val();
            if(val && val.length==8){
                var date=new Date(val.substring(0,4),parseInt(val.substring(4,6)-1),val.substring(6))
                $(this).datetimepicker('update',date)
            }
        });*/
    }
    
}