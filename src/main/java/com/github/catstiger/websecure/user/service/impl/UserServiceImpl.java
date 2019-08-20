package com.github.catstiger.websecure.user.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.sql.id.IdGen;
import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.AccessDecisionService;
import com.github.catstiger.websecure.authc.AccessDeniedException;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.login.AccountNotFoundException;
import com.github.catstiger.websecure.password.PasswordEncoder;
import com.github.catstiger.websecure.user.cache.RBACache;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.RoleService;
import com.github.catstiger.websecure.user.service.UserService;
import com.github.catstiger.websecure.web.SecurityJsService;

@Service
public class UserServiceImpl implements UserService {
  private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RoleService roleService;
  @Autowired
  private AccessDecisionService accessDecisionService;
  @Autowired
  private RBACache rbacache;
  @Autowired
  private IdGen idGen;
  @Autowired
  private SecurityJsService jsService;

  @Override
  @Transactional(readOnly = true)
  public User byName(String name) throws AccountNotFoundException {
    User user = rbacache.getUser(name);
    if (user != null) {
      return user;
    }
    SQLReady sqlReady = new SQLRequest(User.class).usingAlias(true).select().append(" WHERE username=? ", name)
        .append(" AND is_enabled=true AND is_locked=false");
    logger.debug(sqlReady.getSql());
    user = queryUser(sqlReady);

    if (user != null) {
      logger.debug("加载用户信息 {}", user);
    } else {
      throw new AccountNotFoundException(SecureConstants.MSG_USER_NOT_FOUND);
    }
    user = this.simplify(user);
    rbacache.putUser(user);

    return user;
  }
  

  @Override
  public User byNameQuietly(String username) {
    try {
      return byName(username);
    } catch (Exception e) {
      logger.warn("User {} not found, but a null value returned.");
      return null;
    }
  }
  
  /**
   * Query userid by username
   */
  public Long idByUsername(String username) {
    User user = this.byNameQuietly(username);
    return (user != null) ? user.getId() : null;
  }


  @Override
  @Transactional(readOnly = true)
  public User byAlias(String alias) {
    SQLReady sqlReady = new SQLRequest(User.class).usingAlias(true).select().append("where alias_=?", alias);
    return queryUser(sqlReady);
  }

  /**
   * 根据用户名获取User实例，不论User的状态如何（是否可用、是否锁定）。
   * <p>
   * {@code #byNameAnyway(String)}不使用缓存，因此每次都可以取得最新的实例。
   * </p>
   * @param name username
   * @return User of the name, {@code null} if not exists;
   */
  public User getAnyway(String name) {
    SQLReady sqlReady = new SQLRequest(User.class).usingAlias(true).select().append(" where username=? ", name);
    logger.debug(sqlReady.getSql());
    return queryUser(sqlReady);
  }

  /**
   * ID，username，alias，任意一个查询User实体
   */
  public User getAnyway(User user) {
    Assert.notNull(user, "User must not be null");

    SQLReady sqlReady = new SQLRequest(User.class).usingAlias(true).select().append(" where 1=1 ").appendIfExists(" and id = ?", user.getId())
        .appendIfExists(" and username=? ", user.getUsername()).appendIfExists(" and alias_=? ", user.getAlias());

    return queryUser(sqlReady);
  }

  @Override
  @Transactional
  public User register(User user) {
    return this.register(user, null);
  }

  @Override
  @Transactional
  public User register(User user, List<String> roles) {
    Assert.notNull(user, "User must not be null");
    Assert.notNull(user.getUsername(), "用户名是必须的。");
    Assert.notNull(user.getPassword(), "密码是必须的。");
    Assert.isTrue(user.getUsername().length() >= 1, "用户名长度必须大于1字符。");

    Long count = jdbcTemplate.queryForObject("select count(*) from users where username=?", Long.class, user.getUsername());
    // 验证重复的登录名
    if (count > 0) {
      throw Exceptions.unchecked("您输入的登录名'" + user.getUsername() + "'已经存在.");
    }
    count = jdbcTemplate.queryForObject("select count(*) from users where alias_=?", Long.class, user.getAlias());
    // 验证重复的手机号
    if (count > 0) {
      throw Exceptions.unchecked("您输入的手机号'" + user.getAlias() + "'已经存在.");
    }

    user.setRegistTime(DateTime.now().toDate());
    user.setIsEnabled(true);
    user.setIsLocked(false);
    if (user.getIsSys() == null) {
      user.setIsSys(false);
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setId(idGen.nextId());

    SQLReady sqlReady = new SQLRequest(user).insert();
    jdbcTemplate.update(sqlReady.getSql(), sqlReady.getArgs());

    if (roles != null) {
      roles.forEach(r -> {
        Role role = roleService.byName(r);
        if (role != null) {
          jdbcTemplate.update("insert into users_roles (users_id, roles_id) values(?,?)", user.getId(), role.getId());
        }
      });
    }
    return simplify(user);
  }

  @Override
  @Transactional
  public void lock(String username) {
    Assert.notNull(username, "Username must not be null");
    Assert.isTrue(StringUtils.isNotBlank(username), "Username must not be blank");

    User user = getAnyway(username);
    if (user != null) {
      if (user.getIsSys() != null && user.getIsSys()) {
        throw new IllegalStateException("系统用户不可锁定");
      }
      jdbcTemplate.update("update users set is_locked=true, lock_time=? where username=?", DateTime.now().toDate(), username);
      rbacache.evictUser(username);
      jsService.evictCache(username);
    }
  }

  @Override
  @Transactional
  public void unlock(String username) {
    Assert.notNull(username, "Username must not be null");
    Assert.isTrue(StringUtils.isNotBlank(username), "Username must not be blank");

    jdbcTemplate.update("update users set is_locked=false, lock_time=? where username=?", DateTime.now().toDate(), username);
    rbacache.evictUser(username);
    jsService.evictCache(username);
  }

  @Override
  @Transactional
  public User grant(String username, String roleName) {
    User user = getAnyway(username);
    Role role = roleService.byName(roleName);

    if (user == null) {
      throw Exceptions.unchecked("没有找到用户，" + username);
    }
    if (role == null) {
      throw Exceptions.unchecked("没有找到角色，" + roleName);
    }
    Long c = jdbcTemplate.queryForObject("select count(*) from users_roles where users_id=? and roles_id=?", Long.class, user.getId(), role.getId());

    if (c == 0L) {
      jdbcTemplate.update("insert into users_roles(users_id,roles_id) values(?,?)", user.getId(), role.getId());
      rbacache.evictUser(username);
      rbacache.evictRolesOfUser(username);
      jsService.evictCache(username);
    }

    return user;
  }

  @Override
  @Transactional
  public User revoke(String username, String roleName) {
    User user = getAnyway(username);
    Role role = roleService.byName(roleName);

    if (user == null) {
      throw Exceptions.unchecked("没有找到用户，" + username);
    }
    if (role == null) {
      throw Exceptions.unchecked("没有找到角色，" + roleName);
    }
    Long c = jdbcTemplate.queryForObject("select count(*) from users_roles where users_id=? and roles_id=?", Long.class, user.getId(), role.getId());

    if (c > 0L) {
      jdbcTemplate.update("delete from users_roles where users_id=? and roles_id=?", user.getId(), role.getId());
      rbacache.evictUser(username);
      rbacache.evictRolesOfUser(username);
      jsService.evictCache(username);
    }
    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Role> getRolesByUser(User user) {
    Assert.notNull(user, "User must not be null.");

    Collection<Role> rolesInCache = rbacache.getRolesOfUser(user.getUsername());
    if (CollectionUtils.isNotEmpty(rolesInCache)) {
      return rolesInCache;
    }

    user = getAnyway(user);
    if (user != null && user.getId() != null) {
      List<Role> roles = jdbcTemplate.query(
          "SELECT r.id id,r.name name,r.descn descn " + " FROM roles r INNER JOIN users_roles ur ON(ur.roles_id=r.id) WHERE ur.users_id=?;",
          new BeanPropertyRowMapper<Role>(Role.class), user.getId());

      logger.debug("获取用户{}的角色{}。", user.getName(), roles);
      rbacache.putRolesOfUser(user.getUsername(), roles);
      return roles;
    }
    return Collections.emptySet();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasRole(User user, Role role) {
    Assert.notNull(user, "User must not be null.");
    Collection<Role> roles = getRolesByUser(user);
    return roles.contains(role);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAllRoles(User user, Collection<Role> roles) {
    Assert.notNull(user, "User must not be null.");
    Collection<Role> granted = getRolesByUser(user);
    return granted.containsAll(roles);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAnyRole(User user, Collection<Role> roles) {
    Assert.notNull(user, "User must not be null.");
    Collection<Role> granted = getRolesByUser(user);
    return CollectionUtils.containsAny(granted, roles);
  }

  @Override
  @Transactional
  public void changePassword(String username, String password, String originalPassword, String confirmPassword) {
    if (!StringUtils.equals(password, confirmPassword)) {
      throw Exceptions.unchecked("两次输入的密码不同。");
    }
    // 获得加密后的密码
    String encodePasswrod = passwordEncoder.encode(originalPassword);
    User user = this.getAnyway(username);
    Assert.notNull(user, "用户不存在。");

    if (StringUtils.equals(user.getPassword(), encodePasswrod)) {
      jdbcTemplate.update("update users set password=? where id=?", passwordEncoder.encode(password), user.getId());
      rbacache.evictUser(username);
    } else {
      throw Exceptions.unchecked("原密码不正确！");
    }
  }

  @Override
  @Transactional
  public void changePassword(String username, String password) {
    User user = this.getAnyway(username);
    Assert.notNull(user, "用户不存在。");

    jdbcTemplate.update("update users set password=? where id=?", passwordEncoder.encode(password), user.getId());
    rbacache.evictUser(username);
  }

  @Override
  @Transactional
  public void enabled(String username) {
    Assert.notNull(username, "Username must not be null");
    Assert.isTrue(StringUtils.isNotBlank(username), "Username must not be blank");

    jdbcTemplate.update("update users set is_enabled=true where username=?", username);
    rbacache.evictUser(username);
    jsService.evictCache(username);
  }

  @Override
  @Transactional
  public void disabled(String username) {
    Assert.notNull(username, "Username must not be null");
    Assert.isTrue(StringUtils.isNotBlank(username), "Username must not be blank");

    User user = getAnyway(username);
    if (user != null) {
      if (user.getIsSys() != null && user.getIsSys()) {
        throw new IllegalStateException("系统用户不可禁用");
      }
      jdbcTemplate.update("update users set is_enabled=false where username=?", username);
      rbacache.evictUser(username);
      jsService.evictCache(username);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isPermitted(User user, String permission) {
    Assert.notNull(user, "User must not be null");
    Assert.notNull(user.getName(), "User must not be null.");

    boolean permitted = false;
    try {
      user = byName(user.getName()); // 重新加载一下用户，使得角色生效
      Collection<Permission> configurablePermissions = accessDecisionService.loadConfigurablePermissions();
      accessDecisionService.decide(user, permission, configurablePermissions);
      permitted = true;
    } catch (AccessDeniedException e) {
      logger.debug(e.getMessage());
    }
    return permitted;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isPermitted(User user, Permission permission) {
    return isPermitted(user, permission.getPermission());
  }

  /**
   * 更新用户名，别名
   * @param user 被更新的用户，必须包含ID，用户名等自动
   */
  @Transactional
  public User update(User user) {
    Assert.notNull(user, "User must not be null.");

    if (StringUtils.isBlank(user.getUsername())) {
      throw new IllegalArgumentException("用户名是必须的！");
    }

    Long exists = jdbcTemplate.queryForObject("select count(*) from users where username=? and id<>?", Long.class, user.getUsername(), user.getId());
    if (exists > 0) {
      throw new IllegalArgumentException("用户名已经存在！");
    }
    exists = jdbcTemplate.queryForObject("select count(*) from users where alias_=? and id<>?", Long.class, user.getAlias(), user.getId());
    if (exists > 0) {
      throw new IllegalArgumentException("用户别名/手机已经存在！");
    }
    // 清空缓存
    String username = jdbcTemplate.queryForObject("select username from users where id=?", String.class, user.getId());
    if (StringUtils.isBlank(username)) {
      throw new IllegalStateException("用户不存在！");
    }
    rbacache.evictUser(username);
    rbacache.evictRolesOfUser(username);
    jsService.evictCache(username);

    jdbcTemplate.update("update users set username=?, alias_=? where id=?", user.getUsername(), user.getAlias(), user.getId());

    return user;
  }

  /**
   * Clone一个“干净的User”对象
   */
  private User simplify(User user) {
    User simplifyUser = null;
    if (user != null) {
      simplifyUser = new User();
      simplifyUser.setId(user.getId());
      simplifyUser.setUsername(user.getUsername());
      simplifyUser.setPassword(user.getPassword());
      simplifyUser.setIsEnabled(user.getIsEnabled());
      simplifyUser.setIsLocked(user.getIsLocked());
      simplifyUser.setLockTime(user.getLockTime());
      simplifyUser.setRegistTime(user.getRegistTime());
      simplifyUser.setAlias(user.getAlias());
      simplifyUser.setIsSys(user.getIsSys());

      Collection<Role> roles = this.getRolesByUser(user);
      Set<Role> transientRoles = new HashSet<>(roles.size());
      for (Role role : roles) {
        Role transientRole = new Role();
        transientRole.setId(role.getId());
        transientRole.setName(role.getName());
        transientRole.setIsSys(role.getIsSys());
        transientRoles.add(transientRole);
      }
      simplifyUser.setRoles(transientRoles);
    }
    return simplifyUser;
  }

  private User queryUser(SQLReady sqlReady) {
    return jdbcTemplate.queryForObject(sqlReady.getSql(), new BeanPropertyRowMapper<User>(User.class), sqlReady.getArgs());
  }


  @Override
  public User byId(Long id) {
    return jdbcTemplate.get(User.class, id);
  }

}
