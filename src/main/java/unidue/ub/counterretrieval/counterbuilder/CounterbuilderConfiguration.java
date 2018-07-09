package unidue.ub.counterretrieval.counterbuilder;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CounterbuilderConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public CounterFileReader counterFileReader() {
        return new CounterFileReader();
    }

    @Bean
    @StepScope
    public CounterProcessor counterbuilderProcessor() {
        return new CounterProcessor();
    }

    @Bean
    @StepScope
    public CounterCollectionWriter counterCollectionWriter() {
        return new CounterCollectionWriter();
    }

    @Bean
    @StepScope
    public TypeAndFormatDeterminerTasklet typeAndFormatDeterminerTasklet() {
        return new TypeAndFormatDeterminerTasklet();
    }

    @Bean
    public Step counterbuilderInit() {
        return stepBuilderFactory.get("counterbuilderInit")
                .tasklet(typeAndFormatDeterminerTasklet())
                .build();
    }

    @Bean
    public Step counterbuilderStep() {
        return stepBuilderFactory.get("counterbuilderStep")
                .<String, CounterCollection>chunk(10000)
                .reader(counterFileReader())
                .processor(counterbuilderProcessor())
                .writer(counterCollectionWriter())
                .build();
    }

    @Bean
    public Job counterbuilderJob () {
        return jobBuilderFactory.get("counterbuilderJob")
                .start(counterbuilderInit())
                .next(counterbuilderStep())
                .build();
    }


}
