package com.ordering.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 *  "createTime": 1623715607000,
 *  "updateTime": 1623715607
 *  前端显示的时间 和 数据库的时间   类型不一样；  数据库的时间是Date类型的，而前端显示出来的时间多了三个零 Long类型
 */
public class Date2LongSerializer extends JsonSerializer<Date> { //继承JsonSerializer接口 重新里面的方法serialize()

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeNumber(date.getTime() / 1000);
    }
}
