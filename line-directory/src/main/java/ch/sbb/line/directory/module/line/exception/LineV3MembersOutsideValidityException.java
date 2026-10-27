package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * V3: after updating a line, members of it would lie (partly) outside the line's new overall validity, e.g. because the
 * line was shortened before the start of a member's last version or validity and other fields were changed together.
 */
@Getter
@RequiredArgsConstructor
public class LineV3MembersOutsideValidityException extends AtlasException {

  private static final String CODE = "LIDI.LINE.CONFLICT.MEMBERS_OUTSIDE_VALIDITY";
  private static final String MESSAGE = "Member {0} would lie outside of the line {1} validity from {2} to {3}";

  private final String lineSlnid;
  private final transient DateRange lineValidity;
  private final List<String> memberSlnids;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error("Members " + String.join(", ", memberSlnids) + " would lie outside of the line " + lineSlnid
            + " validity from " + lineValidity.getFrom() + " to " + lineValidity.getTo())
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    return memberSlnids.stream()
        .map(memberSlnid -> Detail.builder()
            .field(Fields.validTo)
            .message(MESSAGE)
            .displayInfo(builder()
                .code(CODE)
                .with("memberSlnid", memberSlnid)
                .with(Fields.slnid, lineSlnid)
                .with(Fields.validFrom, lineValidity.getFrom())
                .with(Fields.validTo, lineValidity.getTo())
                .build()).build())
        .collect(Collectors.toCollection(TreeSet::new));
  }

}
