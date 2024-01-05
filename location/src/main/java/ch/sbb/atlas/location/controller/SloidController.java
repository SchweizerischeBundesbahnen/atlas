package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SloidController implements SloidApiV1 {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel generateSloidRequestModel) {
    final String sloidPrefix = generateSloidRequestModel.sloidPrefix();
    switch (generateSloidRequestModel.sloidType()) {
      case AREA -> {
        Integer counter = null;
        try {
          counter = jdbcTemplate.queryForObject("select child_seq from sloid_allocated where sloid = ?;",
              Integer.class,
              sloidPrefix);
        } catch (DataAccessException e) {
          log.info("prefix does not exist or counter not defined");
        }

        if (counter == null) {
          return ResponseEntity.badRequest().build();
        }

        while (true) {
          try {
            counter++;
            jdbcTemplate.update("insert into sloid_allocated (sloid, child_seq) values (?, ?);",
                sloidPrefix + ":" + counter, counter);
            return ResponseEntity.ok(sloidPrefix + ":" + counter);
          } catch (DataAccessException e) {
            log.error("sloid already allocated: ", e);
          }
        }
      }
    }

    throw new RuntimeException("no switch");
  }
}

// todo: sloid überall eindeutig ausser platform = kante
// todo: patterns sepodi (location, bereich, kante), patterns prm (platform = kante, rest 4 doppelpunkte eindeutig)
// todo: <location> von available tabelle, <bereich> und <kante> je eine sequence (überlegen wie sequence reset, damit nichts
//  übersprungen wird.

// todo: direkt inserts anstatt exists prüfen und error handling (bei manuell returnen, bei automatic weiter versuchen)
