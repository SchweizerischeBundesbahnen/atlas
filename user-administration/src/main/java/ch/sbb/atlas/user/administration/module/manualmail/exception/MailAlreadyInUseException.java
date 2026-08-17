package ch.sbb.atlas.user.administration.module.manualmail.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class MailAlreadyInUseException extends AtlasException {

  public static final String CODE_MAIL_ALREADY_IN_USE = "USER_ADMIN.MAIL_ALREADY_IN_USE";
  private static final String ERROR = "E-Mail is already in use";

  private final transient String mail;
  private final transient String sbbUserIdUsingMail;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("E-Mail %s is already in use by user %s".formatted(mail, sbbUserIdUsingMail))
        .error(ERROR)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("mail")
        .message("E-Mail {0} is already in use by user {1}")
        .displayInfo(DisplayInfo.builder()
            .code(CODE_MAIL_ALREADY_IN_USE)
            .with("mail", mail)
            .with("sbbUserId", sbbUserIdUsingMail)
            .build())
        .build());
    return errorDetails;
  }

}
