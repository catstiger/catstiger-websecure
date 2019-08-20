package com.github.catstiger.websecure.web;

import com.github.catstiger.websecure.subject.Subject;

public final class SubjectHolder {
  private static final ThreadLocal<Subject> subjectHolder = new InheritableThreadLocal<Subject>();
  
  public static void setSubject(Subject subject) {
    subjectHolder.set(subject);
  }
  
  /**
   * 返回当前访问的{@code Subject}对象
   * @return
   */
  public static Subject getSubject() {
    return subjectHolder.get();
  }
  
  /**
   * 删除当前访问的Subject
   */
  public static void clear() {
    subjectHolder.remove();
  }
}
