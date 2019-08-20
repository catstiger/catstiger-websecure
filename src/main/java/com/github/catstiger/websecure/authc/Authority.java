package com.github.catstiger.websecure.authc;

import java.io.Serializable;
import java.util.Set;

public interface Authority extends Serializable {
  /**
   * 返回当前Authority的名字，通常，一个{@code String}就可以表达一个Authority。一个Subject/User可以拥有多个Authority。
   * 而一个Authority可以拥有多个{@code Permission}
   * 
   * @return 一个可以表达Authority的{@code String}，不可以为Null
   */
  String getName();
  
  /**
   * 返回Authority可以访问的{@code Permission},一个Authority可以访问/操作0~多个{@code Permission}
   * @return 返回Authority可以访问的{@code Permission}，如果没有任何可以访问的{@code Permission}，返回{@code Collections#emptySet()}
   */
  Set<Permission> getPermissions();
}
