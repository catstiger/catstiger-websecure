package com.github.catstiger.websecure.cfg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.catstiger.websecure.password.PasswordEncoder;
import com.github.catstiger.websecure.password.impl.StandardPasswordEncoder;
import com.github.catstiger.websecure.web.WebSecurityFilter;

@Configuration
public class SecurityConfiguration {
  private static Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

  /**
   * Web安全Filter
   */
  @Bean
  public FilterRegistrationBean<WebSecurityFilter> webSecurityFilterRegistration() {
    logger.info("初始化WebSecure");
    FilterRegistrationBean<WebSecurityFilter> registration = new FilterRegistrationBean<>(new WebSecurityFilter());
    registration.addUrlPatterns("/*");
    return registration;
  }
  
  /**
   * 密码Encoder
   */
  @Bean
  PasswordEncoder passwordEncoder(@Value("${app.security.salt}") String salt) {
    StandardPasswordEncoder passwordEncoder = new StandardPasswordEncoder();
    passwordEncoder.setAlgorithm("MD5");
    passwordEncoder.setIterations(3);
    passwordEncoder.setSalt(salt);

    return passwordEncoder;
  }
}
