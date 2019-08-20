package com.github.catstiger.websecure.password.controller;

import com.github.catstiger.common.web.controller.ModulesController;
import com.github.catstiger.websecure.password.PasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/password_service")

/**
 * 密码服务，修改密码等
 */
public class PasswordController extends ModulesController {
  @Autowired
  private PasswordService passwordService;

  /**
   * 发送验证码
   */
  @RequestMapping("/send_verify_code")
  @ResponseBody
  public void sendVerifyCode(@RequestParam(name = "mobile") String mobile) {
    try {
      passwordService.sendVerifyCode(mobile);
      renderSuccess(null);
    } catch (Exception e) {
      renderError(e.getMessage());
    }
  }

  /**
   * 修改密码
   */
  @RequestMapping("/change_password")
  @ResponseBody
  public void changePassword(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "code") String code,
      @RequestParam(name = "pwd") String pwd, @RequestParam(name = "pwd2") String pwd2) {
    try {
      passwordService.changePassword(mobile, code, pwd, pwd2);
      renderSuccess(null);
    } catch (Exception e) {
      logger.error(e.getMessage());
      renderError(e.getMessage());
    }
  }

  /**
   * 判断手机号是否存在，如果不存在
   */
  @RequestMapping("/mobile_exists")
  @ResponseBody
  public void mobileExists(@RequestParam(name = "mobile") String mobile) {
    try {
      String username = passwordService.usernameByMobile(mobile);
      renderSuccess(username != null);
    } catch (Exception e) {
      logger.error(e.getMessage());
      renderError(e.getMessage());
    }
  }
}
