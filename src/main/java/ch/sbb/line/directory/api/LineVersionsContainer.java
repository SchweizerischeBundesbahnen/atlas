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
public class LineVersionsContainer {

  private List<LineVersionModel> versions;
  private long totalCount;

}
