package io.linuxserver.davos.schedule.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.linuxserver.davos.schedule.ScheduleConfiguration;
import io.linuxserver.davos.schedule.workflow.transfer.FTPTransfer;
import io.linuxserver.davos.schedule.workflow.transfer.TransferStrategy;
import io.linuxserver.davos.schedule.workflow.transfer.TransferStrategyFactory;
import io.linuxserver.davos.transfer.ftp.FTPFile;
import io.linuxserver.davos.transfer.ftp.connection.progress.ListenerFactory;
import io.linuxserver.davos.transfer.ftp.connection.progress.ProgressListener;
import io.linuxserver.davos.transfer.ftp.exception.FTPException;

public class DownloadFilesWorkflowStep extends WorkflowStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilesWorkflowStep.class);

    private TransferStrategyFactory transferStrategyFactory = new TransferStrategyFactory();

    public DownloadFilesWorkflowStep() {
        this.nextStep = new DisconnectWorkflowStep();
    }

    @Override
    public void runStep(ScheduleWorkflow schedule) {

        ScheduleConfiguration config = schedule.getConfig();

        TransferStrategy strategyToUse = transferStrategyFactory.getStrategy(config.getTransferType(), schedule.getConnection());
        LOGGER.debug("Strategy chosen for downloads is {}, selected {}", config.getTransferType(), strategyToUse);
        strategyToUse.setPostDownloadActions(schedule.getConfig().getActions());
        LOGGER.debug("PostDownloadActions: {} have been set against chosen strategy", schedule.getConfig().getActions());

        try {

            if (schedule.getFilesToDownload().isEmpty())
                LOGGER.info("There are no files to download in this run");
            
            for (FTPTransfer transfer : schedule.getFilesToDownload()) {

                LOGGER.debug("Generating listener for transfer");

                FTPFile file = transfer.getFile();
                
                ProgressListener listener = new ListenerFactory().createListener(config.getConnectionType());
                schedule.getConnection().setProgressListener(listener);
                transfer.setListener(listener);
                
                strategyToUse.transferFile(transfer.getFile(), config.getLocalFilePath());
                
                if (config.isDeleteHostFile())
                    schedule.getConnection().deleteRemoteFile(file);
            }

            LOGGER.info("Download step complete. Moving onto next step");
            schedule.getFilesToDownload().clear();

        } catch (FTPException e) {

            LOGGER.error("Unable to complete download. Error was: {}", e.getMessage());
            LOGGER.debug("Stacktrace", e);
            LOGGER.info("Clearing current queue and will still continue to next step");
            schedule.getFilesToDownload().clear();
        }

        nextStep.runStep(schedule);
    }
}
