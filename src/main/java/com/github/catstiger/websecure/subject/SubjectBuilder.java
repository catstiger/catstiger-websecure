package com.github.catstiger.websecure.subject;

public interface SubjectBuilder {
  /**
   * 创建一个Subject。{@code SubjectBuilder}通常用于在用户登录后，从Session\Cookie中创建一个
   * 已经认证成功的{@code Subject}对象。
   * <p/>
   * 类似的，LoginSevice用于从未登录username/password中创建并认证一个Subject
   * @return Subject 如果不成功，返回{@code null}
   */
  Subject build();
  
}
