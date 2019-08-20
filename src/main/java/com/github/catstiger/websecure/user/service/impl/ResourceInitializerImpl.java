package com.github.catstiger.websecure.user.service.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.catstiger.websecure.annotation.SecureResource;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.service.ResourceInitializer;
import com.google.common.base.Splitter;

@Service
public class ResourceInitializerImpl implements ResourceInitializer {
  private static Logger logger = LoggerFactory.getLogger(ResourceInitializerImpl.class);

  @Autowired
  @Value(value = "classpath*:**/controller/*Controller.class")
  private org.springframework.core.io.Resource[] classResources;

  @Override
  public List<Resource> build() {
    List<Class<?>> controllers = new ArrayList<>(180);
    // 加载所有Controller类
    String classRoot = fixTest(getClass().getResource("/").getPath());

    for (org.springframework.core.io.Resource classResource : classResources) {
      buildResource(classResource, classRoot, controllers);
    }
    
    List<Resource> modules = new ArrayList<Resource>(controllers.size());

    for (Class<?> controller : controllers) {
      RequestMapping ns = controller.getAnnotation(RequestMapping.class);
      if (ns == null) {
        continue;
      }
      SecureResource moduleRes = controller.getAnnotation(SecureResource.class);
      // 标记为ignore的资源
      if (moduleRes == null || (moduleRes != null && moduleRes.ignored())) {
        continue;
      }

      Resource module = new Resource();
      module.setDescn(moduleRes.value());
      String url;
      if (ns.value() == null || ns.value().length == 0) {
        url = "/";
      } else {
        url = ns.value()[0].endsWith("/") ? ns.value()[0] : ns.value()[0] + "/"; // @RequestMapping中定义的URL
      }
      module.setUrl(url + "**");

      List<Resource> children = buildByController(controller, module, ns.value()[0]);
      module.getChildren().addAll(children);

      modules.add(module);
    }

    return modules;
  }
  
  private void buildResource(org.springframework.core.io.Resource classResource, String classRoot, List<Class<?>> controllers) {
    try {
      String classFile = classResource.getURL().getPath();
      String className = StringUtils.replace(classFile, classRoot, "");
      className = StringUtils.replace(className, "/", ".");
      className = StringUtils.replace(className, ".class", "");
      Class<?> actionClass;
      try {
        actionClass = ClassUtils.forName(className, getClass().getClassLoader());
        controllers.add(actionClass);
        logger.debug("Adding controller {}", className);
      } catch (Exception e) {
        e.printStackTrace();
      } catch (LinkageError e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 修正单元测试中，classpath最后一个path 为test-classes的问题
   */
  private String fixTest(String original) {
    final String testFolder = "test-classes";
    final String classFolder = "classes";

    List<String> folders = Splitter.on("/").trimResults().splitToList(original);
    if (testFolder.equals(folders.get(folders.size() - 1)) || testFolder.equals(folders.get(folders.size() - 2))) {
      return StringUtils.replace(original, testFolder, classFolder);
    }

    return original;
  }

  /**
   * 导入一个Action类中所有的URL
   */
  @SuppressWarnings("rawtypes")
  private List<Resource> buildByController(Class controller, Resource parent, String baseUrl) {
    Method[] methods = controller.getDeclaredMethods();
    List<Resource> resources = new ArrayList<Resource>();

    for (Method method : methods) {
      RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
      if (methodMapping == null) {
        continue;
      }
      // 描述信息
      SecureResource methodRes = method.getAnnotation(SecureResource.class);
      // 标记为ignore的方法
      if (methodRes == null || (methodRes != null && methodRes.ignored())) {
        continue;
      }
      Resource res = new Resource();
      res.setDescn(methodRes.value());
      StringBuilder url = new StringBuilder(50);
      url.append(baseUrl);
      url.append(baseUrl.endsWith("/") ? "" : "/");
      String subUrl = ArrayUtils.isEmpty(methodMapping.value()) ? "" : methodMapping.value()[0];
      if (subUrl.startsWith("/")) {
        subUrl = subUrl.substring(1);
      }
      url.append(subUrl).append("**");

      res.setUrl(url.toString());
      res.setParentId(parent.getId());

      resources.add(res);
    }

    return resources;
  }

}
