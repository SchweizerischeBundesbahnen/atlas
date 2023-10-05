package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class InformationDeskService {

  private final InformationDeskRepository informationDeskRepository;

  public List<InformationDeskVersion> getAllInformationDesks() {
   return informationDeskRepository.findAll();
  }

}
