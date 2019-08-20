package com.github.catstiger.websecure.user.service;

import com.github.catstiger.websecure.user.model.Resource;

public interface ResourceService {
  /**
   * 根据URL(对于web安全来说，就是URL)，返回Resource的实例，如果没有对应的实例，返回{@code null}
   * @param url 给出URL
   * @return 根据URL(对于web安全来说，就是URL)，返回Resource的实例，如果没有对应的实例，返回{@code null}
   */
  Resource byUrl(String url);
  
  /**
   * 创建一个Resource的实例
   * @param url URL代表了一个或者多个资源，可以使用wildcard.
   * @param descn 本资源的描述
   * @return Instance of the resource.
   */
  Resource create(String url, String descn);
  
  
  /**
   * 删除一个Resource
   * @param url URL代表了一个或者多个资源，可以使用wildcard.
   */
  void remove(String url);
  
  /**
   * 判断一个Resource/Permision是否隐喻另一个Resource/Permission, 例如：<p/>
   * /users/**，隐喻了/users/detail/**，即具有前一个Permission的授权，即具有了后一个Permission的授权。
   * @param resource 给出源Resource/Permision
   * @param target 给出目标URL
   * @return 判断一个Resource/Permision是否隐喻另一个Resource/Permission, 如果是，返回{@code true},否则返回{@code false}
   */
  boolean implies(Resource resource, String target);
  
  /**
   * 重新加载所有URL资源
   */
  void build();
  
}
