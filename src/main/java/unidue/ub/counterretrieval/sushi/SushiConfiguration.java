package unidue.ub.counterretrieval.sushi;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unidue.ub.counterretrieval.DataWriter;
import unidue.ub.counterretrieval.model.data.Counter;
import unidue.ub.counterretrieval.model.settings.Status;

@Configuration
@EnableBatchProcessing
public class SushiConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public SushiCounterReader reader() {
        return new SushiCounterReader();
    }

    @Bean
    @StepScope
    public DataWriter writer() {
        return new DataWriter();
    }

    @Bean
    @StepScope
    public SushiproviderInitializerTasklet sushiproviderIntializer() {
        return new SushiproviderInitializerTasklet();
    }

    @Bean
    @StepScope
    public SushiproviderSettingTasklet sushiStartingTasklet() {
        return new SushiproviderSettingTasklet().setStatus(Status.RUNNING);
    }

    @Bean
    @StepScope
    public SushiproviderSettingTasklet sushiFinishedTasklet() {
        return new SushiproviderSettingTasklet().setStatus(Status.FINISHED);
    }

    @Bean
    public SushiFlowDecision sushiDecision() {
        return new SushiFlowDecision();
    }

    @Bean
    public Step sushiproviderSetStart() {
        return stepBuilderFactory.get("startStep")
                .tasklet(sushiStartingTasklet())
                .build();
    }

    @Bean
    public Step sushiproviderSetFinished() {
        return stepBuilderFactory.get("endStep")
                .tasklet(sushiFinishedTasklet())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<Counter, Counter>chunk(10000)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Step init() {
        return stepBuilderFactory.get("init")
                .tasklet(sushiproviderIntializer())
                .build();
    }

    @Bean
    public Flow sushiFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("manifestationFlow");
        return flowBuilder
                .start(sushiproviderSetStart())
                .next(step())
                .next(sushiproviderSetFinished()).end();
    }

    @Bean
    public Job sushiJob() {
        return jobBuilderFactory.get("sushiJob")
                .incrementer(new RunIdIncrementer())
                .start(init())
                .next(sushiDecision())
                .on(sushiDecision().COMPLETED)
                .to(sushiFlow())
                .from(sushiDecision())
                .on(sushiDecision().UNKNOWN)
                .end()
                .build()
                .build();

    }
}
