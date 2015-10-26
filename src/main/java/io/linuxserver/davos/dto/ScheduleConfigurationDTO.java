package io.linuxserver.davos.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.linuxserver.davos.transfer.ftp.FileTransferType;
import io.linuxserver.davos.transfer.ftp.TransferProtocol;

public class ScheduleConfigurationDTO {

    public Long id;
    public String name;
    public boolean startAutomatically;
    public int interval;
    public TransferProtocol connectionType;
    public String hostName;
    public int port;
    public String username;
    public String password;
    public String remoteFilePath;
    public String localFilePath;
    public long lastRun;
    public FileTransferType transferType;
    public List<FilterDTO> filters = new ArrayList<FilterDTO>();
    public List<ActionDTO> actions = new ArrayList<ActionDTO>();
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
