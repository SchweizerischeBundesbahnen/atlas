package ch.sbb.atlas.wheelchairaccessibility.model;

import org.assertj.core.presentation.Representation;

public class AccessibilityRepresentation implements Representation {

  public static final AccessibilityRepresentation INSTANCE = new AccessibilityRepresentation();

  @Override
  public String toStringOf(Object object) {
    if (object instanceof Accessibility accessibility) {
      return accessibility.prettyPrint();
    }
    throw new AssertionError();
  }
}