package com.github.catstiger.websecure.login.sync;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.user.model.User;

/**
 * 支持分布式条件下，同一个用户只能同时在一个客户端登陆的要求，后登陆的用户会踢出先前登陆的
 * 
 * @author leesam
 *
 */
@Component
public class RedisSessionSynchronizer implements SessionSynchronizer {
	private static Logger logger = LoggerFactory.getLogger(RedisSessionSynchronizer.class);
	public static final String LAST_USER_SESSION_KEY = "login_binding_local_session";
	public static final String LOGIN_TOPIC = "LOGIN_TOPIC_";

	private static final Map<String, List<LocalSessionRecord>> localSessions = new ConcurrentHashMap<>();
	@Autowired
	private RedissonClient redis;
	@Autowired
	private SecurityProperties config;

	/**
	 * 初始化，建立同步session监听器
	 */
	@PostConstruct
	public void initialize() {
		if (!config.isSyncLogin()) {
			return;
		}
		logger.debug("建立用户登录同步监听。");
		RTopic topic = redis.getTopic(LOGIN_TOPIC);
		topic.addListener(LoginRecord.class, new MessageListener<LoginRecord>() {
			@Override
			public void onMessage(CharSequence channel, LoginRecord message) {
				logger.debug("用户登录消息收到 {}", message);
				if (message != null) {
					if (localSessions.containsKey(message.getUsername())) {
						List<LocalSessionRecord> lsrs = localSessions.get(message.getUsername());
						if (CollectionUtils.isEmpty(lsrs)) {
							return;
						}
						logger.debug("已经登录的用户 {}", lsrs.size());
						for (Iterator<LocalSessionRecord> itr = lsrs.iterator(); itr.hasNext();) {
							LocalSessionRecord lsr = itr.next();
							// 一定是之前保存的session才能invalidate，否则会踢出刚刚登陆的那个
							if (lsr.timestamp < message.timestamp) {
								logger.debug("根据Topic消息，踢出登录用户{} (后), {}(前)", message, lsr);
								lsr.session.invalidate();
								lsrs.remove(lsr); // 删除对应的登录记录
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void apply(Principal principal) {
		if (principal == null || !(principal instanceof User)) {
			throw new IllegalStateException("Login principal not exists");
		}
		HttpServletRequest request = WebObjectsHolder.getRequest();
		if (request == null) {
			throw new IllegalStateException("未找到Request，请确认WebSecureFilter正确配置");
		}
		logger.debug("应用Session同步机制..");
		if (config.isRecordLogin()) {
			logger.debug("记录用户成功登陆。");
			RedisStoreSessionBindingListener sessionBindingListener = new RedisStoreSessionBindingListener(principal.getName(), request.getRemoteAddr(), config.isSyncLogin(), DateTime.now().getMillis());
			HttpSession session = request.getSession();
			if (session != null) {
				session.setAttribute(LAST_USER_SESSION_KEY, sessionBindingListener);
			}
		}
	}

	@SuppressWarnings("serial")
	public static class RedisStoreSessionBindingListener implements HttpSessionBindingListener, Serializable {
		private static Logger log = LoggerFactory.getLogger(RedisStoreSessionBindingListener.class);
		private String username;
		private String ip;
		private boolean syncLogin;
		// private RedissonClient redisson;
		private long timestamp;

		public RedisStoreSessionBindingListener() {

		}

		public RedisStoreSessionBindingListener(String username, String ip, boolean syncLogin, long timestamp) {
			this.username = username;
			this.syncLogin = syncLogin;
			// this.redisson = redisson;
			this.timestamp = timestamp;
			this.ip = ip;
		}

		@Override
		public void valueBound(HttpSessionBindingEvent event) {
			log.debug("用户登录绑定...");
			WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getSession().getServletContext());
			RedissonClient redisson = ctx.getBean(RedissonClient.class);
			if (syncLogin) {
				RTopic topic = redisson.getTopic(LOGIN_TOPIC);
				LoginRecord loginRecord = new LoginRecord(username, timestamp, ip);

				long count = topic.publish(loginRecord);
				log.debug("通知用户同步消息。");
				log.debug("{} clients that received the message. ", count);
			}
			log.debug("记录用户登录session {}", username);
			// 将Session和timestamp存在本地，timestamp确保刚刚进入的不会被踢出
			List<LocalSessionRecord> lsrs = localSessions.get(username);
			if (lsrs == null) {
				lsrs = new LinkedList<LocalSessionRecord>();
			}
			lsrs.add(new LocalSessionRecord(event.getSession(), username, timestamp));
			localSessions.put(username, lsrs);
		}

		@Override
		public void valueUnbound(HttpSessionBindingEvent event) {
			log.debug("{} 登出!", username);
			if (localSessions.containsKey(username)) {
				List<LocalSessionRecord> lsrs = localSessions.get(username);
				if (CollectionUtils.isEmpty(lsrs)) {
					return;
				}
				for (Iterator<LocalSessionRecord> itr = lsrs.iterator(); itr.hasNext();) {
					LocalSessionRecord lsr = itr.next();
					if (Objects.equals(username, lsr.username) && lsr.timestamp == timestamp) {
						logger.debug("删除本地登录记录。");
						lsrs.remove(lsr);
						break;
					}
				}
			}
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public boolean isSyncLogin() {
			return syncLogin;
		}

		public void setSyncLogin(boolean syncLogin) {
			this.syncLogin = syncLogin;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	}

	/**
	 * 用于发送Redis消息，作为消息体发送
	 * 
	 * @author leesam
	 *
	 */
	@SuppressWarnings("serial")
	public static class LoginRecord implements Serializable {
		private String username;
		private Long timestamp;
		private String ip;

		public LoginRecord() {

		}

		/**
		 * 用用户名， 时间戳， IP，构建LoginRecord
		 */
		public LoginRecord(String username, Long timestamp, String ip) {
			this.username = username;
			this.timestamp = timestamp;
			this.ip = ip;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		@Override
		public String toString() {
			return "LoginRecord [username=" + username + ", timestamp=" + timestamp + "]";
		}
	}

	/**
	 * 用于在本地保存HttpSession
	 * 
	 * @author leesam
	 *
	 */
	@SuppressWarnings("serial")
	public static class LocalSessionRecord implements Serializable {
		private String username;
		private HttpSession session;
		private Long timestamp;

		public LocalSessionRecord() {
		}

		/**
		 * 用HttpSession, 用户名， 时间戳构建一个LocalSessionRecord对象
		 */
		public LocalSessionRecord(HttpSession session, String username, Long timestamp) {
			this.session = session;
			this.username = username;
			this.timestamp = timestamp;
		}

		public HttpSession getSession() {
			return session;
		}

		public void setSession(HttpSession session) {
			this.session = session;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
			result = prime * result + ((username == null) ? 0 : username.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			LocalSessionRecord other = (LocalSessionRecord) obj;
			if (timestamp == null) {
				if (other.timestamp != null) {
					return false;
				}
			} else if (!timestamp.equals(other.timestamp)) {
				return false;
			}
			if (username == null) {
				if (other.username != null) {
					return false;
				}
			} else if (!username.equals(other.username)) {
				return false;
			}
			return true;
		}
	}

}
