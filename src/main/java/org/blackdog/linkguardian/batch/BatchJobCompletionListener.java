package org.blackdog.linkguardian.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class BatchJobCompletionListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobCompletionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info("after job with status " + jobExecution.getStatus());
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info("BATCH JOB COMPLETED SUCCESSFULLY");
        }
    }

}
