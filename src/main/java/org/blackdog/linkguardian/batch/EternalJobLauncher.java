package org.blackdog.linkguardian.batch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.core.task.TaskExecutor;

public class EternalJobLauncher extends SimpleJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EternalJobLauncher.class);

    // inner executor used to run jobs
    private ExecutorService innerExecutor = Executors.newSingleThreadExecutor();

    public EternalJobLauncher(long jobIntervalInSeconds) {

        this.setTaskExecutor(new TaskExecutor() {
            @Override
            public void execute(Runnable innerRunnable) {

                innerExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.info("running eternal job launcher");

                        RepeatTemplate template = new RepeatTemplate();

                        // logging exception handler
                        template.setExceptionHandler(new ExceptionHandler() {
                            @Override
                            public void handleException(RepeatContext repeatContext, Throwable throwable)
                                throws Throwable {

                                LOGGER.error("handle exception", throwable);
                            }
                        });

                        template.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext repeatContext) throws Exception {

                                LOGGER.info("launching a job...");

                                innerRunnable.run();

                                LOGGER.info("job finished");

                                LOGGER.info("before waiting for " + jobIntervalInSeconds);
                                Thread.sleep(jobIntervalInSeconds * 1000);
                                LOGGER.info("after waiting for " + jobIntervalInSeconds);

                                return RepeatStatus.CONTINUABLE;
                            }
                        });
                    }
                });
            }
        });
    }
}
