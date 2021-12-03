package ch.sbb.timetable.field.number.enumaration;

public enum Status {
  ACTIVE, INACTIVE, NEEDS_REVIEW, IN_REVIEW, REVIEWED;

  public boolean isStatus(String statusName) {
    return this.name().equals(statusName);
  }
}
