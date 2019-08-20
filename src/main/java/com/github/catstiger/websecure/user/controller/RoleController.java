package com.github.catstiger.websecure.user.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.LikeMode;
import com.github.catstiger.common.sql.Page;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.sql.filter.DynaSpec;
import com.github.catstiger.common.sql.filter.DynaSpecImpl;
import com.github.catstiger.common.sql.filter.QueryPart;
import com.github.catstiger.common.web.controller.BaseController;
import com.github.catstiger.websecure.annotation.SecureResource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.UserService;
import com.github.catstiger.websecure.user.service.impl.RoleServiceImpl;

@RestController
@RequestMapping("/roles")
@SecureResource("系统角色管理")
public class RoleController extends BaseController {
  @Autowired
  private RoleServiceImpl roleService;
  @Autowired
  private UserService userServcie;
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;

  /**
   * 根据角色名称查询角色对象，支持分页查询
   * 
   * @param roleName 角色名称
   */
  @RequestMapping("/index")
  @Transactional(readOnly = true)
  @SecureResource("查看角色列表")
  public Page index(@RequestParam(value = "roleName", required = false) String roleName) {
    Page page = page();
    SQLReady sqlReady = new SQLRequest(Role.class).usingAlias(true).select().where()
        .and(" name like ? ", StringUtils.isNotBlank(roleName), new Object[] { LikeMode.FULL.matching(roleName)});

    List<QueryPart> queryParts = QueryPart.parse(getRequest());
    // 符合DynaSpec规范的查询条件
    if (queryParts != null && !queryParts.isEmpty()) {
      DynaSpec dynaSpec = new DynaSpecImpl(queryParts);
      sqlReady.append(dynaSpec.buildQueryString(), dynaSpec.getQueryParams());
    }

    List<Role> roles = jdbcTemplate.query(sqlReady.limitSql(page.getStart(), page.getLimit()), new BeanPropertyRowMapper<Role>(Role.class), sqlReady.getArgs());

    Long total = jdbcTemplate.queryForObject(sqlReady.countSql(), Long.class, sqlReady.getArgs());

    page.setRows(roles);
    page.setTotal(total);

    return page;
  }

  /**
   * 查询某个用户所具有的角色
   * 
   * @param userId 用户ID
   * @return 分页查询结果
   */
  @RequestMapping("/assigned")
  @SecureResource("列出某个用户的角色")
  public Page assigned(@RequestParam("userId") Long userId) {
    Page page = page();
    User user = jdbcTemplate.get(User.class, userId);
    Collection<Role> roles = userServcie.getRolesByUser(user);
    page.setRows(roles);
    page.setTotal(roles.size());

    return page;
  }

  /**
   * 查询某个用户所<strong>不具有</strong>的角色
   * 
   * @param userId 用户ID
   * @return 分页查询结果
   */
  @RequestMapping("/unassigned")
  @SecureResource("列出某个用户没有的角色")
  public Page unassigned(@RequestParam("userId") Long userId) {
    Page page = page();
    Collection<Role> roles = jdbcTemplate.query(
        "select id,name,descn from roles where id not in (select roles_id from users_roles where users_id=?) order by name asc",
        new BeanPropertyRowMapper<Role>(Role.class), userId);
    page.setRows(roles);
    page.setTotal(roles.size());

    return page;
  }

  /**
   * 更新或者新建一个角色，如果{@code role}的id为空，则新建，否则修改。
   * 
   * @param role 角色实例，必须包含{@link Role#getName()}属性
   */
  @RequestMapping("/save")
  @SecureResource("新建或者保存角色")
  public Map<String, Object> save(Role role) {
    if (role == null || StringUtils.isBlank(role.getName())) {
      throw new IllegalArgumentException("角色名称不可为空。");
    }
    try {
      if (role.getId() == null) {
        role = roleService.create(role.getName(), role.getDescn(), false);
      } else {
        role = roleService.update(role.getId(), role.getName(), role.getDescn());
      }
      return forExt(role, true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt(e.getMessage());
    }
  }

  /**
   * 查看角色信息
   * 
   * @param role 角色实例，必须包括{@code Role#getId()}
   * @return 返回符合ExtJS的Map对象
   */
  @RequestMapping("/view")
  @Transactional(readOnly = true)
  @SecureResource("查看单个角色")
  public Map<String, Object> view(Role role) {
    if (role == null || role.getId() == null) {
      throw new IllegalArgumentException("角色ID不可为空。");
    }

    SQLReady sqlReady = new SQLRequest(Role.class).usingAlias(true).byId(true).select().addArg(role.getId());
    role = jdbcTemplate.queryForObject(sqlReady.getSql(), new BeanPropertyRowMapper<Role>(Role.class), sqlReady.getArgs());

    return forExt(role, true);
  }

  /**
   * 删除一个角色
   * 
   * @param role 角色实例，必须包括{@code Role#getId()}
   */
  @RequestMapping("/remove")
  @Transactional
  @SecureResource("删除一个角色")
  public void remove(Role role) {
    if (role == null || role.getId() == null) {
      throw new IllegalArgumentException("角色ID不可为空。");
    }
    SQLReady sqlReady = new SQLRequest(Role.class).usingAlias(true).byId(true).select().addArg(role.getId());
    role = jdbcTemplate.queryForObject(sqlReady.getSql(), new BeanPropertyRowMapper<Role>(Role.class), sqlReady.getArgs());

    if (role != null) {
      roleService.remove(role.getName());
    }
  }
}
