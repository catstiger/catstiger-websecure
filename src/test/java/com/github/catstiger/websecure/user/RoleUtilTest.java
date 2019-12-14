package com.github.catstiger.websecure.user;

import java.util.Objects;

import org.junit.Test;
import org.springframework.util.Assert;

import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.service.RoleUtil;

public class RoleUtilTest {
  
  @Test
  public void test() {
    Role r = new Role("role_admin");   
    r.setCorpId(100L);
    System.out.println(RoleUtil.wrapName(r));
    Assert.isTrue(Objects.equals("role_admin@100", RoleUtil.wrapName(r)), "");
    
    r.setName(RoleUtil.wrapName(r));
    Assert.isTrue(Objects.equals("role_admin@100", RoleUtil.wrapName(r)), "");
    
    System.out.println(RoleUtil.unwrapName(r));
    Assert.isTrue(Objects.equals("role_admin", RoleUtil.unwrapName(r)), "");
  }
}
