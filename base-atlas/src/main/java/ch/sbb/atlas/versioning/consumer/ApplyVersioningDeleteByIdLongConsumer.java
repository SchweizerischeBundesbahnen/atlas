package ch.sbb.atlas.versioning.consumer;

import java.util.function.LongConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public class ApplyVersioningDeleteByIdLongConsumer implements LongConsumer {

  private final JpaRepository<?, Long> repository;

  @Override
  public void accept(long value) {
    repository.deleteById(value);
    repository.flush();
  }

}
