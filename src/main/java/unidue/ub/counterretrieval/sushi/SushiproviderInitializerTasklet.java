package unidue.ub.counterretrieval.sushi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import unidue.ub.counterretrieval.datarepositories.EbookCounterRepository;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;
import unidue.ub.counterretrieval.settingsrepositories.SushiproviderRepository;

@Component
@StepScope
public class SushiproviderInitializerTasklet implements Tasklet {

    @Value("#{jobParameters['sushiprovider.identifier'] ?: 'newProvider'}")
    public String identifier;

    @Autowired
    private SushiproviderRepository sushiproviderRepository;

    Logger log = LoggerFactory.getLogger(SushiproviderInitializerTasklet.class);

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        if (sushiproviderRepository.findById(identifier).isPresent()) {
            Sushiprovider sushiprovider = sushiproviderRepository.findById(identifier).get();
            ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            stepContext.put("Publisher", sushiprovider);
            return RepeatStatus.FINISHED;
        } else
            return null;

    }
}
