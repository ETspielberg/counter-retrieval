package unidue.ub.counterretrieval.counterbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import unidue.ub.counterretrieval.model.data.*;

import java.util.*;

public class CounterProcessor implements ItemProcessor<String, CounterCollection> {

    private String type;

    private Map<Integer,String> datesMap;

    private Set<Integer> dateKeys;

    private Map<Integer, String> fieldMap;

    private Set<Integer> fieldKeys;

    private List<Counter> counters;

    private String delimiter;

    private Logger log = LoggerFactory.getLogger(CounterProcessor.class);

    @Override
    public CounterCollection process(final String line) {
    counters = new ArrayList<>();
    switch (type) {
        case "database": {
            convertLineToDatabaseCounters(line);
            break;
        }
        case "journal": {
            convertLineToJournalCounters(line);
            break;
        }
        case "ebook": {
            convertLineToEbookCounters(line);
        }
    }
    return new CounterCollection(counters);
    }

    private void convertLineToEbookCounters(String line) {
        String[] parts = line.split(delimiter);
        log.info("converting line to ebook counters for " + parts[0]);
        for (Integer dateKey : dateKeys) {
            BeanWrapper wrapper = new BeanWrapperImpl(new EbookCounter());
            for (Integer fieldKey : fieldKeys) {
                wrapper.setPropertyValue(fieldMap.get(fieldKey), parts[fieldKey].trim());
            }
            String datesString = datesMap.get(dateKey).trim();
            int month = Integer.parseInt(datesString.substring(0,datesString.indexOf("-")));
            wrapper.setPropertyValue("month", month);
            int year = Integer.parseInt(datesString.substring(datesString.indexOf("-")+1));
            wrapper.setPropertyValue("year", year);
            wrapper.setPropertyValue("totalRequests",parts[dateKey].trim());
            EbookCounter ebookCounter = (EbookCounter) wrapper.getWrappedInstance();
            ebookCounter.caluclateId();
            counters.add(ebookCounter);
        }
    }

    private void convertLineToJournalCounters(String line) {
        String[] parts = line.split(delimiter);
        log.info("converting line to journal counters for " + parts[0]);
        for (Integer dateKey : dateKeys) {
            BeanWrapper wrapper = new BeanWrapperImpl(new JournalCounter());
            for (Integer fieldKey : fieldKeys) {
                wrapper.setPropertyValue(fieldMap.get(fieldKey), parts[fieldKey]);
            }
            String datesString = datesMap.get(dateKey);
            int month = Integer.parseInt(datesString.substring(0,datesString.indexOf("-")));
            wrapper.setPropertyValue("month", month);
            int year =Integer.parseInt(datesString.substring(datesString.indexOf("-")+1));
            wrapper.setPropertyValue("year", year);
            wrapper.setPropertyValue("totalRequests",parts[dateKey]);
            JournalCounter journalCounter = (JournalCounter)wrapper.getWrappedInstance();
            journalCounter.caluclateId();
            counters.add(journalCounter);
        }
    }

    private void convertLineToDatabaseCounters(String line) {
        Integer activityKey = 0;
        for (Integer key : fieldKeys) {
            if (fieldMap.get(key).equals("activity"))
                activityKey = key;
        }
        Map<String,String[]> map = new HashMap<>();
        String[] parts = line.split("/");
        log.info("converting line to database counters for " + parts[0]);
        List<String> activityNames = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty())
                continue;
            String[] fields = part.split(delimiter);
            String activityName = fields[activityKey];
            activityNames.add(activityName);
            map.put(activityName, fields);
        }
        for (Integer dateKey : dateKeys) {
            BeanWrapper wrapper = new BeanWrapperImpl(new DatabaseCounter());
            for (Integer fieldKey : fieldKeys) {
                if (fieldMap.get(fieldKey).equals("activity"))
                    continue;
                wrapper.setPropertyValue(fieldMap.get(fieldKey), map.get(activityNames.get(0))[fieldKey]);
            }
            String datesString = datesMap.get(dateKey);
            int month = Integer.parseInt(datesString.substring(0,datesString.indexOf("-")));
            wrapper.setPropertyValue("month", month);
            int year = Integer.parseInt(datesString.substring(datesString.indexOf("-")+1));
            wrapper.setPropertyValue("year", year);
            for (String activityName: activityNames) {
                String propertyName = "";
                switch (activityName) {
                    case "regular searches":
                        propertyName = "regularSearches";
                        break;
                    case "result clicks":
                        propertyName = "resultClicks";
                        break;
                    case "record views":
                        propertyName = "recordViews";
                        break;
                    case "federated and automated searches":
                    case "federated searches":
                    case "automated searches":
                        propertyName = "federatedAndAutomatedSearches";
                        break;
                }
                if (propertyName.isEmpty())
                    continue;
                wrapper.setPropertyValue(propertyName, map.get(activityName)[dateKey]);
                DatabaseCounter databaseCounter = (DatabaseCounter) wrapper.getWrappedInstance();
                databaseCounter.caluclateId();
                counters.add((DatabaseCounter) wrapper.getWrappedInstance());
            }
        }
    }


    @BeforeStep
    public void retrieveTypeAndMaps(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.type = jobContext.getString("type");
        this.delimiter = jobContext.getString("delimiter");
        this.datesMap = (Map<Integer,String>) jobContext.get("datesMap");
        dateKeys = datesMap.keySet();
        this.fieldMap = (Map<Integer,String>) jobContext.get("fieldMap");
        fieldKeys = fieldMap.keySet();
    }
}
