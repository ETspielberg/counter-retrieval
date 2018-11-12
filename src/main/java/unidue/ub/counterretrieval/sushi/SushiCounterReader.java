package unidue.ub.counterretrieval.sushi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import unidue.ub.counterretrieval.CounterTools;
import unidue.ub.counterretrieval.settingsrepositories.CounterLogRepository;
import unidue.ub.counterretrieval.model.data.Counter;
import unidue.ub.counterretrieval.model.settings.CounterLog;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

import javax.xml.soap.SOAPMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@StepScope
@Component
public class SushiCounterReader implements ItemReader<Counter> {

    @Autowired
    CounterLogRepository counterLogRepository;

    private Sushiprovider sushiprovider;

    @Value("#{jobParameters['sushi.mode'] ?: 'update'}")
    private String mode;

    @Value("#{jobParameters['sushi.type'] ?: 'JR1'}")
    private String type;

    @Value("#{jobParameters['sushi.year'] ?: 2017}")
    private int year;

    @Value("#{jobParameters['sushi.month'] ?: 1}")
    private int month;

    private List<Counter> counters;

    private boolean collected = false;

    private static Logger log = LoggerFactory.getLogger(SushiCounterReader.class);

    SushiCounterReader() {
    }

    @Override
    public Counter read() {
        if (!collected)
            collectCounters();
        if (!counters.isEmpty())
            return counters.remove(0);
        return null;
    }

    private void collectCounters() {
        SushiClient sushiClient = new SushiClient();
        sushiClient.setSushiprovider(sushiprovider);
        sushiClient.setReportType(type);
        LocalDateTime TODAY = LocalDateTime.now();
        counters = new ArrayList<>();
        int timeshift = resetTimeshift();
        switch (mode) {
            case "update": {
                for(String reportType : sushiprovider.getReportTypes()) {
                    log.info("retrieving " + reportType + " counter reports");
                    sushiClient.setReportType(reportType);
                    counters = executeSushiClient(sushiClient, timeshift);
                }
                break;
            }
            case "full": {
                for(String reportType : sushiprovider.getReportTypes()) {
                    log.info("retrieving " + reportType + " counter reports");
                    timeshift = resetTimeshift();
                    int errors = 0;
                    while (errors <= 2) {
                        sushiClient.setReportType(reportType);
                        List<Counter> countersFound = executeSushiClient(sushiClient, timeshift);
                        addCountersToList(countersFound);
                        timeshift += 1;
                        if (countersFound.size() == 0)
                            errors += 1;
                        else
                            errors = 0;
                    }
                }
                break;
            }
            case "year": {
                log.info("collecting counter data for year");
                for (int i = 1; i <= 12; i++) {
                    if(year >= TODAY.getYear() && i >= TODAY.getMonthValue())
                        break;

                    LocalDateTime start = LocalDateTime.of(year, i, 1, 0, 0);
                    LocalDateTime end = start.plusMonths(1).minusDays(1);
                    List<Counter> countersFound = executeSushiClient(sushiClient, start, end);
                    addCountersToList(countersFound);
                }
                break;
            }
            case "month": {
                log.info("collecting counter data for month");
                LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
                LocalDateTime end = start.plusMonths(1).minusDays(1);
                List<Counter> countersFound = executeSushiClient(sushiClient, start, end);
                addCountersToList(countersFound);
            }
        }
        log.info("collected " + counters.size() + " counters in total");
        collected = true;
    }

    private int resetTimeshift() {
        if (LocalDateTime.now().getDayOfMonth() < 15)
            return 3;
        else
            return 2;
    }

    private void addCountersToList(List<Counter> countersFound) {
        if (countersFound != null) {
            if (countersFound.size() != 0) {
                counters.addAll(countersFound);
                log.info("added " + countersFound.size() + " counterbuilder statistics.");
            }
        } else {
            log.warn("no counters from conversion!");
        }
    }

    private List<Counter> executeSushiClient(SushiClient sushiClient, int timeshift) {
        LocalDateTime start = LocalDateTime.now().minusMonths(timeshift).withDayOfMonth(1);
        LocalDateTime end = LocalDateTime.now().minusMonths(timeshift - 1).withDayOfMonth(1).minusDays(1);
        return executeSushiClient(sushiClient, start, end);
    }

    private List<Counter> executeSushiClient(SushiClient sushiClient, LocalDateTime start, LocalDateTime end) {
        String identifier = sushiprovider.getIdentifier() + "-" + sushiClient.getReportType() + "-" + String.valueOf(start.getYear()) + "-" + String.valueOf(start.getMonth());
        CounterLog counterLog = counterLogRepository.getById(identifier);
        if (counterLog == null) {
            counterLog = new CounterLog();
            counterLog.setMonth(start.getMonthValue());
            counterLog.setYear(start.getYear());
            counterLog.setReportType(sushiClient.getReportType());
            counterLog.setSushiprovider(sushiprovider.getIdentifier());
            counterLog.calculateId();
        }
        List<Counter> countersFound = new ArrayList<>();
        sushiClient.setStartTime(start);
        sushiClient.setEndTime(end);
        try {
            SOAPMessage soapMessage = sushiClient.getResponse();
            if (soapMessage != null) {
                countersFound = (List<Counter>) CounterTools.convertSOAPMessageToCounters(soapMessage);
                counterLog.setStatus("SUCCESS");
                counterLog.setComment("got " + String.valueOf(countersFound.size()) + " counters from conversion of SOAP response");
            } else {
                counterLog.setStatus("ERROR");
                counterLog.setError("SOAP response is null");
            }
        } catch (Exception e) {
            counterLog.setStatus("ERROR");
            counterLog.setError(e.getLocalizedMessage());
        }
        counterLogRepository.save(counterLog);
        return countersFound;
    }

    @BeforeStep
    public void retrievePublisher(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.sushiprovider = (Sushiprovider) jobContext.get("Publisher");
    }
}
