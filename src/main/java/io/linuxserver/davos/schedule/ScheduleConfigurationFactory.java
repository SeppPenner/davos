package io.linuxserver.davos.schedule;

import org.apache.commons.lang3.StringUtils;

import io.linuxserver.davos.persistence.model.ActionModel;
import io.linuxserver.davos.persistence.model.FilterModel;
import io.linuxserver.davos.persistence.model.ScheduleModel;
import io.linuxserver.davos.schedule.workflow.actions.HttpAPICallAction;
import io.linuxserver.davos.schedule.workflow.actions.MoveFileAction;
import io.linuxserver.davos.schedule.workflow.actions.PushbulletNotifyAction;
import io.linuxserver.davos.transfer.ftp.client.UserCredentials;

public class ScheduleConfigurationFactory {

    public static ScheduleConfiguration createConfig(ScheduleModel model) {

        ScheduleConfiguration config = new ScheduleConfiguration(model.name, model.host.protocol, model.host.address,
                model.host.port, new UserCredentials(model.host.username, model.host.password), model.remoteFilePath,
                model.localFilePath, model.transferType, model.filtersMandatory, model.invertFilters, model.deleteHostFile);

        if (StringUtils.isNotBlank(model.moveFileTo))
            config.getActions().add(new MoveFileAction(config.getLocalFilePath(), model.moveFileTo));
        
        if (null != model.filters)
            addFilters(model, config);

        if (null != model.actions)
            addActions(model, config);

        return config;
    }

    private static void addActions(ScheduleModel model, ScheduleConfiguration config) {

        for (ActionModel action : model.actions) {

            if ("pushbullet".equals(action.actionType))
                config.getActions().add(new PushbulletNotifyAction(action.f1));

            if ("api".equals(action.actionType))
                config.getActions().add(new HttpAPICallAction(action.f1, action.f2, action.f3, action.f4));
        }
    }

    private static void addFilters(ScheduleModel model, ScheduleConfiguration config) {

        for (FilterModel filter : model.filters)
            config.getFilters().add(filter.value);
    }
}
