package ch.sbb.atlas.base.service.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FakeUserType {

  KAFKA("fxatlka");

  private final String userName;
}
