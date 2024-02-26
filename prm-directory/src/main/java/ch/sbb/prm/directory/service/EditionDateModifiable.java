package ch.sbb.prm.directory.service;

import java.time.LocalDateTime;

/**
 * @deprecated To be deleted with BasePrmImportEntity
 */
@Deprecated(since = "2.0.0")
public interface EditionDateModifiable {

  void setEditionDate(LocalDateTime editionDate);

  void setEditor(String editor);
}
