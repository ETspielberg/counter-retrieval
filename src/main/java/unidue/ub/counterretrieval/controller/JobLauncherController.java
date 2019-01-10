package unidue.ub.counterretrieval.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import unidue.ub.counterretrieval.settingsrepositories.CounterLogRepository;
import unidue.ub.counterretrieval.settingsrepositories.SushiproviderRepository;

@Controller
@EnableScheduling
public class JobLauncherController {

    private final JobLauncher jobLauncher;

    private final Job sushiJob;

    private final Job counterbuilderJob;


    private Logger log = LoggerFactory.getLogger(JobLauncherController.class);

    private final CounterLogRepository counterLogRepository;

    private final SushiproviderRepository sushiproviderRepository;

    @Autowired
    public JobLauncherController(JobLauncher jobLauncher, Job sushiJob, Job counterbuilderJob, CounterLogRepository counterLogRepository, SushiproviderRepository sushiproviderRepository) {
        this.jobLauncher = jobLauncher;
        this.sushiJob = sushiJob;
        this.counterbuilderJob = counterbuilderJob;
        this.counterLogRepository = counterLogRepository;
        this.sushiproviderRepository = sushiproviderRepository;
    }

    @RequestMapping("/sushi")
    public ResponseEntity<?> runSushiClient(String identifier, String type, String mode, Long year, Long month) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("sushiprovider.identifier", identifier)
                .addString("sushi.type", type)
                .addString("sushi.mode", mode)
                .addLong("sushi.year", year)
                .addLong("sushi.month", month)
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        try {
            jobLauncher.run(sushiJob, jobParameters);
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Scheduled(cron = "0 0 23 14 * ?")
    @RequestMapping("/updateAllSushi")
    public void updateAllSushi() {
        sushiproviderRepository.findAll().forEach(
                sushiprovider -> {
                    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                    jobParametersBuilder.addString("sushiprovider.identifier", sushiprovider.getIdentifier())
                            .addString("sushi.mode", "update")
                            .addLong("time", System.currentTimeMillis()).toJobParameters();
                    JobParameters jobParameters = jobParametersBuilder.toJobParameters();
                    try {
                        jobLauncher.run(sushiJob, jobParameters);
                    } catch (Exception e) {
                        log.info("could not run Sushiprovider " + sushiprovider.getIdentifier());
                    }
                }
        );
    }

    @GetMapping("/counterbuilder")
    public ResponseEntity<?> buildCounter(String filename) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("counter.file.name", filename)
                .addLong("time", System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(counterbuilderJob, jobParameters);
        return ResponseEntity.ok("successfuly run");
    }

    @GetMapping("/counterlog/forSushiprovider")
    public ResponseEntity<?> getCounterLogsForSushiprovider(String sushiprovider) {
        return ResponseEntity.ok(counterLogRepository.findAllBySushiproviderOrderByYear(sushiprovider));
    }

    @GetMapping("/sushiprovider/all")
    public ResponseEntity<?> getAllSushiproviders() {
        return ResponseEntity.ok(sushiproviderRepository.findAll());
    }

    @GetMapping("/sushiprovider/running")
    public ResponseEntity<?> getActiveSushiproviderIdentifiers() {return ResponseEntity.ok(sushiproviderRepository.getActiveIdentifiers()); }

}
