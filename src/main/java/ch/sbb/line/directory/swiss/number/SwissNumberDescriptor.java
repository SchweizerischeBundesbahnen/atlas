package ch.sbb.line.directory.swiss.number;

import java.util.function.Supplier;

public class SwissNumberDescriptor {

  private final String name;
  private final Supplier<String> value;

  public SwissNumberDescriptor(String name, Supplier<String> value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value.get();
  }
}