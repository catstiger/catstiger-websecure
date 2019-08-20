package com.github.catstiger.websecure.authc;

import com.github.catstiger.websecure.login.AccountNotFoundException;

public interface PrincipalService {
  /**
   * 根据{@code Principal#getName()}, 得到Principal, 所取得的Principal必须是一个可以登录的，不可被锁定或者禁用等。
   * @param name of the Principal
   * @return Instance of Principal, or throws {@code AccountNotFoundException} if the principal not exists;
   */
  Principal byName(String name) throws AccountNotFoundException;
}
