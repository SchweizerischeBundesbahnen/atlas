package ch.sbb.line.directory.swiss.number;

import java.time.LocalDate;

public interface SwissNumber {

  Long getId();

  LocalDate getValidFrom();

  LocalDate getValidTo();

  SwissNumberDescriptor getSwissNumberDescriptor();

}
