package ch.sbb.line.directory.swiss.number;

import java.time.LocalDate;

public interface SwissNumber {

  LocalDate getValidFrom();

  LocalDate getValidTo();

  SwissNumberDescriptor getSwissNumber();

}
