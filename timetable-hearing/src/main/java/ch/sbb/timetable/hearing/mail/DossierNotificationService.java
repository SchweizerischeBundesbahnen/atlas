package ch.sbb.timetable.hearing.mail;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.helper.AtlasFrontendBaseUrl;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.timetable.hearing.client.UserAdministrationAdminClient;
import ch.sbb.timetable.hearing.entity.Dossier;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DossierNotificationService {

  @Value("${spring.profiles.active:local}")
  protected String activeProfile;

  @Value("${mail.workflow.stop-point.from}")
  protected String from;

  private final MailProducerService mailProducerService;
  private final UserAdministrationAdminClient userAdministrationAdminClient;

  public void notifyBoAboutNewQuestion(Dossier dossier) {
    MailNotification mailNotification = MailNotification.builder()
        .to(List.of(dossier.getBoContactMail()))
        .subject("Neues Dossier / Nouveau dossier / Nuovo dossier - \"" + dossier.getTopic() + "\"")
        .mailType(MailType.TTH_DOSSIER_NEW_QUESTION_NOTIFICATION)
        .templateProperties(buildMailProperties(dossier))
        .build();
    mailProducerService.produceMailNotification(mailNotification);
  }

  public void notifyCantonAboutNewAnswer(Dossier dossier) {
    String creatorMail = userAdministrationAdminClient.getUser(dossier.getCreator()).getMail();

    MailNotification mailNotification = MailNotification.builder()
        .to(List.of(creatorMail))
        .subject("Rückgabe Dossier / Retour du dossier / Restituzione dossier - \"" + dossier.getTopic() + "\"")
        .mailType(MailType.TTH_DOSSIER_NEW_ANSWER_NOTIFICATION)
        .templateProperties(buildMailProperties(dossier))
        .build();
    mailProducerService.produceMailNotification(mailNotification);
  }

  public List<Map<String, Object>> buildMailProperties(Dossier dossier) {
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

  private String buildFrontendUrl(Dossier dossier) {
    return AtlasFrontendBaseUrl.getUrl(activeProfile) +
        "timetable-hearing/" + dossier.getSwissCanton().getAbbreviation().toLowerCase() +
        "/active/dossiers/" + dossier.getId();
  }
}
