package ch.sbb.workflow.module.lidi.tth.mail;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.mail.BaseNotificationService;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.module.lidi.tth.client.UserAdministrationAdminClient;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TthDossierNotificationService extends BaseNotificationService {

  private final MailProducerService mailProducerService;
  private final UserAdministrationAdminClient userAdministrationAdminClient;

  public void notifyBoAboutNewQuestion(TthDossier dossier) {
    MailNotification mailNotification = MailNotification.builder()
        .to(List.of(dossier.getBoContactMail()))
        .subject("Neues Dossier / Nouveau dossier / Nuovo dossier - \"" + dossier.getTopic() + "\"")
        .mailType(MailType.TTH_DOSSIER_NEW_QUESTION_NOTIFICATION)
        .templateProperties(buildMailProperties(dossier))
        .build();
    mailProducerService.produceMailNotification(mailNotification);
  }

  public void notifyCantonAboutNewAnswer(TthDossier dossier) {
    String creatorMail = userAdministrationAdminClient.getUser(dossier.getCreator()).getMail();

    MailNotification mailNotification = MailNotification.builder()
        .to(List.of(creatorMail))
        .subject("Rückgabe Dossier / Retour du dossier / Restituzione dossier - \"" + dossier.getTopic() + "\"")
        .mailType(MailType.TTH_DOSSIER_NEW_ANSWER_NOTIFICATION)
        .templateProperties(buildMailProperties(dossier))
        .build();
    mailProducerService.produceMailNotification(mailNotification);
  }

  public List<Map<String, Object>> buildMailProperties(TthDossier dossier) {
    List<Map<String, Object>> mailProperties = new ArrayList<>();
    Map<String, Object> mailContentProperty = new HashMap<>();
    mailContentProperty.put("topic", dossier.getTopic());
    mailContentProperty.put("title", "Fahrplananhörung / Consultation sur l’horaire / Consultazione sull’orario");
    mailContentProperty.put("swissCantonName", dossier.getSwissCanton().getName());
    mailContentProperty.put("boDeadlineToAnswer",
        dossier.getBoDeadlineToAnswer().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH)));
    mailContentProperty.put("dossierUrl", buildFrontendUrl(dossier));
    mailProperties.add(mailContentProperty);
    return mailProperties;
  }

  private String buildFrontendUrl(TthDossier dossier) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) +
        "timetable-hearing/" + dossier.getSwissCanton().getAbbreviation().toLowerCase() +
        "/active/dossiers/" + dossier.getId();
  }
}
