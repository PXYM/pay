package com.seinfish.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PayCallbackController {
    /**
     * 支付成功返回地址接口
     */
    @RequestMapping("/home")
    public String home(){
        return "index";
    }
}
