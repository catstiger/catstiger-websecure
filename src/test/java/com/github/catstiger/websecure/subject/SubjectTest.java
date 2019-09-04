package com.github.catstiger.websecure.subject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.TestApplication;
import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cache.SecureObjectsCache;
import com.github.catstiger.websecure.token.TokenStrategy;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.ResourceService;
import com.github.catstiger.websecure.user.service.RoleService;
import com.github.catstiger.websecure.user.service.UserService;

import jodd.net.HttpMethod;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public class SubjectTest {
  @Autowired
  private TokenStrategy tokenStrategy;
  @Autowired
  private SubjectBuilder subjectBuilder;
  @Autowired
  private RoleService roleService;
  @Autowired
  private UserService userService;
  @Autowired
  private ResourceService resourceService;
  @Autowired
  private SecureObjectsCache secObjCache;
  
  @Before
  public void before() throws Exception {
    secObjCache.clearAll();
    String token = tokenStrategy.sign("admin");
    
    MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "/console/index");
    request.setParameter(SecureConstants.TOKEN_NAME_OF_PARAMETER, token);
    request.setSession(new MockHttpSession());
    request.setCookies(new MockCookie("mock", "mock"));
    
    MockHttpServletResponse response = new MockHttpServletResponse();
    
    Method method = WebObjectsHolder.class.getDeclaredMethod("putRequest", HttpServletRequest.class);
    method.setAccessible(true);
    method.invoke(null, request);
    
    method = WebObjectsHolder.class.getDeclaredMethod("putResponse", HttpServletResponse.class);
    method.setAccessible(true);
    method.invoke(null, response);
  }
  
  @Test
  public void testHolder() {
    Subject subject = subjectBuilder.build();
    Principal principal = subject.getPrincipal();
    Assert.notNull(principal, "Principal must not be null.");
    Assert.isTrue(subject.isPermitted("/admin"), "Admin 可以访问 /admin");
    
    roleService.create("role1", "", false);
    roleService.create("role2", "", false);
    
    userService.grant("admin", "role1");
    Collection<Role> rs = userService.getRolesByUser((User) principal);
    Assert.isTrue(rs.size() == 2, "Get roles by user");
    System.out.println(JSON.toJSONString(rs, true));
    Assert.isTrue(subject.hasAllRoles(Arrays.asList("administrator", "role1")), "Test has All roles");
    
    Resource r2 = resourceService.create("/console/index**", "");
    Assert.isTrue(resourceService.implies(r2, "/console/index?a=1"), "Test implies.");
    roleService.grant("role2", "/console/index**");
    
    Assert.isTrue(!subject.isPermitted("/admin"), "Admin 不可以访问 /admin");
    Assert.isTrue(!subject.isPermitted("/console/index?x=2"), "Admin 不可以访问 /console/index?x=2");
    
    userService.grant("admin", "role2");
    Assert.isTrue(subject.isPermitted("/console/index?x=2"), "Admin 可以访问 /console/index?x=2");
    
  }
  
}
