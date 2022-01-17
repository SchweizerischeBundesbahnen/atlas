package ch.sbb.line.directory.controller;

import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class NotFoundException {

  private NotFoundException() {
    // nop
  }

  public static Supplier<ResponseStatusException> getInstance() {
    return () -> new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

}
