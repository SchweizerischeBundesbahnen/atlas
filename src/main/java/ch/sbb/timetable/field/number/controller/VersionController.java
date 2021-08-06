package ch.sbb.timetable.field.number.controller;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.model.VersionModel;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Tag(name = "timetable-field-numbers")
public class VersionController {

    private static final Supplier<ResponseStatusException> NOT_FOUND_EXCEPTION = () -> new ResponseStatusException(HttpStatus.NOT_FOUND);

    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public VersionController(VersionRepository versionRepository, ModelMapper modelMapper) {
        this.versionRepository = versionRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PageableAsQueryParam
    public List<VersionModel> getVersions(@Parameter(hidden = true) Pageable pageable) {
        log.info("Load Versions using pageable={}", pageable);
        return versionRepository.findAll(pageable).stream().map(version -> modelMapper.map(version, VersionModel.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VersionModel getVersion(@PathVariable Long id) {
        return versionRepository.findById(id).map(this::toModel).orElseThrow(NOT_FOUND_EXCEPTION);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VersionModel createVersion(@RequestBody VersionModel newVersion) {
        Version createdVersion = versionRepository.save(modelMapper.map(newVersion, Version.class));
        return toModel(createdVersion);
    }

    @PutMapping({"/{id}"})
    public VersionModel updateVersion(@PathVariable Long id, @RequestBody VersionModel newVersion) {
        Version versionToUpdate = versionRepository.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);

        versionToUpdate.setTtfnid(newVersion.getTtfnid());
        versionToUpdate.setName(newVersion.getName());
        versionToUpdate.setNumber(newVersion.getNumber());
        versionToUpdate.setSwissTimetableFieldNumber(newVersion.getSwissTimetableFieldNumber());
        versionToUpdate.setValidFrom(newVersion.getValidFrom());
        versionToUpdate.setValidTo(newVersion.getValidTo());
        versionToUpdate.setComment(newVersion.getComment());
        versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
        versionToUpdate.setNameCompact(newVersion.getNameCompact());
        versionRepository.save(versionToUpdate);

        return toModel(versionToUpdate);
    }

    @DeleteMapping({"/{id}"})
    public void deleteTodo(@PathVariable Long id) {
        if (!versionRepository.existsById(id)) {
            throw NOT_FOUND_EXCEPTION.get();
        }
        versionRepository.deleteById(id);
    }

    private VersionModel toModel(Version version) {
        return modelMapper.map(version, VersionModel.class);
    }
}
