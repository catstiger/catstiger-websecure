package com.github.catstiger.websecure.cfg;

/**
 * 某些类，需要用到配置信息，{@code SecurityConfigurable}接口为这些类提供一致的实现规范
 * 
 * @author leesam
 *
 */
public interface SecurityConfigurable {
  SecurityProperties getCfg();

  void setCfg(SecurityProperties cfg);
}
