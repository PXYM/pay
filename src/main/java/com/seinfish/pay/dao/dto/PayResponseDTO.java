package com.seinfish.pay.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付返回实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayResponseDTO {
  /**
   * 调用支付返回信息
   */
  private String body;
}
