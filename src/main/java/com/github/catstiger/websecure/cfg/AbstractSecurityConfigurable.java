package com.github.catstiger.websecure.cfg;

import org.springframework.beans.factory.annotation.Autowired;

public class AbstractSecurityConfigurable implements SecurityConfigurable {
  private SecurityProperties cfg;

  @Override
  public SecurityProperties getCfg() {
    if (cfg == null) {
      throw new java.lang.IllegalStateException("未注入配置信息");
    }
    return cfg;
  }

  @Override
  @Autowired
  public void setCfg(SecurityProperties cfg) {
    this.cfg = cfg;
  }

}
