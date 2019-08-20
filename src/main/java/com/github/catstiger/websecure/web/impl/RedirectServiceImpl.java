package com.github.catstiger.websecure.web.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.common.web.WebUtil;
import com.github.catstiger.websecure.web.RedirectService;

public class RedirectServiceImpl implements RedirectService {
  private static Logger logger = LoggerFactory.getLogger(RedirectServiceImpl.class);

  /**
   * 重定向
   */
  public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
    String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
    redirectUrl = response.encodeRedirectURL(redirectUrl);
    logger.debug("Redirect to {}", redirectUrl);

    response.sendRedirect(redirectUrl);
  }

  private String calculateRedirectUrl(String contextPath, String url) {
    if (!WebUtil.isAbsoluteUrl(url)) {
      return contextPath + url;
    }
    return url;

  }
}
