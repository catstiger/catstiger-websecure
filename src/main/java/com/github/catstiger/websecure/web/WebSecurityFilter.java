package com.github.catstiger.websecure.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.common.web.WebUtil;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.FreeAccessService;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.subject.SubjectBuilder;
import com.github.catstiger.websecure.web.impl.HttpSessionRequestKeeper;

public class WebSecurityFilter extends OncePerRequestFilter {
  private static Logger logger = LoggerFactory.getLogger(WebSecurityFilter.class);

  private SubjectBuilder subjectBuilder;
  private ApplicationContext appContext;
  private RequestKeeper requestKeeper = new HttpSessionRequestKeeper();
  private FreeAccessService freeAccessService;

  @Override
  protected void initFilterBean() throws ServletException {
    logger.info("初始化WebSecurityFilter.");
    ServletContext servletContext = getServletContext();
    appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    if (appContext == null) {
      throw new IllegalStateException("没有正确配置WebSecurityFilter和Spring");
    }
    subjectBuilder = appContext.getBean(SubjectBuilder.class);
    freeAccessService = appContext.getBean(FreeAccessService.class);
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    // URI,删除参数、Context Path
    String uri = getRequestUri(request);

    try {
      // 获取Subject
      Subject subject = subjectBuilder.build();
      SubjectHolder.setSubject(subject);

      // 静态资源直接访问
      if (freeAccessService.isStatic(uri)) {
        chain.doFilter(request, response);
        return;
      }

      if (!subject.isPermitted(uri)) {
        accessDenied(request, response);
      } else {
        chain.doFilter(request, response); // 如果允许访问，则继续
      }
    } finally {
      SubjectHolder.clear();
    }
  }

  /**
   * 处理访问被拒之后的事情。
   * <p/>
   * <ul>
   * <li>如果是AJAX访问，则向Response对象Write一个Model，指明访问错误。</li>
   * <li>否则重新定向到403页面</li>
   * </ul>
   */
  private void accessDenied(HttpServletRequest request, HttpServletResponse response) {
    requestKeeper.set(request, response); // 保存不能访问的请求
    if (!WebUtil.isJsonRequest(request)) { // 不是JSON访问
      try {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, SecureConstants.MSG_ACCESS_DENIED);
      } catch (IOException e) {
        e.printStackTrace();
        throw Exceptions.unchecked(e); // to 500
      }
    } else {
      SecurityObjectsModel model = new SecurityObjectsModel(SecureConstants.MSG_ACCESS_DENIED, false);
      try {
        response.getWriter().write(JSON.toJSONString(model));
      } catch (IOException e) {
        e.printStackTrace();
        throw Exceptions.unchecked(e); // to 500
      }
    }
  }

  private static String getRequestUri(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri == null) {
      logger.warn("URI is null !");
      return StringUtils.EMPTY;
    }
    uri = StringUtils.removeEnd(uri, request.getContextPath());

    return uri;
  }

  @Override
  public void destroy() {

  }

}
