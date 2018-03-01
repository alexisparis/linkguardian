package org.blackdog.linkguardian.task;

import java.util.UUID;
import javafx.util.Pair;
import org.blackdog.linkguardian.batch.BatchItemProcessor;
import org.blackdog.linkguardian.batch.BatchItemReader;
import org.blackdog.linkguardian.batch.BatchItemWriter;
import org.blackdog.linkguardian.batch.BatchJobCompletionListener;
import org.blackdog.linkguardian.batch.BatchProcessor;
import org.blackdog.linkguardian.batch.BatchReader;
import org.blackdog.linkguardian.batch.BatchWriter;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class BookmarkBatchTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkBatchTasks.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private BatchItemReader batchItemReader;

    @Autowired
    private BatchItemProcessor batchItemProcessor;

    @Autowired
    private BatchItemWriter batchItemWriter;

    @Autowired
    private BatchReader batchReader;

    @Autowired
    private BatchProcessor batchProcessor;

    @Autowired
    private BatchWriter batchWriter;

    @Bean
    public JobExecutionListener listener() {
        return new BatchJobCompletionListener();
    }

    @Bean(name = "BookmarkProcessJob")
    public Job createBookmarkProcessJob() {

        Step stepBatchItem = stepBuilderFactory.get("stepBookmarkBatchItemProcess")
            .<BookmarkBatchItem, Pair<Link, BookmarkBatchItem>> chunk(5)
            //            .<BookmarkBatchItem, Pair<Link, BookmarkBatchItem>>chunk(new DefaultResultCompletionPolicy() {
            //
            //            })
            .reader(batchItemReader)
            .processor(batchItemProcessor)
            .writer(batchItemWriter)
            .build();

        Step stepBatch = stepBuilderFactory.get("stepBookmarkItemProcess")
            .<BookmarkBatch, BookmarkBatch> chunk(5)
            //            .<BookmarkBatch, BookmarkBatch>chunk(new DefaultResultCompletionPolicy() {
            //
            //            })
            .reader(batchReader)
            .processor(batchProcessor)
            .writer(batchWriter)
            .build();

        return jobBuilderFactory.get("processJob")
//            .incrementer(new RunIdIncrementer())
            .incrementer(new JobParametersIncrementer() {
                @Override
                public JobParameters getNext(JobParameters jobParameters) {
                    return new JobParametersBuilder()
                        .addString("id", UUID.randomUUID().toString())
                        .toJobParameters();
                }
            })
            .listener(listener())
            .flow(stepBatchItem)
            .next(stepBatch)
            .end()
            .build();
    }
}
