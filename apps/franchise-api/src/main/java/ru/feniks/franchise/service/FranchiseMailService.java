package ru.feniks.franchise.service;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.feniks.franchise.dto.FranchiseLeadRequest;

@Service
public class FranchiseMailService {

  private static final Logger log = LoggerFactory.getLogger(FranchiseMailService.class);

  private final JavaMailSender mailSender;

  @Value("${app.mail.to}")
  private String mailTo;

  @Value("${app.mail.from:}")
  private String mailFromOverride;

  @Value("${spring.mail.username}")
  private String mailUsername;

  public FranchiseMailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /** Синхронно: до вызова async — чтобы клиент получил 503, если почта не настроена. */
  public void assertMailConfigured() {
    if (!StringUtils.hasText(mailTo)) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Почта получателя не настроена (MAIL_TO)");
    }
  }

  /**
   * Отправка в фоне: HTTP сразу 204, иначе при зависании SMTP прокси Vite часто отдаёт 502.
   */
  @Async
  public void sendLeadNotificationAsync(FranchiseLeadRequest lead) {
    try {
      sendBlocking(lead);
    } catch (Exception e) {
      Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
      if (e instanceof MailAuthenticationException || root instanceof AuthenticationFailedException) {
        log.error(
            "SMTP отказ в входе (часто 535). Логин должен быть полным email (MAIL_USERNAME). "
                + "Пароль MAIL_PASSWORD — точно как в панели Timeweb у этого ящика; лишний «\\» в .env даёт неверный пароль. "
                + "Логин в приложении={}",
            mailUsername);
      }
      log.error(
          "Не удалось отправить письмо по заявке: city={} email={}",
          lead.city(),
          lead.email(),
          e);
    }
  }

  private void sendBlocking(FranchiseLeadRequest lead) throws Exception {
    if (!StringUtils.hasText(mailTo)) {
      throw new IllegalStateException("MAIL_TO is not configured");
    }
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    String[] recipients =
        Arrays.stream(mailTo.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toArray(String[]::new);
    helper.setTo(recipients);
    helper.setSubject("Заявка на франшизу: " + lead.city());
    helper.setFrom(resolveFrom());
    helper.setText(buildBody(lead), false);
    mailSender.send(message);
  }

  private String resolveFrom() {
    if (StringUtils.hasText(mailFromOverride)) {
      return mailFromOverride;
    }
    return mailUsername;
  }

  private static String buildBody(FranchiseLeadRequest lead) {
    return "Новая заявка на франшизу\n\n"
        + "ФИО: "
        + lead.fullName()
        + "\nТелефон: "
        + lead.phone()
        + "\nE-mail: "
        + lead.email()
        + "\nГород: "
        + lead.city()
        + "\n";
  }
}
