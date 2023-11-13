package ch.sbb.atlas.imports;

import java.time.LocalDateTime;

public interface Importable {

  LocalDateTime getEditionDate();

  String getEditor();

}
