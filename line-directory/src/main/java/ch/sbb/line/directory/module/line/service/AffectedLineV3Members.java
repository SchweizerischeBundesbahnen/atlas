package ch.sbb.line.directory.module.line.service;

import java.util.List;

/**
 * Members (slnids) of a V3 line whose validity would be affected by shortening the line; V3 port of
 * {@link ch.sbb.atlas.api.lidi.AffectedSublinesModel}.
 */
public record AffectedLineV3Members(List<String> allowedMembers, List<String> notAllowedMembers) {

  public boolean isEmpty() {
    return allowedMembers.isEmpty() && notAllowedMembers.isEmpty();
  }

  public boolean hasAllowedMembers() {
    return !allowedMembers.isEmpty();
  }

  public boolean hasNotAllowedMembers() {
    return !notAllowedMembers.isEmpty();
  }
}
