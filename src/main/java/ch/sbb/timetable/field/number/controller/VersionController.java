package ch.sbb.timetable.field.number.controller;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import ch.sbb.timetable.field.number.enumaration.Status;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class VersionController {

    private final VersionRepository versionRepository;

    @Autowired
    public VersionController(
        VersionRepository versionRepository
    ) {
        this.versionRepository = versionRepository;
    }

    @RequestMapping(value="/create", method= RequestMethod.GET)
    public String create() {
        Version version = new Version();
        version.setNumber("asdsad");
        version.setStatus(Status.ACTIVE);
        version.setCreationDate(LocalDateTime.now());

        Version save = versionRepository.save(version);
        System.out.println(save);

        return "redirect:/";
    }

    @RequestMapping(value="/read", method= RequestMethod.GET)
    public String read() {
        log.info("beste shit");
        List<Version> all = versionRepository.findAll();
        all.forEach(version -> System.out.println(version));

        return "redirect:/";
    }

}
