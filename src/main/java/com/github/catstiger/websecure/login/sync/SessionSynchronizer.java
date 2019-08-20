package com.github.catstiger.websecure.login.sync;

import com.github.catstiger.websecure.authc.Principal;

/**
 * Http Session同步器，实现者利用SessionBindingListener等机制可以实现“一个用户只能同时登录一次的需求”，
 * 也可以记录用户登录信息
 * @author leesam
 *
 */
public interface SessionSynchronizer {
  /**
   * 应用同步机制。实现者必须可以根据{@link com.github.catstiger.websecure.cfg.SecurityConfig#isRecordLogin()}记录用户登录情况
   * 并且可以根据{@link com.github.catstiger.websecure.cfg.SecurityConfig#isSyncLogin()}控制用户同步登录算法。
   * @param principal 当前执行登录\登出的Principal\User
   */
  void apply(Principal principal);
}
