package io.linuxserver.davos.schedule.workflow.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.linuxserver.davos.transfer.ftp.FTPFile;
import io.linuxserver.davos.transfer.ftp.connection.Connection;
import io.linuxserver.davos.util.FileUtils;

public class FilesAndFoldersTranferStrategy extends TransferStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesAndFoldersTranferStrategy.class);
    
    public FilesAndFoldersTranferStrategy(Connection connection) {
        super(connection);
    }

    @Override
    public void transferFile(FTPFile fileToTransfer, String destination) {

        String filename = fileToTransfer.getName();
        String cleanFilePath = FileUtils.ensureTrailingSlash(fileToTransfer.getPath());
        String cleanDestination = FileUtils.ensureTrailingSlash(destination);

        LOGGER.info("Downloading {} to {}", cleanFilePath + filename, cleanDestination);
        connection.download(fileToTransfer, cleanDestination);
        LOGGER.info("Successfully downloaded file.");

        LOGGER.info("Running post download actions on {}", filename);
        runPostDownloadAction(fileToTransfer);
    }
}
