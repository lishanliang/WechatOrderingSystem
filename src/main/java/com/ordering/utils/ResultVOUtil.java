package com.ordering.utils;

import com.ordering.VO.ResultVO;

/**
 * 工具包  将resultVO创建好并返回
 */
public class ResultVOUtil {

    public static ResultVO success(Object object) {//传入正确的productVO
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static ResultVO success() {
        return success(null);
    } //不传入productVO 默认的success()方法

    public static ResultVO error(Integer code, String msg) {//code为1 msg为"错位" data就直接为null
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }
}
