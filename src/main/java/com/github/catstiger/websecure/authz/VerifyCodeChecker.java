package com.github.catstiger.websecure.authz;

/**
 * 验证码登录检查，如果需要手机短信验证码登录，则必须实现这个接口。
 * @author samlee
 */
public interface VerifyCodeChecker {
  /**
   * 检查验证码是否正确
   * @param mobile 手机号码
   * @param verifyCode 用户提交的验证码
   * @return 如果验证码正确，返回{@code true}，否则返回{@code true}
   */
  boolean isCorrect(String mobile, String verifyCode);
}
