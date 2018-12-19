package unidue.ub.counterretrieval.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import unidue.ub.counterretrieval.datarepositories.DatabaseCounterRepository;
import unidue.ub.counterretrieval.datarepositories.EbookCounterRepository;
import unidue.ub.counterretrieval.datarepositories.JournalCounterRepository;
import unidue.ub.counterretrieval.model.data.EbookCounter;
import unidue.ub.counterretrieval.model.data.EbookCounterCollection;
import unidue.ub.counterretrieval.model.data.JournalCounter;
import unidue.ub.counterretrieval.model.data.JournalCounterCollection;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CounterController {

    private final JournalCounterRepository journalCounterRepository;

    private final EbookCounterRepository ebookCounterRepository;

    private final DatabaseCounterRepository databaseCounterRepository;

    @Autowired
    public CounterController(JournalCounterRepository journalCounterRepository, EbookCounterRepository ebookCounterRepository, DatabaseCounterRepository databaseCounterRepository) {
        this.journalCounterRepository = journalCounterRepository;
        this.ebookCounterRepository = ebookCounterRepository;
        this.databaseCounterRepository = databaseCounterRepository;
    }

    @GetMapping("/journalcounter/getForIssn")
    public ResponseEntity<?> getAlJournalCountersForIssn(@Param("issn") String issn) {
        String[] issns;
        if (issn.contains(";"))
            issns = issn.split(";");
        else
            issns = new String[]{issn};
        List counterCollections = new ArrayList();
        for (String issnInd : issns) {
            JournalCounterCollection counterCollection = new JournalCounterCollection(issnInd);
            List<JournalCounter> list = journalCounterRepository.findAllByOnlineIssn(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByPrintIssn(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByDoi(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByProprietary(issnInd);
            counterCollection.setJournalCounters(list);
            counterCollection.calculateTotalRequests();
            counterCollections.add(counterCollection);
        }
        return ResponseEntity.ok(counterCollections);
    }

    @GetMapping("/ebookcounter/getForIsbn")
    public ResponseEntity<?> getAlEbookCountersForIsbn(@Param("isbn") String isbn) {
        String[] isbns;
        if (isbn.contains(";"))
            isbns = isbn.split(";");
        else
            isbns = new String[]{isbn};
        List counterCollections = new ArrayList();
        for (String isbnInd : isbns) {
            EbookCounterCollection ebookCounterCollection = new EbookCounterCollection(isbnInd);
            List<EbookCounter> list = ebookCounterRepository.findAllByOnlineIsbn(isbnInd);
            if (list.size() == 0)
                list = ebookCounterRepository.findAllByPrintIsbn(isbnInd);
            if (list.size() == 0)
                list = ebookCounterRepository.findAllByDoi(isbnInd);
            if (list.size() == 0)
                list = ebookCounterRepository.findAllByProprietary(isbnInd);
            ebookCounterCollection.setEbookCounters(list);
            ebookCounterCollection.calculateTotalRequests();
            counterCollections.add(ebookCounterCollection);
        }

        return ResponseEntity.ok(counterCollections);
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
