package com.ordering.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * http请求返回的最外层对象 返回给前端view object

 */
@Data
public class ResultVO<T> implements Serializable {  //由于使用了Redis缓存，需要实现序列化（数据存入Redis是JSon格式的）

    //安装并使用插件GenerateSerialVersionUID 生成唯一的序列化ID   设置的快捷键为Ctrl+Shift+Q
    private static final long serialVersionUID = 3068837394742385883L;

    /** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体内容. */
    private T data;
}
