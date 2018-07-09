package unidue.ub.counterretrieval.sushi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

@Component
public class SushiFlowDecision implements JobExecutionDecider {

    static final String COMPLETED = "COMPLETED";

    static final String UNKNOWN = "UNKNOWN";

    private Logger log = LoggerFactory.getLogger(SushiFlowDecision.class);

    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        Sushiprovider sushiprovider = (Sushiprovider) jobExecution.getExecutionContext().get("Publisher");
        if (sushiprovider == null) {
            log.info("could not retrieve sushiprovider from context");
            return FlowExecutionStatus.UNKNOWN;
        }
        if (sushiprovider.getName().equals("newProvider")) {
            log.info("new sushiprovider");
            return FlowExecutionStatus.UNKNOWN;
        }
        else {
            return FlowExecutionStatus.COMPLETED;
        }
    }
}
