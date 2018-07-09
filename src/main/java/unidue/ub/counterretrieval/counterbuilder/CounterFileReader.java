package unidue.ub.counterretrieval.counterbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CounterFileReader implements ItemReader<String> {

    private Logger log = LoggerFactory.getLogger(CounterFileReader.class);

    @Value("#{jobParameters['counter.file.name'] ?: ''}")
    private String filename;

    @Value("${ub.statistics.data.dir}")
    private String dataDir;

    private Boolean collected = false;

    private String type;

    private Integer initalLines;

    private List<String> lines;

    private String delimiter;

    CounterFileReader() {}

    @Override
    public String read() throws IOException {
        if (!collected)
            readLines();

        if (!lines.isEmpty())
            return lines.remove(0);
        return null;
    }

    private void readLines() throws IOException {
        lines = new ArrayList<>();
        File file = new File(dataDir + "/counterbuilder/"+ filename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String readLine;
        int numberOfLines = 0;
        int databaseLineCounter = 0;
        String databaseLine = "";
        while ((readLine = bufferedReader.readLine()) != null ) {
            if(numberOfLines < initalLines) {
                numberOfLines++;
                continue;
            }
            if (type.equals("database")) {
                if (readLine.toLowerCase().contains(delimiter + "sessions" + delimiter)) {
                    log.info("skipping line");
                    continue;
                }

                databaseLine += "/" + readLine;
                databaseLineCounter++;
                if  (databaseLineCounter == 4) {
                    lines.add(databaseLine);
                    databaseLine = "";
                    databaseLineCounter = 0;
                }
            } else if (!readLine.isEmpty()) {
                if (readLine.toLowerCase().contains("total for all")) {
                    log.info("skipping line");
                    continue;
                }
                lines.add(readLine);
            }
        }
        collected = true;
        log.info("read " + lines.size() + " lines of counter data");
    }

    @BeforeStep
    public void retrieveTypeAndInitialLines(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.type = (String) jobContext.get("type");
        this.delimiter = (String) jobContext.get("delimiter");
        this.initalLines = (Integer) jobContext.get("inital.lines");
    }
}
