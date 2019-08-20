package com.github.catstiger.websecure.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.web.ui.FreeMarkerService;
import com.github.catstiger.websecure.authc.AccessDecisionService;
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.RoleMenuService;
import com.github.catstiger.websecure.user.service.RoleService;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

@Service
public class SecurityJsService {
  private static Logger logger = LoggerFactory.getLogger(SecurityJsService.class);
  /**
   * Securtiy JS缓存名称
   */
  public static final String SECURITY_JS_CACHE_NAME = "__SECURITY__JS__CACHE__C";
  /**
   * Security JS缓存过期时间(Sec)
   */
  public static final int SECURITY_JS_CACHE_TTL = 1800;

  @Autowired
  private FreeMarkerService freeMarkerService;
  @Autowired
  private AccessDecisionService accessDecisionService;
  @Autowired
  private RoleMenuService roleMenuService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private RedissonClient redis;
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
 

  private static String anonymousJs = null;

  /**
   * 返回一个标准的Javascript代码用于在js中判断角色和权限
   * 
   * @param isCompress 是否压缩
   * @return
   */
  public String js(Boolean isCompress, Long corpId) {
    Subject subject = SubjectHolder.getSubject();
    if (subject == null || !subject.isAuthenticated()) {
      return renderAnonymous(); // 渲染匿名用户js
    }

    String js = getCache(subject.getPrincipal().getName());
    if (StringUtils.isNotBlank(js)) {
      logger.debug("从缓存中取得SecurityJS {}", subject.getPrincipal().getName());
      return js;
    }

    Map<String, Object> model = new HashMap<>(10);
    model.put("resources", JSON.toJSONString(accessDecisionService.loadConfigurablePermissions()));
    model.put("roles", JSON.toJSONString(subject.getPrincipal().getAuthorities()));
    model.put("user", JSON.toJSONString((User) subject.getPrincipal()));
    model.put("authenticated", JSON.toJSONString(subject.isAuthenticated()));

    Set<Authority> authorities = subject.getPrincipal().getAuthorities();
    Map<String, Collection<String>> roleViews = new HashMap<>(authorities.size());
    authorities.forEach(a -> {
      Role role = roleService.byName(a.getName());
      if (role != null) {
        Collection<String> views = roleMenuService.getRoleViews(role, corpId);
        roleViews.put(role.getName(), views);
      }
    });

    model.put("roleViews", JSON.toJSONString(roleViews));

    js = freeMarkerService.processTemplate("js_security.ftl", model);
    // 压缩JS
    if (Boolean.TRUE.equals(isCompress)) {
      js = compressJs(js);
    }
    // 缓存JS
    putCache(subject.getPrincipal().getName(), js);

    return js;
  }

  private void putCache(String key, String js) {
    RMapCache<Object, Object> cache = redis.getMapCache(SECURITY_JS_CACHE_NAME);
    cache.put(key, js, SECURITY_JS_CACHE_TTL, TimeUnit.SECONDS);
  }

  private String getCache(String key) {
    RMapCache<Object, Object> cache = redis.getMapCache(SECURITY_JS_CACHE_NAME);
    return (String) cache.get(key);
  }

  /**
   * 清除某个User对应的SecurityJS的缓存
   * 
   * @param username Username即缓存KEY
   */
  public void evictCache(String username) {
    RMapCache<Object, Object> cache = redis.getMapCache(SECURITY_JS_CACHE_NAME);
    logger.info("清除Security Js缓存 {}", username);
    cache.remove(username);
  }

  /**
   * 清空所有具有某个角色的用户的js缓存
   * 
   * @param roleName 角色名称，如果角色不存在，则不做任何操作
   */
  public void evictCacheByRole(String roleName) {
    // 清空security js缓存
    Role role = roleService.byName(roleName);
    if (role != null && role.getId() != null) {
      RMapCache<Object, Object> cache = redis.getMapCache(SECURITY_JS_CACHE_NAME);
      List<String> usernames = jdbcTemplate.queryForList("select u.username from users u inner join users_roles r on (u.id=r.users_id) where r.roles_id=?",
          String.class, role.getId());
      usernames.forEach(username -> {
        cache.removeAsync(username);
      });
    }
  }

  /**
   * 清空所有用户的Security JS缓存
   */
  public void clearCache() {
    logger.info("清除所有Security Js缓存");
    RMapCache<Object, Object> cache = redis.getMapCache(SECURITY_JS_CACHE_NAME);
    cache.clear();
  }

  private String renderAnonymous() {
    if (anonymousJs == null) {
      anonymousJs = freeMarkerService.processTemplate("js_security_anonymous.ftl", new Object());
    }
    return anonymousJs;
  }

  private static String compressJs(String js) {
    StringReader reader = new StringReader(js);
    String compressed = js;
    try {
      JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorReporter() {
        @Override
        public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {
        }

        @Override
        public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {
          return null;
        }

        @Override
        public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {
        }

      });
      StringWriter writer = new StringWriter();
      compressor.compress(writer, 10, true, true, false, false);
      compressed = writer.toString();
    } catch (EvaluatorException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return compressed;
  }
}
