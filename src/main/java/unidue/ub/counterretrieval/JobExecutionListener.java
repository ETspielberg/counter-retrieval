package unidue.ub.counterretrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionListener.class);

    @Autowired
    public JobExecutionListener() {
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("--- Job starting ---");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
    }
}
