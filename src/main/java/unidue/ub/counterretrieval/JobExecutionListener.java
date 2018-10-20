package unidue.ub.counterretrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.counterretrieval.model.settings.Status;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;
import unidue.ub.counterretrieval.settingsrepositories.SushiproviderRepository;

@Component
public class JobExecutionListener extends JobExecutionListenerSupport {

    @Autowired
    private SushiproviderRepository sushiproviderRepository;

    private static final Logger log = LoggerFactory.getLogger(JobExecutionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String identifier = jobExecution.getJobParameters().getString("sushiprovider.identifier");
        log.info("--- Job counter collection for Sushiprovider " + identifier + " starting ---");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String identifier = jobExecution.getJobParameters().getString("sushiprovider.identifier");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB counter collection for Sushiprovider " + identifier+ " finished");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("!!! JOB counter collection for Sushiprovider " + identifier+ " failed");
            Sushiprovider sushiprovider = sushiproviderRepository.findById(identifier).get();
            sushiprovider.setStatus(Status.ERROR);
            sushiproviderRepository.save(sushiprovider);
        }
    }
}
