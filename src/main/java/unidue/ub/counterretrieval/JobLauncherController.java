package unidue.ub.counterretrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import unidue.ub.counterretrieval.datarepositories.DatabaseCounterRepository;
import unidue.ub.counterretrieval.datarepositories.EbookCounterRepository;
import unidue.ub.counterretrieval.datarepositories.JournalCounterRepository;
import unidue.ub.counterretrieval.model.data.EbookCounter;
import unidue.ub.counterretrieval.model.data.JournalCounter;
import unidue.ub.counterretrieval.settingsrepositories.CounterLogRepository;
import unidue.ub.counterretrieval.settingsrepositories.SushiproviderRepository;

import java.util.List;

@Controller
@EnableScheduling
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job sushiJob;

    @Autowired
    Job counterbuilderJob;

    private SimpMessagingTemplate template;

    private Logger log = LoggerFactory.getLogger(JobLauncherController.class);
/*
    @Autowired
    public JobLauncherController(SimpMessagingTemplate template) {
        this.template = template;
    }
*/
    @Autowired
    CounterLogRepository counterLogRepository;

    @Autowired
    SushiproviderRepository sushiproviderRepository;

    @Autowired
    private JournalCounterRepository journalCounterRepository;

    @Autowired
    private EbookCounterRepository ebookCounterRepository;

    @Autowired
    private DatabaseCounterRepository databaseCounterRepository;

    @RequestMapping("/sushi")
    public ResponseEntity<?> runSushiClient(String identifier, String type, String mode, Long year, Long month) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("sushiprovider.identifier", identifier)
                .addString("sushi.type", type)
                .addString("sushi.mode", mode)
                .addLong("sushi.year", year)
                .addLong("sushi.month", month)
                .addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        try {
            jobLauncher.run(sushiJob, jobParameters);
            //this.template.convertAndSend("/profileUpdate", "{'identifier' : '" + identifier + "', 'status' : 'FINISHED'}");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } catch (Exception e) {
            //this.template.convertAndSend("/sushi/error", "{'identifier' : '" + identifier + "', 'status' : 'ERROR', 'message' : '" + e.getLocalizedMessage() + "*}");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Scheduled(cron="0 0 29 23 * ?")
    @RequestMapping("/updateAllSushi")
    public void updateAllSushi() {
        sushiproviderRepository.findAll().forEach(
                sushiprovider -> {
                    JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                    jobParametersBuilder.addString("sushiprovider.identifier", sushiprovider.getIdentifier())
                            .addString("sushi.mode", "update")
                            .addLong("time",System.currentTimeMillis()).toJobParameters();
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
                .addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(counterbuilderJob, jobParameters);
        return ResponseEntity.ok("successfuly run");
    }

    @GetMapping("/journalcounter/getForIssn")
    public ResponseEntity<?> getAlJournalCountersForIssn(@Param("issn") String issn) {
        List<JournalCounter> list = journalCounterRepository.findAllByOnlineIssn(issn);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByPrintIssn(issn);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByDoi(issn);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByProprietary(issn);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/ebookcounter/getForIsbn")
    public ResponseEntity<?> getAlEbookCountersForIsbn(@Param("isbn") String isbn) {
        List<EbookCounter> list = ebookCounterRepository.findAllByOnlineIsbn(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByPrintIsbn(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByDoi(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByProprietary(isbn);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/counterlog/forSushiprovider")
    public ResponseEntity<?> getCounterLogsForSushiprovider(String sushiprovider) {
        return ResponseEntity.ok(counterLogRepository.findAllBySushiproviderOrderByYear(sushiprovider));
    }

    @GetMapping("/sushiprovider/all")
    public ResponseEntity<?> getAllSushiproviders() {
        return ResponseEntity.ok(sushiproviderRepository.findAll());
    }

    @GetMapping("/journalcounter/getForPublisher")
    public ResponseEntity<?> getAllJournalCountersForPublisher(@Param("publisher") String publisher) {
        return ResponseEntity.ok(journalCounterRepository.findAllByPublisher(publisher));
    }

    @GetMapping("/journalcounter/getForPlatform")
    public ResponseEntity<?> getAllJournalCountersForPlatform(@Param("platform") String platform) {
        return ResponseEntity.ok(journalCounterRepository.findAllByPlatform(platform));
    }

    @GetMapping("/ebookcounter/getForPublisher")
    public ResponseEntity<?> getAllEbookCountersForPublisher(@Param("publisher") String publisher) {
        return ResponseEntity.ok(ebookCounterRepository.findAllByPublisher(publisher));
    }

    @GetMapping("/ebookcounter/getForPlatform")
    public ResponseEntity<?> getAllEbookCountersForPlatform(@Param("platform") String platform) {
        return ResponseEntity.ok(ebookCounterRepository.findAllByPlatform(platform));
    }

    @GetMapping("databasecounter/getForPlatform")
    public ResponseEntity<?> getAllDatabaseCounterForPlatform(@Param("platform") String platform) {
        return ResponseEntity.ok(databaseCounterRepository.getAllByPlatform(platform));
    }
}
