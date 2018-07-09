package unidue.ub.counterretrieval.counterbuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
public class TypeAndFormatDeterminerTasklet implements Tasklet {

    @Value("#{jobParameters['counter.file.name'] ?: ''}")
    private String filename;

    private Logger log = LoggerFactory.getLogger(TypeAndFormatDeterminerTasklet.class);

    private String delimiter;

    @Value("${ub.statistics.data.dir}")
    private String dataDir;

    private Map<Integer, String> fieldMap;

    private Map<Integer, String> datesMap;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext)  {
        log.info("building counter statistics for file " + dataDir + "/counterbuilder" + filename);
        File file = new File(dataDir + "/counterbuilder/"+ filename);
        fieldMap = new HashMap<>();
        datesMap = new HashMap<>();
        String type = "";
        Integer initialLines = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                readLine = readLine.toLowerCase();
                initialLines++;
                log.info(readLine);
                log.info("read line contains 'database': " + readLine.contains("user activity"));
                if (readLine.contains("issn")||readLine.contains("isbn")|readLine.contains("user activity")) {
                    if (readLine.contains("isbn"))
                        type = "ebook";
                    else if (readLine.contains("issn"))
                        type = "journal";
                    else type = "database";
                    prepareMapsFromLine(readLine);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        log.info("type of usage data is " + type + ", delimited by '" + delimiter + "' and with " + initialLines + " initial lines");
        stepContext.put("fieldMap", fieldMap);
        stepContext.put("datesMap", datesMap);
        stepContext.put("type", type);
        stepContext.put("delimiter", delimiter);
        stepContext.put("inital.lines",initialLines);
        return RepeatStatus.FINISHED;
    }

    private void prepareMapsFromLine(String readLine) {
        determineDelimiter(readLine);

        String[] parts = readLine.split(delimiter);
        for (int i = 0; i< parts.length; i++) {
            switch (parts[i]) {
                case "title" : {
                    fieldMap.put(i, "title");
                }
                case "database" : {
                    fieldMap.put(i, "title");
                    break;
                }
                case "online issn": {
                    fieldMap.put(i, "onlineIssn");
                    break;
                }
                case "print issn": {
                    fieldMap.put(i, "printIssn");
                    break;
                }
                case "publisher": {
                    fieldMap.put(i, "publisher");
                    break;
                }
                case "platform": {
                    fieldMap.put(i, "platform");
                    break;
                }
                case "journal": {
                    fieldMap.put(i, "fullName");
                    break;
                }
                case "user activity": {
                    fieldMap.put(i, "activity");
                    break;
                }
                case "": {
                    if (i == 0)
                        fieldMap.put(i, "title");
                    break;
                }
                case " ": {
                    if (i == 0)
                        fieldMap.put(i, "title");
                    break;
                }
                case "book doi": {
                    fieldMap.put(i, "doi");
                    break;
                }
                case "isbn": {
                    fieldMap.put(i, "onlineIsbn");
                    break;
                }
                case "online isbn": {
                    fieldMap.put(i, "onlineIsbn");
                    break;
                }
                case "print isbn": {
                    fieldMap.put(i, "printIsbn");
                    break;
                }
                case "proprietary identifier": {
                    fieldMap.put(i, "proprietary");
                    break;
                }
                case "journal doi": {
                    fieldMap.put(i, "doi");
                    break;
                }
                case "reporting period total": break;
                case "reporting period html": break;
                case "reporting period pdf": break;
                default: {
                    String part = parts[i].trim();
                    if (part.contains(" "))
                        part = part.replace(" ", "-");
                    String monthRegex = "(jan?|feb?|mar?|apr?|may?|jun?|jul?|aug?|sep?|oct?|nov?|dec?)([- ])(19|20)?\\d\\d";
                    if (part.matches(monthRegex)){
                        String month = part.substring(0,3);
                        String yearString = part.substring(4);
                        Integer year  = Integer.parseInt(yearString);
                        if (year < 90 ) {
                            year += 2000;
                        } else if (year > 90 && year < 100) {
                            year += 1900;
                        }
                        switch (month) {
                            case "jan": {
                                datesMap.put(i,"01-" + year);
                                break;
                            }
                            case "feb": {
                                datesMap.put(i,"02-" + year);
                                break;
                            }
                            case "mar": {
                                datesMap.put(i,"03-" + year);
                                break;
                            }
                            case "apr": {
                                datesMap.put(i,"04-" + year);
                                break;
                            }
                            case "may": {
                                datesMap.put(i,"05-" + year);
                                break;
                            }
                            case "jun": {
                                datesMap.put(i,"06-" + year);
                                break;
                            }
                            case "jul": {
                                datesMap.put(i,"07-" + year);
                                break;
                            }
                            case "aug": {
                                datesMap.put(i,"08-" + year);
                                break;
                            }
                            case "sep": {
                                datesMap.put(i,"09-" + year);
                                break;
                            }
                            case "oct": {
                                datesMap.put(i,"10-" + year);
                                break;
                            }
                            case "nov": {
                                datesMap.put(i,"11-" + year);
                                break;
                            }
                            case "dec": {
                                datesMap.put(i,"12-" + year);
                            }
                        }
                    }
                }

            }
        }
    }

    private void determineDelimiter(String readLine) {
        if (StringUtils.countMatches(readLine,",") > 2)
            this.delimiter = ",";
         else if (StringUtils.countMatches(readLine,";") > 2)
            this.delimiter = ";";
         else
             this.delimiter = "\\t";
    }
}
