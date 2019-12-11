package com.github.catstiger.websecure.authz;

import java.io.Serializable;

public interface AuthzToken extends Serializable {

  /**
   * 在认证的过程中，返回账户的标识
   */
  String getPrincipal();
  
  /**
   * 获取用户手机
   * @return
   */
  String getMobile();
  
  
  /**
   * 返回在认证过程中提交的credentials
   */
  String getCredentials();

  /**
   * 获取登录验证码
   * @return
   */
  String getVerifyCode();
  
  /**
   * 返回进行登录操作的客户端的host name。
   * <p/>
   * 在Web环境中，这个操作等同于 {@code ServletRequest.getRemoteHost()} 的值.
   */
  String getHost();
  
  /**
   * 返回Captcha（如果有）
   * @return
   */
  String getCaptcha();
  
  /**
   * 如果当前提交的请求要求实现RememberMe，则返回{@code true}，否则返回{@code false}
   */
  boolean isRememberMe();
  
  /**
   * 是否有验证码
   * @return
   */
  boolean hasVerifyCode();
  
  /**
   * 是否有Captcha
   * @return
   */
  boolean hasCaptcha();
}
