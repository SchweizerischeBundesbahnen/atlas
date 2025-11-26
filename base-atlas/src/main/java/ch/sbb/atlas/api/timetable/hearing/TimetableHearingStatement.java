package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.kafka.model.SwissCanton;

public interface TimetableHearingStatement {

  String getTtfnid();

  String getTimetableFieldNumber();

  SwissCanton getSwissCanton();

  String getStatement();

  String getStopPlace();

  TimetableHearingStatementSenderModelV2 getStatementSender();
}
