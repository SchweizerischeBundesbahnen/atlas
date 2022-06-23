package ch.sbb.atlas.model.exception;


public class AtlasScheduledJobException extends RuntimeException {

  public AtlasScheduledJobException(String jobName, Throwable cause) {
    super("Scheduled job " + jobName + " failed: ", cause);
  }
}
