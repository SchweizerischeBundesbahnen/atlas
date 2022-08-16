package ch.sbb.line.directory.api;

import ch.sbb.atlas.model.api.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Future Timetable")
@RequestMapping("v1/future-timetable")
@Validated
public interface FutureTimetableApiV1 {

  @GetMapping("{year}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", description = "Param argument not valid error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  LocalDate getFutureTimetable(@PathVariable @Min(1700) @Max(9999) int year);

  @GetMapping("/next-years/{count}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", description = "Param argument not valid error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  List<LocalDate> getNextYearsFutureTimetables(@PathVariable @Min(1) @Max(100) int count);

}
