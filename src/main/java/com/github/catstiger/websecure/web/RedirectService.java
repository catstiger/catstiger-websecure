package com.github.catstiger.websecure.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RedirectService {
  /**
   * Performs a redirect to the supplied URL
   * @param request the current request
   * @param response the response to redirect
   * @param url the target URL to redirect to, for example "/login"
   */
  void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException;
}
