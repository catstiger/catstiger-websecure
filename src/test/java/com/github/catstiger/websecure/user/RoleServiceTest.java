package com.github.catstiger.websecure.user;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.github.catstiger.TestApplication;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.service.ResourceService;
import com.github.catstiger.websecure.user.service.impl.RoleServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class RoleServiceTest {
  @Autowired
  private RoleServiceImpl roleService;
  
  @Autowired
  private ResourceService resourceService;
  
  @Test
  public void testCreate() {
    Role role1 = roleService.create("spring", "SPRING", true, 0L);
    Assert.isTrue(role1.getName().equals("spring@0"), "");
    
    role1 = roleService.byName("spring@0");
    Resource r = resourceService.create("/admin/**", "Administrator");
    
    roleService.grant("spring@0", r.getPermission());
    Collection<Permission> ps = roleService.getPermissionsOfRole(role1);
    Assert.isTrue(roleService.hasAnyPermission(role1, ps), "");
    
    roleService.revoke("spring@0", r.getPermission());
    Assert.isTrue(!roleService.hasAnyPermission(role1, ps), "");
  }
}
