package ch.sbb.atlas.kafka.model.mail;

import lombok.Getter;

@Getter
public enum MailType {

  ATLAS_STANDARD,
  TU_IMPORT,
  SCHEDULING_ERROR_NOTIFICATION,

  WORKFLOW_NOTIFICATION

}
