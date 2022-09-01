package ch.sbb.line.directory.api;

import ch.sbb.atlas.model.api.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Timetable year change", description = "Generate the Timetable year change based on the [Official Documentation](https://www.fahrplanfelder.ch/en/explanations/timetable-year.html)")
@RequestMapping("v1/timetable-year-change")
@Validated
public interface TimetableYearChangeApiV1 {

  @GetMapping("{year}")
  @Operation(description = "Returns the Timetable year change for the given year")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", description = "Param argument not valid error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  LocalDate getTimetableYearChange(@PathVariable @Min(1700) @Max(9999) int year);

  @GetMapping("/next-years/{count}")
  @Operation(description = "Returns a list of the next Timetable years change")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", description = "Param argument not valid error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  List<LocalDate> getNextTimetablesYearChange(@PathVariable @Min(1) @Max(100) int count);

}
