package com.github.catstiger.websecure.login.listener;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.user.model.User;

@Service
public class UserLoginSuccessListener implements LoginSuccessListener {
  private static Logger logger = LoggerFactory.getLogger(UserLoginSuccessListener.class);
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  @Transactional
  public void onSuccess(Subject subject) {
    if (subject == null || !(subject.getPrincipal() instanceof User)) {
      logger.error("Principal is null or not a User");
      return;
    }

    User user = (User) subject.getPrincipal();
    jdbcTemplate.update("update users set host=?, login_time=? where username=?", WebObjectsHolder.getRequest().getRemoteHost(), DateTime.now().toDate(),
        user.getUsername());
  }

}
