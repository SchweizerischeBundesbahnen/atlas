package ch.sbb.atlas.user.administration.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ApplicationRole {
  WRITER,SUPER_USER,ADMIN
}
