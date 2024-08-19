package ch.sbb.atlas.imports.bulk;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
@Data
public class BulkImportUpdateContainer<T> implements BulkImportContainer {

  private final int lineNumber;
  private final T object;
  private final List<String> attributesToNull;
}
