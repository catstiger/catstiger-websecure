package com.github.catstiger.websecure.user.service;

import java.util.List;

import com.github.catstiger.websecure.user.model.Resource;

/**
 * 根据系统中可以访问的URL，构建所有Resource对象。所谓“系统中可以访问的URL”，
 * 通常都定义在MVC框架的配置文件或者类的Annotaion配置中，实现类需要读取这些
 * 配置文件或者Annotation，以生成Resource对象。
 * @author sam
 *
 */
public interface ResourceInitializer {
  /**
   * 查询所有带有@SecureResource标记的Controller类，根据RequestMapping，构建对应的{@link Resource}对象。
   * 类的RequestMapping对应的{@link Resource}对象其{@link Resource#getParentId()}为空，类中方法的RequestMapping
   * 对应的{@link Resource}对象其{@link Resource#getParentId()}为顶级Resource的ID
   * @return
   */
  public abstract List<Resource> build();

}
