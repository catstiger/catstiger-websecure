package com.github.catstiger.websecure.login.sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.user.model.User;

/**
 * 虚拟机内部实现用户登录记录和Session同步登录机制（后一个登录者踢出前一个）
 * 
 * @author leesam
 */

public class JVMSessionSynchronizer implements SessionSynchronizer {
  private static Logger logger = LoggerFactory.getLogger(JVMSessionSynchronizer.class);
  public static final String LAST_USER_SESSION_KEY = "__login_binding_in_session_key";
  private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();
  @Autowired
  private SecurityProperties config;

  @Override
  public void apply(Principal principal) {
    if (principal == null || !(principal instanceof User)) {
      throw new IllegalStateException("Login principal not exists");
    }
    HttpServletRequest request = WebObjectsHolder.getRequest();
    if (request == null) {
      throw new IllegalStateException("未找到Request，请确认WebSecureFilter正确配置");
    }

    if (config.isRecordLogin()) {
      JVMStoreSessionBindingListener sessionBindingListener = new JVMStoreSessionBindingListener(principal.getName(), config.isSyncLogin());
      HttpSession session = request.getSession();
      if (session != null) {
        session.setAttribute(LAST_USER_SESSION_KEY, sessionBindingListener);
      }
    }
  }

  public static class JVMStoreSessionBindingListener implements HttpSessionBindingListener {
    private String username;
    private boolean syncLogin;

    JVMStoreSessionBindingListener(String username, boolean syncLogin) {
      this.username = username;
      this.syncLogin = syncLogin;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
      if (syncLogin) {
        HttpSession sessionInUse = sessions.get(username);
        if (sessionInUse != null) {
          logger.info("用户多次登录，前一次的登录被踢出{}", username);
          sessionInUse.invalidate();
        }
      }

      sessions.put(username, event.getSession());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
      logger.info("{} 登出!", username);
      if (sessions.containsKey(username)) {
        sessions.remove(username); // 删除登录记录
      }
    }
  }

}
