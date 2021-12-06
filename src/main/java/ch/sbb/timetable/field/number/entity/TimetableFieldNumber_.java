package ch.sbb.timetable.field.number.entity;

import ch.sbb.timetable.field.number.enumaration.Status;
import java.time.LocalDate;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TimetableFieldNumber.class)
public class TimetableFieldNumber_ {
  public static volatile SingularAttribute<TimetableFieldNumber, String> swissTimetableFieldNumber;
  public static volatile SingularAttribute<TimetableFieldNumber, String> ttfnid;
  public static volatile SingularAttribute<TimetableFieldNumber, String> name;
  public static volatile SingularAttribute<TimetableFieldNumber, Status> status;
  public static volatile SingularAttribute<TimetableFieldNumber, LocalDate> validFrom;
  public static volatile SingularAttribute<TimetableFieldNumber, LocalDate> validTo;
}
