package ch.sbb.atlas.pdf.sanitize;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfCdrResult {

  private final List<String> performedActions = new ArrayList<>();
  private Exception occurredException;

  public void addPerformedAction(String action) {
    performedActions.add(action);
  }

}
