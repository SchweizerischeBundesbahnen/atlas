package ch.sbb.atlas.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

@UtilityClass
public class LocalDateTimeMatchers {

  public static Matcher<String> stringDateTimeIsWithinOneHourOfNow() {
    return new TypeSafeMatcher<>() {
      @Override
      public void describeTo(Description description) {
      }

      @Override
      protected boolean matchesSafely(String string) {
        LocalDateTime localDateTime = LocalDateTime.parse(string);
        return ChronoUnit.HOURS.between(localDateTime, LocalDateTime.now()) == 0;
      }
    };
  }

}