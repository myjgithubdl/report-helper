package com.myron.reporthelper.resp;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult implements Serializable {

    /**
     * 返回状态码
     */
    private String respCode;

    /**
     * 返回表述文字
     */
    private String respDesc;

    /**
     * 返回数据
     */
    private Object respData;

    /**
     * @param respCode
     * @param respDesc
     * @param respData
     * @return
     */
    public static ResponseResult build(final String respCode, final String respDesc, final Object respData) {
        return new ResponseResult(respCode, respDesc, respData);
    }

    /**
     * @param respCode
     * @param respDesc
     * @param respData
     * @return
     */
    public static String buildToJSONString(final String respCode, final String respDesc, final Object respData) {
        return JSONObject.toJSONString(new ResponseResult(respCode, respDesc, respData));
    }


    /**
     * 返回成功
     *
     * @return
     */
    public static ResponseResult success() {
        return build(RespCodeConstant.SUCCESS, RespCodeConstant.SUCCESS_DESC, null);
    }

    /**
     * @return
     */
    public static String successToJSONString() {
        return JSONObject.toJSONString(build(RespCodeConstant.SUCCESS, RespCodeConstant.SUCCESS_DESC, null));
    }

    /**
     * 返回成功
     *
     * @param respDesc
     * @return
     */
    public static ResponseResult success(final String respDesc) {
        return build(RespCodeConstant.SUCCESS, respDesc, null);
    }

    /**
     * 返回成功
     *
     * @param respData
     * @return
     */
    public static ResponseResult success(final Object respData) {
        return build(RespCodeConstant.SUCCESS, RespCodeConstant.SUCCESS_DESC, respData);
    }

    /**
     * @param respData
     * @return
     */
    public static String successToJSONString(final Object respData) {
        return JSONObject.toJSONString(build(RespCodeConstant.SUCCESS, RespCodeConstant.SUCCESS_DESC, respData));
    }

    /**
     * 返回成功
     *
     * @param respData
     * @return
     */
    public static ResponseResult success(final String respDesc, final Object respData) {
        return build(RespCodeConstant.SUCCESS, respDesc, respData);
    }

    /**
     * @param respDesc
     * @param respData
     * @return
     */
    public static String successToJSONString(final String respDesc, final Object respData) {
        ResponseResult ResponseResult = build(RespCodeConstant.SUCCESS, respDesc, respData);
        return JSONObject.toJSONString(ResponseResult);
    }


    /**
     * 返回失败
     *
     * @return
     */
    public static ResponseResult error() {
        return build(RespCodeConstant.ERROR, RespCodeConstant.ERROR_DESC, null);
    }

    /**
     * @return
     */
    public static String errorToJSONString() {
        ResponseResult ResponseResult = build(RespCodeConstant.ERROR, RespCodeConstant.ERROR_DESC, null);
        return JSONObject.toJSONString(ResponseResult);
    }

    /**
     * 返回失败
     *
     * @param respDesc
     * @return
     */
    public static ResponseResult error(final String respDesc) {
        return build(RespCodeConstant.ERROR, respDesc, null);
    }

    /**
     * 返回失败
     *
     * @param respData
     * @return
     */
    public static ResponseResult error(final Object respData) {
        return build(RespCodeConstant.ERROR, RespCodeConstant.ERROR_DESC, respData);
    }

    /**
     * @param respData
     * @return
     */
    public static String errorToJSONString(final Object respData) {
        ResponseResult ResponseResult = build(RespCodeConstant.ERROR, RespCodeConstant.ERROR_DESC, respData);
        return JSONObject.toJSONString(ResponseResult);
    }

    /**
     * 返回失败
     *
     * @param respData
     * @return
     */
    public static ResponseResult error(final String respDesc, final Object respData) {
        return build(RespCodeConstant.ERROR, respDesc, respData);
    }

    /**
     * 返回失败
     *
     * @param respData
     * @return
     */
    public static ResponseResult error(final String respCode, final String respDesc, final Object respData) {
        return build(respCode, respDesc, respData);
    }

    /**
     * @param respDesc
     * @param respData
     * @return
     */
    public static String errorToJSONString(final String respDesc, final Object respData) {
        ResponseResult ResponseResult = build(RespCodeConstant.ERROR, respDesc, respData);
        return JSONObject.toJSONString(ResponseResult);
    }


    /**
     * RespData转为list
     *
     * @return
     */
    public static List<Map<String, Object>> getDataList(ResponseResult ResponseResult) {
        if (ResponseResult.getRespData() != null) {
            List<Map<String, Object>> respData = (List) ResponseResult.getRespData();
            return respData;
        }
        return null;
    }


    /**
     * 是否成功
     *
     * @return
     */
    public static boolean isSuccess(ResponseResult ResponseResult) {
        return RespCodeConstant.SUCCESS.equals(ResponseResult.getRespCode());
    }
}
