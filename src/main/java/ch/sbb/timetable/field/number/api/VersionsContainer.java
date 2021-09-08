package ch.sbb.timetable.field.number.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VersionsContainer {

  private List<VersionModel> versions;
  private long totalCount;

}
