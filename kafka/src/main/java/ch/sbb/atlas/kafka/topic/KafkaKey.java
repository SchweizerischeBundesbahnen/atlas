package ch.sbb.atlas.kafka.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KafkaKey {

  MAIL("mail"),
  WORKFLOW("workflow");

  private final String value;

}
