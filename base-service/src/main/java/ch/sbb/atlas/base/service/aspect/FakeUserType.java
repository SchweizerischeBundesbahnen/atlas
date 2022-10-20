package ch.sbb.atlas.base.service.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FakeUserType {

  KAFKA("kafka_system_user");

  private final String userName;
}
