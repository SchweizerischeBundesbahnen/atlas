package ch.sbb.line.directory.module.line.service;

import ch.sbb.line.directory.module.line.entity.LineVersionV3;

/**
 * Detached copies of a member version before ({@code currentVersion}) and after ({@code editedVersion}) shortening, to be
 * passed to the member versioning update.
 */
public record LineV3MemberShortening(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {

}
