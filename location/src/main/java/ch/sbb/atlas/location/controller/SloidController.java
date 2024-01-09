package ch.sbb.atlas.location.controller;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.location.SloidApiV1;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SloidController implements SloidApiV1 {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public ResponseEntity<String> generateSloid(GenerateSloidRequestModel request) {
    final String sloidPrefix = request.sloidType().getSloidPrefix(request.sloidPrefix());
    final String seqName = request.sloidType().getSeqName();
    String generatedSloid;
    do {
      generatedSloid = tryToGenerateNewSloid(sloidPrefix, seqName);
    } while (generatedSloid == null);
    return ResponseEntity.ok(generatedSloid);
  }

  @Override
  public ResponseEntity<String> claimSloid(ClaimSloidRequestModel request) {
    try {
      jdbcTemplate.update("insert into sloid_allocated (sloid) values (?);", request.sloid());
      return ResponseEntity.ok(request.sloid());
    } catch (DataAccessException e) {
      log.info("{} occupied", request.sloid());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(request.sloid() + " is already used.");
    }
  }

  private String tryToGenerateNewSloid(String sloidPrefix, String seqName) {
    final Integer nextSeqValue = jdbcTemplate.queryForObject("select nextval(?);", Integer.class, seqName);
    final String sloid = sloidPrefix + ":" + nextSeqValue;
    try {
      jdbcTemplate.update("insert into sloid_allocated (sloid) values (?);", sloid);
      return sloid;
    } catch (DataAccessException e) {
      log.info("{} occupied", sloid);
      return null;
    }
  }
}
