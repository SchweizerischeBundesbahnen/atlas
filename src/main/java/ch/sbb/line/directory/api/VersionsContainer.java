package ch.sbb.line.directory.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VersionsContainer<T> {

  private List<T> versions;
  private long totalCount;

}
