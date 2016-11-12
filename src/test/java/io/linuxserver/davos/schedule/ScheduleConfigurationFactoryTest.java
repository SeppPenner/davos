package io.linuxserver.davos.schedule;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import io.linuxserver.davos.persistence.model.ActionModel;
import io.linuxserver.davos.persistence.model.FilterModel;
import io.linuxserver.davos.persistence.model.ScheduleModel;
import io.linuxserver.davos.schedule.workflow.actions.HttpAPICallAction;
import io.linuxserver.davos.schedule.workflow.actions.MoveFileAction;
import io.linuxserver.davos.schedule.workflow.actions.PushbulletNotifyAction;
import io.linuxserver.davos.transfer.ftp.FileTransferType;
import io.linuxserver.davos.transfer.ftp.TransferProtocol;

public class ScheduleConfigurationFactoryTest {
    
    @Test
    public void shouldConvertAllMainFields() {
        
        ScheduleModel model = new ScheduleModel();
        model.connectionType = TransferProtocol.FTP;
        model.hostName = "hostname";
        model.localFilePath = "local/";
        model.name = "schedulename";
        model.password = "password";
        model.port = 8;
        model.remoteFilePath = "thing/";
        model.startAutomatically = true;
        model.transferType = FileTransferType.FILE;
        model.username = "username";
        model.lastRun = new DateTime(2015, 1, 1, 0, 0).toDate();
        
        ScheduleConfiguration config = ScheduleConfigurationFactory.createConfig(model);
        
        assertThat(config.getConnectionType()).isEqualTo(model.connectionType);
        assertThat(config.getHostName()).isEqualTo(model.hostName);
        assertThat(config.getLocalFilePath()).isEqualTo(model.localFilePath);
        assertThat(config.getScheduleName()).isEqualTo(model.name);
        assertThat(config.getCredentials().getPassword()).isEqualTo(model.password);
        assertThat(config.getPort()).isEqualTo(model.port);
        assertThat(config.getRemoteFilePath()).isEqualTo(model.remoteFilePath);
        assertThat(config.getTransferType()).isEqualTo(model.transferType);
        assertThat(config.getCredentials().getUsername()).isEqualTo(model.username);
        assertThat(config.getLastRun().toDate()).isEqualTo(model.lastRun);
    }
    
    @Test
    public void ifLastRunInModelIsNullThenSetToEpoch() {
        
        ScheduleModel model = new ScheduleModel();
        
        ScheduleConfiguration config = ScheduleConfigurationFactory.createConfig(model);
        
        assertThat(config.getLastRun().getMillis()).isEqualTo(0);
    }
    
    @Test
    public void shouldAddAllFiltersIfAny() {
        
        ScheduleModel model = new ScheduleModel();
        model.filters = new ArrayList<FilterModel>();
        
        FilterModel filterModel = new FilterModel();
        filterModel.value = "filter1";
        
        FilterModel filterModel2 = new FilterModel();
        filterModel2.value = "filter2";
        
        model.filters.add(filterModel);
        model.filters.add(filterModel2);
        
        ScheduleConfiguration config = ScheduleConfigurationFactory.createConfig(model);
        
        assertThat(config.getFilters()).contains("filter1", "filter2");
        assertThat(config.getFilters()).hasSize(2);
    }
    
    @Test
    public void shouldAddAllActionsIfAny() {
        
        ScheduleModel model = new ScheduleModel();
        model.localFilePath = "a/local/path/";
        model.actions = new ArrayList<ActionModel>();
        
        ActionModel action1 = new ActionModel();
        action1.actionType = "move";
        action1.f1 = "a/local/path/";
        action1.f2 = "new/";
        
        ActionModel action2 = new ActionModel();
        action2.actionType = "pushbullet";
        action2.f1 = "apiKey";
        
        ActionModel action3 = new ActionModel();
        action3.actionType = "api";
        action3.f1 = "url";
        action3.f2 = "POST";
        action3.f3 = "application/json";
        action3.f4 = "some body";
        
        model.actions.add(action1);
        model.actions.add(action2);
        model.actions.add(action3);
        
        ScheduleConfiguration config = ScheduleConfigurationFactory.createConfig(model);
        
        assertThat(config.getActions().get(0)).isInstanceOf(MoveFileAction.class);
        assertThat(config.getActions().get(1)).isInstanceOf(PushbulletNotifyAction.class);
        assertThat(config.getActions().get(2)).isInstanceOf(HttpAPICallAction.class);
    }
}
