package ch.sbb.workflow.aop;

import lombok.Builder;
import lombok.Data;

@Redacted
@Data
@Builder(toBuilder = true)
class NestedRedactTarget {

  @Redacted(showFirstChar = true)
  private String firstName;

  @Redacted(showFirstChar = true)
  private String lastName;

  private String function;

  @Redacted(showFirstChar = true)
  private String mail;

}