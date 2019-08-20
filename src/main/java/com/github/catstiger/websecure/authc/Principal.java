package com.github.catstiger.websecure.authc;

import java.io.Serializable;
import java.util.Set;

public interface Principal extends java.security.Principal, Serializable {
  /**
   * 返回当前Principal所具有的{@code Authority}，如果没有任何授权，则返回{@code Collections#emptySet()}
   * @return 当前Principal所具有的{@code Authority}
   */
  Set<Authority> getAuthorities();
}
