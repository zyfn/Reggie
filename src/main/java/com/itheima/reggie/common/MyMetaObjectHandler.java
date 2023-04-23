package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据填充规则
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue( "createTime", LocalDateTime.now()) ;
        metaObject.setValue ( "updateTime", LocalDateTime. now() ) ;
        metaObject.setValue(  "createUser" , BaseContext.getCurrentId());
        metaObject.setValue ("updateUser" , BaseContext.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        long id =Thread.currentThread().getId();
        log.info("线程id为{}",id);

        metaObject.setValue ( "updateTime", LocalDateTime. now() ) ;
        metaObject.setValue ("updateUser" , BaseContext.getCurrentId());
    }
}
