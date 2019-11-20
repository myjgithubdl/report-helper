/**
 * Created by bdf on 2019/10/11.
 */
var CommonUtils = {
    /**
     * 限制输入框只能为整数
     * 在输入框上添加事件 onkeyup="CommonUtils.checkInteger(this,3);"
     * @param obj   输入框对象
     * @param integerLength  整数长度
     * @param isAllowNegative   是否允许负数
     */
    checkInteger:function(obj,integerLength,isAllowNegative){
        if(!obj) return;
        var value=$.trim($(obj).val());
        if(value == ''){
            return $(obj).val('');
        }
        var negativeFlag=value.substring(0,1) == '-' ? '-':'' ;//是否是负数
        if(!isAllowNegative){
            negativeFlag='';
        }
        if(negativeFlag)
            value=value.substring(1);

        if(value!="" && !CommonUtils.isNumber(value)){
            value=value.substring(0,value.length-1);
            $(obj).val(negativeFlag+value);
            if(!CommonUtils.isNumber(value)){//再次检查，如果不符合数字，继续循环
                CommonUtils.checkInteger(obj);
            }
            return;
        }
        //检验长度
        if(integerLength && integerLength>0){
            if(value.length>integerLength){
                $(obj).val(negativeFlag+value.substring(0,integerLength));
            }
        }
    },
    /**
     * 校验是否是数字
     * @param num
     * @returns {boolean}
     */
    isNumber:function (num) {
        var numExp = /^[0-9]\d*$/; //数字正则表达式
        var reg = new RegExp(numExp);
        return reg.test(num);
    },
    /**
     * 校验是否是整数
     * @param num
     * @returns {boolean}
     */
    isInteger:function (num) {//
        var numExp =new RegExp("^-?\\d+$"); //整数
        var reg = new RegExp(numExp);
        return reg.test(num);
    },
    /**
     * 产生4为随机数
     * @returns {string}
     * @constructor
     */
    S4: function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    },
    /**
     * 获取UUID
     * @param fornum
     * @returns {*}
     */
    getUUID: function (fornum) {
        if (!fornum || fornum < 0)
            fornum = 1;
        var uuid = CommonUtils.S4();
        var forIndex = 1
        while (forIndex < fornum) {
            uuid += '-' + CommonUtils.S4();
            forIndex ++;
        }
        return uuid;
    },
}