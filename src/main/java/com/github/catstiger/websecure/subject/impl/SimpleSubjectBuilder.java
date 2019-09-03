package com.github.catstiger.websecure.subject.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.authc.FreeAccessService;
import com.github.catstiger.websecure.authz.impl.UserAuthzToken;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.subject.SubjectBuilder;
import com.github.catstiger.websecure.token.TokenStrategy;
import com.github.catstiger.websecure.token.impl.CookieTokenStorage;
import com.github.catstiger.websecure.token.impl.HeaderTokenStorage;
import com.github.catstiger.websecure.token.impl.HttpSessionTokenStorage;
import com.github.catstiger.websecure.token.impl.RequestParameterTokenStorage;
import com.github.catstiger.websecure.user.service.UserService;

@Service
public class SimpleSubjectBuilder implements SubjectBuilder {
  private static Logger logger = LoggerFactory.getLogger(SimpleSubjectBuilder.class);
  
  private HttpSessionTokenStorage httpSessionTokenStorage;
  private CookieTokenStorage cookieTokenStorage;
  private HeaderTokenStorage headerTokenStorage;
  private RequestParameterTokenStorage requestParameterTokenStorage;

  @Autowired
  private TokenStrategy tokenStrategy;
  @Autowired
  private FreeAccessService freeAccessService;
  @Autowired
  private UserService userService;
  @Autowired
  private SecurityProperties securityProperties;

  /**
   * 初始化，在初次构造SimpleSubjectBuilder的时候调用，为各个服务创建缺省的实现。
   */
  @PostConstruct
  public void initialize() {
    this.httpSessionTokenStorage = new HttpSessionTokenStorage(securityProperties);
    this.cookieTokenStorage = new CookieTokenStorage(securityProperties);
    this.headerTokenStorage = new HeaderTokenStorage(securityProperties);
    this.requestParameterTokenStorage = new RequestParameterTokenStorage(securityProperties);
  }

  @Override
  public Subject build() {
    Subject subject = buildFromSession();
    
    if (subject == null) {
      subject = buildFromSession();
    }

    if (subject == null) {
      subject = buildFromRequestCookie();
    }

    if (subject == null) {
      subject = buildFromRequestHeader();
    }

    if (subject == null) {
      subject = buildFromRequestParameter();
    }

    if (subject == null) {
      subject = AnonymousSubject.getInstance(freeAccessService);
    }

    return subject;
  }

  private Subject buildFromSession() {
    String token = httpSessionTokenStorage.get();
    if (StringUtils.isBlank(token)) {
      return null;
    }
    logger.debug("从Session中获取TOKEN {}", token);
    return token2Subject(token, false);
  }

  private Subject buildFromRequestParameter() {
    String token = requestParameterTokenStorage.get();
    if (StringUtils.isBlank(token)) {
      return null;
    }
    logger.debug("从Request中获取TOKEN {}", token);
    return token2Subject(token, false);
  }

  private Subject buildFromRequestHeader() {
    String token = headerTokenStorage.get();
    if (StringUtils.isBlank(token)) {
      return null;
    }
    logger.debug("从Header中获取TOKEN {}", token);
    return token2Subject(token, false);
  }

  private Subject buildFromRequestCookie() {
    String token = cookieTokenStorage.get();
    if (StringUtils.isBlank(token)) {
      return null;
    }
    logger.debug("从Cookie中获取TOKEN {}", token);
    return token2Subject(token, true);
  }

  private Subject token2Subject(String token, boolean isRememberMe) {
    try {
      String username = (String) tokenStrategy.verify(token);
      UserAuthzToken authzToken = new UserAuthzToken(username, StringUtils.EMPTY, isRememberMe);
      DelegatingSubject subject = new DelegatingSubject(userService, authzToken);
      subject.setRememberMe(isRememberMe);
      subject.setAuthenticated(true);
      subject.setHost(WebObjectsHolder.getRequest().getRemoteHost());
      return subject;
    } catch (Exception e) {
      return null;
    }
  }

}
