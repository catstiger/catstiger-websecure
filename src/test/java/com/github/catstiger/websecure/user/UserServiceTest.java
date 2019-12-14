package com.github.catstiger.websecure.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.github.catstiger.TestApplication;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.RoleService;
import com.github.catstiger.websecure.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class UserServiceTest {
  @Autowired
  private UserService userService;
  
  @Autowired
  private RoleService roleService;
  
  @Autowired
  private SecurityProperties securityProp;

  @Test
  public void testRegister() {
    User user1 = new User();
    
    user1.setUsername(RandomStringUtils.random(10));
    user1.setMobile(RandomStringUtils.randomNumeric(11));
    user1.setPassword(RandomStringUtils.randomAscii(8));
    
    user1 = userService.register(user1);
    Assert.notNull(user1.getId(), "ID must not be null.");
  }
  
  @Test
  public void testByName() {
    Principal p = userService.byName(UserConstants.ADMIN_USER);
    Assert.notNull(p, "User not exists");  
  }
  
  @Test
  public void test() {
    System.out.println(securityProp.toString());
    
    Resource res = new Resource("/admin");
   
    final String roleName = "user";
    roleService.create(roleName, "", false, 0L);
    
    User user1 = new User();
    
    user1.setUsername(RandomStringUtils.randomAscii(10));
    user1.setMobile(RandomStringUtils.randomNumeric(11));
    user1.setPassword(RandomStringUtils.randomAscii(8));
    
    List<String> roles = new ArrayList<>();
    roles.add(roleName);
    user1 = userService.register(user1, roles);
    
    boolean permitted = userService.isPermitted(user1, res);
    
    Assert.isTrue(!permitted, "Can not access");
    
    permitted = userService.isPermitted(user1, "/js/xyz.js");
    Assert.isTrue(permitted, "Can be accessed");
    
  }
}
