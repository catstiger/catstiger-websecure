package com.github.catstiger.websecure.authc.impl;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import com.github.catstiger.websecure.SecureConstants;

@Component
public class ConfigurableResourcesKeyGenerator implements KeyGenerator {

  /**
   * 全局唯一，只需返回一个固定的String即可
   */
  @Override
  public Object generate(Object target, Method method, Object... params) {
    return SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES;
  }

}
