package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ToiletRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ToiletService {

  private final ToiletRepository toiletRepository;

  public List<ToiletVersion> getAllToilets() {
   return toiletRepository.findAll();
  }


}
