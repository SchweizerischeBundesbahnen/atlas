package ch.sbb.line.directory.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SublineType {
  TECHNICAL, COMPENSATION, CONCESSION
}
