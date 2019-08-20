package com.github.catstiger.websecure.password;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.modules.staff.model.Staff;
import com.github.catstiger.modules.staff.service.StaffService;
import com.github.catstiger.sms.service.SmsVerifyCodeService;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.UserService;
import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordService {
  private static Logger logger = LoggerFactory.getLogger(PasswordService.class);

  @Autowired
  private UserService userService;

  @Autowired
  private StaffService staffService;

  @Autowired
  private SmsVerifyCodeService smsVerifyCodeService;

  /**
   * 发送身份验证短信
   * 
   * @param mobile 手机号码，如果在users和staff都不存在，则报异常
   */
  public void sendVerifyCode(String mobile) {
    Preconditions.checkArgument(StringUtils.isNotBlank(mobile));

    String username = this.usernameByMobile(mobile);
    if (username == null) {
      throw Exceptions.unchecked("手机号码未注册");
    }

    smsVerifyCodeService.sendIdentityVerifyCode(mobile);
  }

  /**
   * 修改密码，如果验证码错误，则抛出异常
   */
  @Transactional
  public void changePassword(String mobile, String code, String pwd, String pwd2) {
    Preconditions.checkArgument(StringUtils.isNotBlank(mobile), "手机号码不可为空");
    Preconditions.checkArgument(StringUtils.isNotBlank(code), "验证码不可为空");
    Preconditions.checkArgument(StringUtils.isNotBlank(pwd), "密码不可为空");
    Preconditions.checkArgument(StringUtils.isNotBlank(pwd2), "确认密码不可为空");

    if (pwd.length() < 6 || pwd.length() > 32) {
      throw Exceptions.unchecked("密码长度应在6 - 32英文字符之间。");
    }

    if (!StringUtils.equals(pwd, pwd2)) {
      throw Exceptions.unchecked("两次输入的密码不同");
    }

    if (!smsVerifyCodeService.isCorrect(mobile, code)) {
      throw Exceptions.unchecked("验证码错误");
    }

    String username = this.usernameByMobile(mobile);
    userService.changePassword(username, pwd);
  }

  /**
   * 根据手机号码找到对应的用户名，首先判断Username是否为给定的mobile，否则判断Staff的phone是否为mobile
   */
  public String usernameByMobile(String mobile) {
    Preconditions.checkArgument(StringUtils.isNotBlank(mobile), "手机号码不可为空");

    String username = null;
    User user = userService.byNameQuietly(mobile); //用户名就是手机号？
    if (user == null) {
      Staff staff = staffService.byPhone(mobile); //用户名不是手机号
      if (staff != null) {
        logger.info("根据Staff查询 {}", mobile);
        username = staff.getUsername();
      }
    } else {
      logger.info("根据User查询 {}", mobile);
      username = user.getUsername();
    }

    return username;
  }

}
