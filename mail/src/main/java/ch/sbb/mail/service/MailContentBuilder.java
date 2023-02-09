package ch.sbb.mail.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.mail.model.MailTemplateConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Component
@Setter
public class MailContentBuilder {

  public static final String LOGO = "logo";
  private static final ClassPathResource LOGO_ATALAS_PATH_RESOURCE = new ClassPathResource(
      "images/logo-atlas.jpeg");
  private static final String ATLAS_SENDER = "TechSupport-ATLAS@sbb.ch";
  private final TemplateEngine templateEngine;

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  public void prepareMessageHelper(MailNotification mailNotification, MimeMessage mimeMessage)
      throws MessagingException {
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, UTF_8.name());
    MailTemplateConfig mailTemplateConfig = MailTemplateConfig.getMailTemplateConfig(
        mailNotification.getMailType());
    messageHelper.setFrom(getFrom(mailTemplateConfig, mailNotification));
    messageHelper.setTo(getTo(mailTemplateConfig, mailNotification));
    messageHelper.setCc(mailNotification.ccAsArray());
    messageHelper.setBcc(mailNotification.bccAsArray());
    messageHelper.setSubject(getSubject(mailTemplateConfig, mailNotification));
    String htmlContent = getHtmlContent(mailTemplateConfig, mailNotification);
    messageHelper.setText(htmlContent, true);
    final InputStreamSource logoInputStreamSource = getLogoInputStreamSource();
    messageHelper.addInline(LOGO, logoInputStreamSource, "image/jpeg");
  }

  public String[] getTo(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification) {
    if (mailTemplateConfig.getTo() == null && (mailNotification.getTo() == null || mailNotification.getTo().isEmpty())) {
      throw new IllegalArgumentException("No receiver defined! You have to provide at least one receiver");
    }
    if (mailNotification.getTo() != null && !mailNotification.getTo().isEmpty()) {
      return mailNotification.toAsArray();
    }
    return mailTemplateConfig.getTo();
  }

  String getFrom(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification) {
    if (mailTemplateConfig.isFrom() && mailNotification.getFrom() != null && !mailNotification.getFrom().isEmpty()) {
      return mailNotification.getFrom();
    } else {
      return ATLAS_SENDER;
    }
  }

  String getSubject(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification) {
    if (mailTemplateConfig.getSubject() == null && mailNotification.getSubject() == null) {
      throw new IllegalArgumentException("No Subject defined! You have to provide a Subject");
    }
    if (mailTemplateConfig.getSubject() != null) {
      return getSubjectPrefix() + mailTemplateConfig.getSubject();
    } else {
      return getSubjectPrefix() + mailNotification.getSubject();
    }
  }

  String getHtmlContent(MailTemplateConfig mailTemplateConfig,
      MailNotification mailNotification) {
    if (mailTemplateConfig.isContent() && mailTemplateConfig.isTemplateProperties()) {
      return buildtHtmlWithContentAndTemplateProperties(mailTemplateConfig,
          mailNotification.getContent(), mailNotification.getTemplateProperties());
    } else if (mailTemplateConfig.isContent()) {
      return buildHtmlContent(mailTemplateConfig, mailNotification.getContent());
    } else {
      return buildtHtmlWithProperties(mailTemplateConfig, mailNotification.getTemplateProperties());
    }
  }

  private String getSubjectPrefix() {
    String subjectPrefix = "[ATLAS";
    if (!activeProfile.equals("prod")) {
      subjectPrefix += "-" + activeProfile.toUpperCase();
    }
    subjectPrefix += "] ";
    return subjectPrefix;
  }

  private String buildHtmlContent(MailTemplateConfig mailTemplateConfig, String content) {
    Context context = new Context();
    context.setVariable("content", content);
    context.setVariable(LOGO, LOGO);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }

  private String buildtHtmlWithProperties(MailTemplateConfig mailTemplateConfig,
      List<Map<String, Object>> properties) {
    Context context = new Context();
    context.setVariable("properties", properties);
    context.setVariable(LOGO, LOGO);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }

  private String buildtHtmlWithContentAndTemplateProperties(MailTemplateConfig mailTemplateConfig,
      String content, List<Map<String, Object>> properties) {
    Context context = new Context();
    context.setVariable("content", content);
    context.setVariable("properties", properties);
    context.setVariable(LOGO, LOGO);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }

  private InputStreamSource getLogoInputStreamSource() {
    try (InputStream inputStream = LOGO_ATALAS_PATH_RESOURCE.getInputStream()) {
      return new ByteArrayResource(inputStream.readAllBytes());
    } catch (IOException e) {
      throw new IllegalArgumentException("The given path resource is wrog!", e);
    }
  }
}
