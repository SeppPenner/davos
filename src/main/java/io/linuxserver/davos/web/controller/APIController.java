package io.linuxserver.davos.web.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.linuxserver.davos.delegation.services.HostService;
import io.linuxserver.davos.delegation.services.ScheduleService;
import io.linuxserver.davos.delegation.services.SettingsService;
import io.linuxserver.davos.web.Host;
import io.linuxserver.davos.web.Schedule;
import io.linuxserver.davos.web.ScheduleCommand;
import io.linuxserver.davos.web.controller.response.APIResponse;
import io.linuxserver.davos.web.controller.response.APIResponseBuilder;
import io.linuxserver.davos.web.selectors.LogLevelSelector;

@RestController
@RequestMapping("/api/v2")
public class APIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APIController.class);

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private HostService hostService;

    @Resource
    private SettingsService settingsService;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public ResponseEntity<APIResponse> createSchedule(@RequestBody Schedule schedule) {

        LOGGER.info("Creating new schedule");
        LOGGER.debug("Schedule values are {}", schedule);
        Schedule createdSchedule = scheduleService.saveSchedule(schedule);
        LOGGER.info("New schedule has been created");

        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponseBuilder.create().withBody(createdSchedule));
    }

    @RequestMapping(value = "/schedule/{id}", method = RequestMethod.GET)
    public ResponseEntity<APIResponse> fetchSchedule(@PathVariable("id") Long id) {

        Schedule schedule = scheduleService.fetchSchedule(id);
        LOGGER.debug("Fetched schedule: {}", schedule);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create().withBody(schedule));
    }

    @RequestMapping(value = "/schedule/{id}", method = RequestMethod.PUT)
    public ResponseEntity<APIResponse> updateSchedule(@PathVariable("id") Long id, @RequestBody Schedule schedule) {

        LOGGER.info("Updating schedule with id {} and name {}", id, schedule.getName());
        LOGGER.debug("Schedule values are {}", schedule);
        LOGGER.debug("Imposing id from URL into body");
        schedule.setId(id);

        Schedule updatedSchedule = scheduleService.saveSchedule(schedule);
        LOGGER.debug("Schedule has been updated");

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create().withBody(updatedSchedule));
    }

    @RequestMapping(value = "/schedule/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<APIResponse> deleteSchedule(@PathVariable("id") Long id) {

        LOGGER.info("Deleting schedule with id {}", id);
        scheduleService.deleteSchedule(id);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create());
    }

    @RequestMapping(value = "/schedule/{id}/execute", method = RequestMethod.POST)
    public ResponseEntity<APIResponse> executeSchedule(@PathVariable("id") Long id, @RequestBody ScheduleCommand command) {

        if (command.command == ScheduleCommand.Command.START)
            scheduleService.startSchedule(id);

        if (command.command == ScheduleCommand.Command.STOP)
            scheduleService.stopSchedule(id);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create());
    }

    @RequestMapping(value = "/host", method = RequestMethod.POST)
    public ResponseEntity<APIResponse> createHost(@RequestBody Host host) {

        LOGGER.info("Saving new host");
        LOGGER.debug("Host values are {}", host);
        Host createdHost = hostService.saveHost(host);
        LOGGER.info("Host has been created");

        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponseBuilder.create().withBody(createdHost));
    }

    @RequestMapping(value = "/host/{id}", method = RequestMethod.PUT)
    public ResponseEntity<APIResponse> updateHost(@PathVariable("id") Long id, @RequestBody Host host) {

        LOGGER.info("Updating host with id {} and name {}", id, host.getName());
        LOGGER.debug("Host values are {}", host);
        LOGGER.debug("Imposing id from URL into body");
        host.setId(id);

        Host updatedHost = hostService.saveHost(host);
        LOGGER.debug("Host has been updated");

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create().withBody(updatedHost));
    }

    @RequestMapping(value = "/host/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<APIResponse> deleteHost(@PathVariable("id") Long id) {

        LOGGER.info("Deleting host with id {}", id);
        hostService.deleteHost(id);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create());
    }

    @RequestMapping(value = "/settings/log", method = RequestMethod.POST)
    public ResponseEntity<APIResponse> setLogLevel(@RequestParam("level") LogLevelSelector level) {

        settingsService.setLoggingLevel(level);

        return ResponseEntity.status(HttpStatus.OK).body(APIResponseBuilder.create());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponseBuilder.create().withBody(e.getMessage()).withStatus(e.getMessage()));
    }
}
