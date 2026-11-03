package ch.sbb.line.directory.module.line.security;

/**
 * Write permission required for a single field of {@link ch.sbb.line.directory.module.line.entity.LineVersionV3}. Fields
 * without a {@link FieldPermission} annotation require the regular LIDI write permission on the business organisation of the
 * line.
 */
public enum LineV3FieldPermission {

  CABLEWAY,
  FINANCING

}
