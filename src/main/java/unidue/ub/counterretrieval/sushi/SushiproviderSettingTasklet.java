package unidue.ub.counterretrieval.sushi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import unidue.ub.counterretrieval.model.settings.Status;
import unidue.ub.counterretrieval.settingsrepositories.SushiproviderRepository;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

public class SushiproviderSettingTasklet implements Tasklet {

    @Autowired
    private SushiproviderRepository sushiproviderRepository;

    private final static Logger log = LoggerFactory.getLogger(SushiproviderSettingTasklet.class);
    private Status status;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        Sushiprovider sushiprovider = (Sushiprovider) chunkContext.getStepContext().getJobExecutionContext().get("Publisher");
        sushiprovider.setStatus(status);
        sushiproviderRepository.save(sushiprovider);
        log.info("set Publisher '" + sushiprovider.getIdentifier() + "' status to " + status);
        return RepeatStatus.FINISHED;
    }

    SushiproviderSettingTasklet setStatus(Status status) {
        this.status = status;
        return this;
    }
}
