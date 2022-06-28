package ch.sbb.mail.service;

import ch.sbb.mail.model.MailTemplate;
import java.util.List;
import java.util.Map;
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

  public String buildTuImportHtmlContent(List<Map<String,Object>> content) {
    Context context = new Context();
    context.setVariable("content",content);
    return templateEngine.process(MailTemplate.IMPORT_TU_TEMPLATE.getTemplate(), context);
  }

}
