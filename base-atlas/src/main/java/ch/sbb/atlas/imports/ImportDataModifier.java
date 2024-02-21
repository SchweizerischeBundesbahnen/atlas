package ch.sbb.atlas.imports;

import java.time.LocalDate;

public interface ImportDataModifier {

  void setLastModifiedToNow();

  LocalDate getValidTo();

  void setValidTo(LocalDate localDate);

}
