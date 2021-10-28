package ch.sbb.line.directory.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConflictExcpetion extends ResponseStatusException {

    public ConflictExcpetion() {
      super(HttpStatus.CONFLICT, "SwissNumber already taken in specified period");
    }

  }