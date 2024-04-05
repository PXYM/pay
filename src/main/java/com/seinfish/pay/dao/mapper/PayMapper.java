package com.seinfish.pay.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seinfish.pay.dao.entity.PayDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付持久层
 */
@Mapper
public interface PayMapper extends BaseMapper<PayDO> {
}
