package ch.sbb.mail.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
@Component
public class MailContentBuilder {

  private final TemplateEngine templateEngine;

  public String buildHtmlContent(String message) {
    Context context = new Context();
    context.setVariable("message", message);
    return templateEngine.process("mail-html-template", context);
  }

  public String buildPlainTextContent(String message) {
    Context context = new Context();
    context.setVariable("message", message);
    return templateEngine.process("mail-plaintext-template.txt", context);
  }

}
