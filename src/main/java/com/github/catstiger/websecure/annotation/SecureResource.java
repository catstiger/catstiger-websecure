package com.github.catstiger.websecure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于描述一个可以被HTTP协议访问的资源或者方法，指出这个类或者方法的文字描述，以及是否被导出为WebSecure管理的URL资源
 * @author catstiger@gmail.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface SecureResource {
  /**
   * 类或者方法的文字描述，便于管理
   */
  String value() default "";
  
  /**
   * 是否忽略，如果为true，则不导出为URL资源
   */
  boolean ignored() default false;
}
