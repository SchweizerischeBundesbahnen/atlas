package ch.sbb.timetable.field.number.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.sbb.timetable.field.number.model.VersionModel;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class VersionController {

    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public VersionController(VersionRepository versionRepository, ModelMapper modelMapper) {
        this.versionRepository = versionRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/listVersions")
    @PageableAsQueryParam
    public List<VersionModel> getAllVersions(@Parameter(hidden = true) Pageable pageable) {
        log.info("Load Versions using pageable={}", pageable);
        return versionRepository.findAll(pageable).stream().map(version -> modelMapper.map(version, VersionModel.class)).collect(Collectors.toList());
    }


}
