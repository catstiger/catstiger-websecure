package com.github.catstiger.websecure.token;

public interface TokenStorage {
  /**
   * 保存Token，可以保存到HttpSession\Cookie，也可以render到Response，
   */
  void save(String token);
  
  /**
   * 删除保存的Token
   */
  void remove();
  
  /**
   * 从HttpSession\Cookie中得到保存的Token，如果没有，返回null
   */
  String get();
}
