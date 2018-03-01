package org.blackdog.linkguardian.batch;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class CustomBatchConfigurer implements BatchConfigurer {
    private static final Log logger = LogFactory
        .getLog(org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer.class);
    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    @Value("${batch.bookmarks.job.interval_seconds}")
    private Long jobIntervalInSeconds;

    @Autowired(
        required = false
    )
    public void setDataSource(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        this.dataSource = dataSource;
        this.transactionManager = (PlatformTransactionManager)(entityManagerFactory != null ?
            new JpaTransactionManager(entityManagerFactory) :
            new DataSourceTransactionManager(this.dataSource));
    }

    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JobLauncher getJobLauncher() {
        return this.jobLauncher;
    }

    public JobExplorer getJobExplorer() {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (this.dataSource == null) {
                logger.warn("No datasource was provided...using a Map based JobRepository");
                if (this.transactionManager == null) {
                    this.transactionManager = new ResourcelessTransactionManager();
                }

                MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.transactionManager);
                jobRepositoryFactory.afterPropertiesSet();
                this.jobRepository = jobRepositoryFactory.getObject();
                MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
                jobExplorerFactory.afterPropertiesSet();
                this.jobExplorer = jobExplorerFactory.getObject();
            } else {
                this.jobRepository = this.createJobRepository();
                JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
                jobExplorerFactoryBean.setDataSource(this.dataSource);
                jobExplorerFactoryBean.afterPropertiesSet();
                this.jobExplorer = jobExplorerFactoryBean.getObject();
            }

            this.jobLauncher = this.createJobLauncher();
        } catch (Exception var3) {
            throw new BatchConfigurationException(var3);
        }
    }

    protected JobLauncher createJobLauncher() throws Exception {
//        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//        jobLauncher.setJobRepository(this.jobRepository);
//        jobLauncher.afterPropertiesSet();
//        return jobLauncher;

        SimpleJobLauncher jobLauncher = new EternalJobLauncher(jobIntervalInSeconds);
        jobLauncher.setJobRepository(this.getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(this.dataSource);
        factory.setTransactionManager(this.transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
