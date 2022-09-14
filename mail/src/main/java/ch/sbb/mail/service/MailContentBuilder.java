package ch.sbb.mail.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.mail.model.MailTemplateConfig;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Component
@Setter
public class MailContentBuilder {

  private static final String ATLAS_SENDER = "TechSupport-ATLAS@sbb.ch";
  public static final String LOGO_SVG = "logo.svg";
  public static final ClassPathResource LOGO_ATALAS_PATH_RESOURCE = new ClassPathResource(
      "images/logo-atlas.svg");

  private final TemplateEngine templateEngine;

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  public void prepareMessageHelper(MailNotification mailNotification, MimeMessage mimeMessage)
      throws MessagingException {
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
        MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
    MailTemplateConfig mailTemplateConfig = MailTemplateConfig.getMailTemplateConfig(
        mailNotification.getMailType());
    messageHelper.addAttachment(LOGO_SVG, LOGO_ATALAS_PATH_RESOURCE);
    messageHelper.setFrom(getFrom(mailTemplateConfig, mailNotification));
    messageHelper.setTo(getTo(mailTemplateConfig,mailNotification));
    messageHelper.setCc(mailNotification.ccAsArray());
    messageHelper.setBcc(mailNotification.bccAsArray());
    messageHelper.setSubject(getSubject(mailTemplateConfig, mailNotification));
    String htmlContent = getHtmlContent(mailTemplateConfig, mailNotification);
    messageHelper.setText(htmlContent, true);
  }

  public String[] getTo(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification){
    if(mailTemplateConfig.getTo() == null && (mailNotification.getTo() == null || mailNotification.getTo().isEmpty())){
      throw new IllegalArgumentException("No receiver defined! You have to provide at least one receiver");
    }
    if(mailNotification.getTo() != null && !mailNotification.getTo().isEmpty()){
      return mailNotification.toAsArray();
    }
    return mailTemplateConfig.getTo();
  }

  String getFrom(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification){
    if(mailTemplateConfig.isFrom() && mailNotification.getFrom() != null && !mailNotification.getFrom().isEmpty()) {
     return mailNotification.getFrom();
    }else {
      return ATLAS_SENDER;
    }
  }

  String getSubject(MailTemplateConfig mailTemplateConfig, MailNotification mailNotification){
    if(mailTemplateConfig.getSubject() == null && mailNotification.getSubject() == null){
      throw new IllegalArgumentException("No Subject defined! You have to provide a Subject");
    }
    if (mailTemplateConfig.getSubject() != null) {
      return getSubjectPrefix() + mailTemplateConfig.getSubject();
    } else {
      return getSubjectPrefix() + mailNotification.getSubject();
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

  private String buildHtmlContent(MailTemplateConfig mailTemplateConfig, String content) {
    Context context = new Context();
    context.setVariable("content", content);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }

  private String buildtHtmlWithProperties(MailTemplateConfig mailTemplateConfig,
      List<Map<String, Object>> properties) {
    Context context = new Context();
    context.setVariable("properties", properties);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }

  private String buildtHtmlWithContentAndTemplateProperties(MailTemplateConfig mailTemplateConfig,
      String content, List<Map<String, Object>> properties) {
    Context context = new Context();
    context.setVariable("content", content);
    context.setVariable("properties", properties);
    return templateEngine.process(mailTemplateConfig.getTemplate(), context);
  }
}
