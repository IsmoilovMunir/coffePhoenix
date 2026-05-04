package ru.feniks.franchise.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * При 535 в логах SMTP — убедиться, что пароль реально подставился (длина больше 0) и логин — полный email.
 */
@Component
public class MailStartupDiagnostics {

  private static final Logger log = LoggerFactory.getLogger(MailStartupDiagnostics.class);

  @Value("${spring.mail.host:}")
  private String host;

  @Value("${spring.mail.port:0}")
  private int port;

  @Value("${spring.mail.username:}")
  private String username;

  @Value("${spring.mail.password:}")
  private String password;

  @EventListener(ApplicationReadyEvent.class)
  public void logMailBinding() {
    int passLen = password != null ? password.length() : 0;
    if (!StringUtils.hasText(username) || passLen == 0) {
      log.warn(
          "Почта: в Spring не подставились логин/пароль (user пустой={}, длина пароля={}). "
              + "Проверьте .env (MAIL_USERNAME, MAIL_PASSWORD) и перезапуск после правок.",
          !StringUtils.hasText(username),
          passLen);
    } else {
      log.info(
          "Почта: SMTP {}:{} , пользователь={}, длина пароля={} (символов)",
          host,
          port,
          username,
          passLen);
    }
  }
}
