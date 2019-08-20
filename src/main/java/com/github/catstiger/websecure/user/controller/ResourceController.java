package com.github.catstiger.websecure.user.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.common.web.WebUtil;
import com.github.catstiger.common.web.controller.BaseController;
import com.github.catstiger.common.web.ui.FreeMarkerService;
import com.github.catstiger.websecure.annotation.SecureResource;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.service.ResourceService;
import com.github.catstiger.websecure.user.service.RoleService;
import com.google.common.base.Splitter;

@Controller
@RequestMapping("/resources")
@SecureResource("许可管理")
public class ResourceController extends BaseController {
  private static Logger logger = LoggerFactory.getLogger(ResourceController.class);
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private RoleService roleService;
  @Autowired
  private ResourceService resourceService;
  @Autowired
  private FreeMarkerService freeMarkerService;

  /**
   * 列出所有{@link Resource#getParentId()}为{@code null}的资源，按照URL逆向排序。
   */
  @RequestMapping("/list_modules")
  @SecureResource("列出所有模块")
  @ResponseBody
  public List<Resource> listModules() {
    List<Resource> resources = jdbcTemplate.query(
        new SQLRequest(Resource.class).usingAlias(true).select().append(" WHERE parent_id is null ").orderBy("url", "desc").getSql(),
        new BeanPropertyRowMapper<Resource>(Resource.class));
    return resources;
  }

  /**
   * 根据@SecureResource标注的Controller类，找到其中的RequestMapping映射的URL资源，构建全部{@link Resource}对象
   */
  @RequestMapping("/build")
  @SecureResource("导入全部资源")
  @ResponseBody
  public Map<String, Object> build() {
    try {
      resourceService.build();
      return forExt(true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt(e.getMessage());
    }
  }

  /**
   * 批量为角色分配资源/Permission. 本操作会覆盖角色现有的授权。
   * 
   * @param roleId 角色ID
   * @param resIds 资源ID，comma分隔
   */
  @RequestMapping("/grant")
  @SecureResource("为角色分配资源")
  @ResponseBody
  public Map<String, Object> grant(@RequestParam("roleId") Long roleId, @RequestParam("resIds") String resIds) {
    if (roleId == null) {
      return forExt("请选择一个角色。");
    }
    Role role = jdbcTemplate.get(Role.class, roleId);
    if (role != null) {
      if (StringUtils.isBlank(resIds)) {
        roleService.grant(role.getName(), new String[] {}); // 这样清除所有此角色的授权
      } else {
        // 为角色重新授权
        List<String> resourceIds = Splitter.on(",").trimResults().splitToList(resIds);
        List<String> permissions = new ArrayList<>(resourceIds.size());
        resourceIds.forEach(res -> {
          Long id = Long.valueOf(res);
          Resource resource = jdbcTemplate.get(Resource.class, id);
          if (resource != null) {
            permissions.add(resource.getUrl());
          }
        });
        roleService.grant(role.getName(), permissions.toArray(new String[permissions.size()]));
      }

    }
    return forExt(true);
  }

  /**
   * 列出某个模块的资源
   */
  @SecureResource("列出某模块资源")
  @RequestMapping("res_of_module")
  @ResponseBody
  public void resOfModule(@RequestParam("resId") Long resId, @RequestParam("roleId") Long roleId) {
    SQLReady sqlReady = new SQLRequest(Resource.class).usingAlias(true).select().append(" WHERE parent_id=? ", resId).orderBy("url", "desc");

    List<Resource> items = jdbcTemplate.query(sqlReady.getSql(), new BeanPropertyRowMapper<Resource>(Resource.class), sqlReady.getArgs());

    Boolean isAll = false; // 是否模块下的所有资源都可以被给定角色访问
    if (roleId != null) {
      logger.debug("加载角色资源对应关系，用于页面checkbox复位{}", roleId);
      List<Long> resIds = jdbcTemplate.queryForList("select resources_id from roles_resources where roles_id=?", Long.class, roleId);
      if (!CollectionUtils.isEmpty(resIds) && !CollectionUtils.isEmpty(items)) {
        int checked = 0;
        for (Resource item : items) {
          if (resIds.contains(item.getId())) {
            checked++; // 记录匹配的数量
            item.setChecked(true);
          } else {
            item.setChecked(false);
          }
        }
        // 如果都匹配，则标记为all
        if (checked == items.size()) {
          isAll = true;
        }
      }

    }
    Map<String, Object> model = new HashMap<>(3);

    model.put("resId", resId);
    model.put("isAll", isAll);
    model.put("resources", items);

    HttpServletResponse response = WebObjectsHolder.getResponse();
    WebUtil.setNoCacheHeader(response); // 不可缓存

    String html = freeMarkerService.processTemplate("res_of_modules.ftl", model);
    renderHTML(html);
  }
}
