package unidue.ub.counterretrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.counterretrieval.datarepositories.DatabaseCounterRepository;
import unidue.ub.counterretrieval.datarepositories.EbookCounterRepository;
import unidue.ub.counterretrieval.datarepositories.JournalCounterRepository;
import unidue.ub.counterretrieval.model.data.DatabaseCounter;
import unidue.ub.counterretrieval.model.data.EbookCounter;
import unidue.ub.counterretrieval.model.data.JournalCounter;

import java.util.List;

@Component
@StepScope
public class DataWriter implements ItemWriter {

    private static final Logger log = LoggerFactory.getLogger(DataWriter.class);

    @Autowired
    private EbookCounterRepository ebookCounterRepository;

    @Autowired
    private JournalCounterRepository journalCounterRepository;

    @Autowired
    private DatabaseCounterRepository databaseCounterRepository;

    @Override
    public void write(List list) {
        long successful = 0;
        for (Object object : list) {
            //try {
                switch (object.getClass().getSimpleName()) {
                    case "JournalCounter":
                        journalCounterRepository.save((JournalCounter) object);
                        successful++;
                        break;
                    case "EbookCounter":

                        ebookCounterRepository.save((EbookCounter) object);
                        successful++;
                        break;
                    case "DatabaseCounter":
                        databaseCounterRepository.save((DatabaseCounter) object);
                        successful++;
                        break;
                }
            /*  } catch (Exception e) {
                log.warn("could not save object");
            }*/
        }
        log.info("successfully saved " + successful + " of " + list.size() + " counter data.");
    }

}
