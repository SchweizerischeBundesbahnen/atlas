package ch.sbb.timetable.field.number.controller;

import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import ch.sbb.timetable.field.number.enumaration.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.List;

@Controller
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
        Version version = versionRepository.findById(1000L).orElse(null);


        return "redirect:/";
    }

}
