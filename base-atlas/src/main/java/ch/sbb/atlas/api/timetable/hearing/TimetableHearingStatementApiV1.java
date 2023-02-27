package ch.sbb.atlas.api.timetable.hearing;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Timetable Hearing")
@RequestMapping("v1/timetable-hearing/statements")
public interface TimetableHearingStatementApiV1 {

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  TimetableHearingStatementModel createStatement(
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents);

  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  TimetableHearingStatementModel updateStatement(
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents
  );

}
