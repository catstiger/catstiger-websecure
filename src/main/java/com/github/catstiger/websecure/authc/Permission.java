package com.github.catstiger.websecure.authc;

import java.io.Serializable;
import java.util.Collection;

public interface Permission extends Serializable {
  /**
   * 返回Permission的String形式
   */
  String getPermission();
  
  Collection<Authority> getAuthorities();
}
