package com.github.catstiger.websecure.login.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.login.LoginException;
import com.github.catstiger.websecure.login.LoginService;
import com.github.catstiger.websecure.login.listener.LoginFailureListener;
import com.github.catstiger.websecure.login.listener.LoginSuccessListener;
import com.github.catstiger.websecure.login.listener.LogoutListener;
import com.github.catstiger.websecure.login.sync.SessionSynchronizer;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.token.TokenStrategy;
import com.github.catstiger.websecure.token.impl.CookieTokenStorage;
import com.github.catstiger.websecure.token.impl.HttpSessionTokenStorage;
import com.github.catstiger.websecure.user.service.UserService;
import com.github.catstiger.websecure.web.RedirectService;
import com.github.catstiger.websecure.web.RequestKeeper;
import com.github.catstiger.websecure.web.RequestModel;
import com.github.catstiger.websecure.web.SecurityObjectsModel;
import com.github.catstiger.websecure.web.SubjectHolder;
import com.github.catstiger.websecure.web.impl.HttpSessionRequestKeeper;
import com.github.catstiger.websecure.web.impl.RedirectServiceImpl;

public abstract class AbstractLoginService implements LoginService {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  protected SecurityProperties config;

  @Autowired
  protected UserService userService;

  @Autowired
  protected TokenStrategy signStrategy;

  protected RedirectService redirectService;

  protected HttpSessionTokenStorage httpSessionTokenStorage;

  protected CookieTokenStorage cookieTokenStorage;
  @Autowired
  protected SessionSynchronizer sessionSynchronizer;
  
  /**
   * 初始化
   */
  @PostConstruct
  public void initialize() {
    this.httpSessionTokenStorage = new HttpSessionTokenStorage(config);
    this.cookieTokenStorage = new CookieTokenStorage(config);
    this.redirectService = new RedirectServiceImpl();
  }

  @Override
  public Object login() {
    Object result = null;
    try {
      AuthzToken authzToken = getLoginToken();
      if (authzToken == null) {
        throw new LoginException(SecureConstants.MSG_USER_NOT_FOUND);
      }
      Subject subject = attemptLogin(authzToken);
      if (subject != null) {
        SubjectHolder.setSubject(subject);
        String token = signStrategy.sign(subject.getPrincipal());
        // 保存Session
        httpSessionTokenStorage.save(token);
        // Remember Me
        if (subject.isRememberMe()) {
          logger.debug("RememberMe 登录 {}", subject.getPrincipal().getName());
          cookieTokenStorage.save(token);
        }
        // 同步控制
        sessionSynchronizer.apply(subject.getPrincipal());
        // 执行登录成功监听
        execSuccessListeners(subject);
        // 做最后的收尾工作，重定向什么的
        result = doSuccessed(subject, token);
      }
    } catch (LoginException e) {
      httpSessionTokenStorage.remove(); // 此次失败的登录，需要删除Session中的信息
      cookieTokenStorage.remove(); // 此次失败的登录，需要删除Cookies中的信息
      execFailureListeners(e);
      doFailed(e); // 处理登录失败后的一些工作
      logger.warn("登录失败！{}", e.getMessage());
    }

    return result;
  }

  @Override
  public Object logout() throws LoginException {
    Subject subject = SubjectHolder.getSubject();
    if (subject == null || !subject.isAuthenticated()) {
      logger.info("匿名用户不可logout");
      return false;
    }
    logger.info("用户{}正在登出。", subject.getPrincipal().getName());

    this.httpSessionTokenStorage.remove();
    this.cookieTokenStorage.remove();
    // 删除保存的Request（上次访问被拒绝的那个Request）
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpServletResponse response = WebObjectsHolder.getResponse();
    RequestKeeper requestKeeper = new HttpSessionRequestKeeper();
    RequestModel requestModel = requestKeeper.get(request, response);
    if (requestModel != null) {
      requestKeeper.remove(request, response);
    }

    Object result = doLogout();
    execLogoutListeners(subject); // 执行监听

    return result;
  }

  /**
   * 执行成功登录后的监听器
   */
  protected void execSuccessListeners(Subject subject) {
    List<LoginSuccessListener> listeners = getLoginSuccessListeners();
    if (listeners != null) {
      for (LoginSuccessListener listener : listeners) {
        listener.onSuccess(subject);
      }
    }
  }

  /**
   * 执行成功登录后的监听器
   */
  protected void execFailureListeners(LoginException e) {
    List<LoginFailureListener> listeners = getLoginFailureListeners();
    if (listeners != null) {
      for (LoginFailureListener listener : listeners) {
        listener.onAuthenticationFailure(e);
      }
    }
  }

  protected void execLogoutListeners(Subject subject) {
    List<LogoutListener> listeners = getLogoutListeners();
    if (listeners != null) {
      for (LogoutListener listener : listeners) {
        listener.afterLogout(subject);
      }
    }
  }

  /**
   * 用于处理登录失败之后的行为：重定向、或者抛出异常、render一个错误信息等。
   * 
   */
  public abstract Object doFailed(LoginException e);

  /**
   * 返回登出监听器集合，这些监听器将在登出之后执行，缺省的，返回{@link Collections#emptyList()}
   */
  protected List<LogoutListener> getLogoutListeners() {
    return Collections.emptyList();
  }

  /**
   * 子类实现这个方法，返回所需的{@code LoginSuccessListener}的实例，以执行登录成功后的操作。
   */
  protected abstract List<LoginSuccessListener> getLoginSuccessListeners();

  /**
   * 返回登录失败后的LoginFailureListener
   */
  protected List<LoginFailureListener> getLoginFailureListeners() {
    return Collections.emptyList();
  }

  /**
   * 做Logout之后的事情，重定向，执行Listeners等。
   * 
   * @return 返回登出操作之后的结果
   */
  protected abstract Object doLogout();

  /**
   * 获取登录信息，用户名、密码之类的
   */
  protected abstract AuthzToken getLoginToken();

  /**
   * 尝试进行登录人，如果成功，返回此次登录对应的{@code Subject}对象，如果失败，抛出{@code LoginException}
   * 
   * @param token 登录请求对象
   * @return Instance of {@code Subject} if successed.
   * @throws LoginException if login failed.
   */
  protected abstract Subject attemptLogin(AuthzToken token);

  /**
   * 用于 成功登录之后的处理，通常，会将登录后的{@code Principal}存入Session，并且将Subject存入SecurityObjectsHolder。 最后，根据不同的应用场景，重定向页面，或者writer一个token、cookie
   * 
   * @param subject 登录成功之后创建的{@link Subject}对象
   * @param token   登录成功后的token
   * @return 返回登录成功后的结果，可以为{@code null}, 也可以是{@link SecurityObjectsModel}
   */
  public abstract Object doSuccessed(Subject subject, String token);

}
