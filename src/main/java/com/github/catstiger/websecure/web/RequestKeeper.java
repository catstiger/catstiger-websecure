package com.github.catstiger.websecure.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestKeeper {
  /**
   * 缓存当前Reqeust（被拒的），一旦认证成功，可以重新访问
   * @param request 被保存的reqeust
   */
  void set(HttpServletRequest request, HttpServletResponse response);

  /**
   * 返回保存的RequestModel,并且清除出缓存
   * @param request <strong>当前</strong> request
   * @return 返回之前保存的RequestModel，如果没有，返回{@code null}
   */
  RequestModel get(HttpServletRequest request, HttpServletResponse response);

  /**
   * 将Request从缓存移除
   */
  void remove(HttpServletRequest request, HttpServletResponse response);
}
