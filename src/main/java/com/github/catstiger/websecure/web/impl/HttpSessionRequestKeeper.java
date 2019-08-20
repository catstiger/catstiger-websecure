package com.github.catstiger.websecure.web.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.catstiger.websecure.web.RequestKeeper;
import com.github.catstiger.websecure.web.RequestModel;

public class HttpSessionRequestKeeper implements RequestKeeper {
  static final String SAVED_REQUEST = "CATSTIGER_WEB_SECURITY_SAVED_REQUEST";

  @Override
  public void set(HttpServletRequest request, HttpServletResponse response) {
    if (request.getSession(true) != null) {
      RequestModel requestModel = new RequestModel(request);
      request.getSession().setAttribute(SAVED_REQUEST, requestModel);
    }
  }

  @Override
  public RequestModel get(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(true);

    if (session != null) {
      return (RequestModel) session.getAttribute(SAVED_REQUEST);
    }

    return null;
  }

  @Override
  public void remove(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(true);

    if (session != null) {
      session.removeAttribute(SAVED_REQUEST);
    }
  }

}
