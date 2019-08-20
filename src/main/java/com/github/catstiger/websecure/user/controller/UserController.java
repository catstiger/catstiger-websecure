package com.github.catstiger.websecure.user.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.LikeMode;
import com.github.catstiger.common.sql.Page;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.web.controller.BaseController;
import com.github.catstiger.websecure.annotation.SecureResource;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.impl.UserServiceImpl;
import com.github.catstiger.websecure.web.SubjectHolder;

@RestController
@RequestMapping("/users")
@SecureResource("系统用户管理")
public class UserController extends BaseController {
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private UserServiceImpl userService;

  /**
   * 查询用户信息/用户列表
   * 
   * @param user 查询Example
   * @return 分页查询结果
   */
  @RequestMapping("/index")
  @SecureResource("系统用户列表")
  public Page index(@ModelAttribute User user) {
    Page page = page();
    SQLReady sqlReady = new SQLRequest(User.class).usingAlias(true).select().append(" WHERE 1=1 ")
        .like(" and username like ? ", user.getUsername(), LikeMode.FULL).like(" and alias_ like ?", user.getAlias(), LikeMode.FULL)
        .orderBy(getSortName(User.class), getSortOrder(), getSortName(User.class) != null && getSortOrder() != null);

    List<User> rows = jdbcTemplate.queryBySqlReady(sqlReady, page, User.class);
    Long count = jdbcTemplate.queryTotal(sqlReady);

    page.setRows(rows);
    page.setTotal(count);

    return page;
  }

  /**
   * 为指定的用户分配一个角色
   * 
   * @param userId 给出用户ID
   * @param roleId 给出角色ID
   * @return
   */
  @RequestMapping("/grant")
  @SecureResource("为用户分配角色")
  public Map<String, Object> grant(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId) {
    User user = jdbcTemplate.get(User.class, userId);
    Role role = jdbcTemplate.get(Role.class, roleId);

    if (user == null || role == null) {
      return forExt("用户或者角色不存在");
    }

    try {
      userService.grant(user.getUsername(), role.getName());
      return forExt(user, true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt(e.getMessage());
    }
  }

  /**
   * 夺取一个用户的角色
   * 
   * @param userId 给出用户ID
   * @param roleId 给出角色ID
   * @return
   */
  @RequestMapping("/revoke")
  @SecureResource("撤销一个用户的角色")
  public Map<String, Object> revoke(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId) {
    User user = jdbcTemplate.get(User.class, userId);
    Role role = jdbcTemplate.get(Role.class, roleId);

    if (user == null || role == null) {
      return forExt("用户或者角色不存在");
    }

    try {
      userService.revoke(user.getUsername(), role.getName());
      return forExt(user, true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt(e.getMessage());
    }
  }

  /**
   * 查看单个用户信息
   * 
   * @param user 用户实体，主要是提供id
   * @return 符合EXTJS要求的结果
   */
  @RequestMapping("/view")
  @SecureResource("查看一个用户信息")
  public Map<String, Object> view(@ModelAttribute User user) {
    Assert.notNull(user, "实体类不可为空");
    Assert.notNull(user.getId(), "ID不可为空。");

    user = jdbcTemplate.get(User.class, user.getId());
    return forExt(user, true);
  }

  /**
   * 新增或者修改用户信息，如果{@link User#getId()}为{@code null}, 则信息，否则，修改。 只能修改用户名{@link User#getUsername()}和别名{@link User#getAlias()}
   * 
   * @param user 给出用户实例，包含新的用户信息
   * @return 返回修改/新增后的用户数据
   */
  @RequestMapping("/save")
  @SecureResource("新建或者修改用户")
  public Map<String, Object> save(@ModelAttribute User user) {
    Assert.notNull(user, "实体类不可为空");
    if (StringUtils.isBlank(user.getAlias())) {
      user.setAlias(null);
    }
    try {
      if (user.getId() == null) { // 新增
        user = userService.register(user);
      } else { // 修改
        user = userService.update(user);
      }
      return forExt(user, true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt(e.getMessage());
    }
  }

  /**
   * 禁用一个账号
   */
  @RequestMapping("/disabled")
  @SecureResource("禁用一个账号")
  public Map<String, Object> disabled(@ModelAttribute User user) {
    Assert.notNull(user, "实体类不可为空");
    Assert.notNull(user.getId(), "ID不可为空。");

    user = jdbcTemplate.get(User.class, user.getId());
    if (user != null) {
      userService.disabled(user.getUsername());
    }

    return forExt(user, true);
  }

  /**
   * 启用一个账号
   */
  @RequestMapping("/enabled")
  @SecureResource("启用一个账号")
  public Map<String, Object> enabled(@ModelAttribute User user) {
    Assert.notNull(user, "实体类不可为空");
    Assert.notNull(user.getId(), "ID不可为空。");

    user = jdbcTemplate.get(User.class, user.getId());
    if (user != null) {
      userService.enabled(user.getUsername());
    }

    return forExt(user, true);
  }

  /**
   * 修改密码
   * @param password 新密码
   * @param originalPassword 原密码
   * @param confirmPassword 新密码确认
   * @return
   */
  @RequestMapping("/changePassword")
  public Map<String, Object> changePassword(@RequestParam("password") String password, @RequestParam("originalPassword") String originalPassword,
      @RequestParam("confirmPassword") String confirmPassword) {
    Subject subject = SubjectHolder.getSubject();
    User user = (User) subject.getPrincipal();
    if (user == null || !subject.isAuthenticated()) {
      throw new IllegalStateException();
    }
    try {
      userService.changePassword(user.getUsername(), password, originalPassword, confirmPassword);
      return forExt(user, true);
    } catch (Exception e) {
      return forExt(e.getMessage());
    }
  }

}
