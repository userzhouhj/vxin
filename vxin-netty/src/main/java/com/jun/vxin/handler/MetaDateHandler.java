package com.jun.vxin.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Component
public class MetaDateHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("requestDateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    }


}
