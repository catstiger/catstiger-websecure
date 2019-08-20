package com.github.catstiger.websecure.token;

public interface TokenStrategy {
  /**
   * 对给定的对象进行签名操作
   * @param rawObject 给出被签名的对象，不可为<code>null</code>
   * @return 返回签名后的Token
   */
  String sign(Object rawObject);
  
  /**
   * 验证Token，如果成功，返回sign之前的Object，否则，返回{@code null}
   * @param token sign之后的Token
   * @return 加密之前的Object，如果失败返回{@code null}
   */
  Object verify(String token);
}
