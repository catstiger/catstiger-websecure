package com.github.catstiger.websecure.authz;

public interface CaptchaChecker {
  /**
   * 判断Captcha是否正确
   * @param captcha
   * @return
   */
  boolean isCorrect(String captcha);
}
