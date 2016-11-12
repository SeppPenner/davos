package io.linuxserver.davos.dto.converters;

import java.util.Date;

import io.linuxserver.davos.dto.ActionDTO;
import io.linuxserver.davos.dto.FilterDTO;
import io.linuxserver.davos.dto.ScheduleDTO;
import io.linuxserver.davos.persistence.model.ActionModel;
import io.linuxserver.davos.persistence.model.FilterModel;
import io.linuxserver.davos.persistence.model.ScheduleModel;

public class ScheduleDTOConverter implements Converter<ScheduleModel, ScheduleDTO> {

    @Override
    public ScheduleDTO convert(ScheduleModel source) {

        ScheduleDTO dto = new ScheduleDTO();

        dto.connectionType = source.connectionType;
        dto.hostName = source.hostName;
        dto.id = source.id;
        dto.interval = source.interval;
        Date lastRun = source.lastRun;

        if (null != lastRun)
            dto.lastRun = lastRun.getTime();
        
        dto.localFilePath = source.localFilePath;
        dto.name = source.name;
        dto.password = source.password;
        dto.port = source.port;
        dto.remoteFilePath = source.remoteFilePath;
        dto.startAutomatically = source.startAutomatically;
        dto.transferType = source.transferType;
        dto.username = source.username;

        for (ActionModel action : source.actions) {

            ActionDTO actionDto = new ActionDTO();

            actionDto.id = action.id;
            actionDto.actionType = action.actionType;
            actionDto.f1 = action.f1;
            actionDto.f2 = action.f2;
            actionDto.f3 = action.f3;
            actionDto.f4 = action.f4;

            dto.actions.add(actionDto);
        }

        for (FilterModel filter : source.filters) {

            FilterDTO filterDto = new FilterDTO();

            filterDto.id = filter.id;
            filterDto.value = filter.value;

            dto.filters.add(filterDto);
        }

        return dto;
    }
}
