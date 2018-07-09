package unidue.ub.counterretrieval;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job sushiJob;

    @Autowired
    Job counterbuilderJob;

    @RequestMapping("/sushi")
    public ResponseEntity<?> runSushiClient(String identifier, String type, String mode, Long year, Long month) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("sushiprovider.identifier", identifier)
                .addString("sushi.type", type)
                .addString("sushi.mode", mode)
                .addLong("sushi.year", year)
                .addLong("sushi.month", month)
                .addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(sushiJob, jobParameters);
        return ResponseEntity.status(HttpStatus.FOUND).build();
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
}
