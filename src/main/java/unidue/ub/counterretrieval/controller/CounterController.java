package unidue.ub.counterretrieval.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.counterretrieval.datarepositories.DatabaseCounterRepository;
import unidue.ub.counterretrieval.datarepositories.EbookCounterRepository;
import unidue.ub.counterretrieval.datarepositories.JournalCounterRepository;
import unidue.ub.counterretrieval.model.data.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CounterController {

    private final JournalCounterRepository journalCounterRepository;

    private final EbookCounterRepository ebookCounterRepository;

    private final DatabaseCounterRepository databaseCounterRepository;

    private static Logger log = LoggerFactory.getLogger(CounterController.class);

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
        List<CounterCollection> counterCollections = new ArrayList<>();
        for (String issnInd : issns) {
            CounterCollection counterCollection = new CounterCollection(issnInd);
            List<JournalCounter> list = journalCounterRepository.findAllByOnlineIssn(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByPrintIssn(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByDoi(issnInd);
            if (list.size() == 0)
                list = journalCounterRepository.findAllByProprietary(issnInd);
            counterCollection.setCounters(list);
            counterCollection.calculateTotalRequests();
            counterCollections.add(counterCollection);
        }
        return ResponseEntity.ok(counterCollections);
    }

    @GetMapping("/ebookcounter/isbn/{isbn}")
    public ResponseEntity<DigitalManifestation> getAllEbookCountersForIsbn(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(getDigitalManifestation(isbn));
    }

    @GetMapping("/ebookcounter/doi")
    public ResponseEntity<DigitalManifestation> getAllEbookCountersForDOI(@RequestParam("doi") String doi) {
        log.info("retrieving data by doi " + doi);
        List<EbookCounter> list = ebookCounterRepository.findAllByDoi(doi);
        DigitalManifestation digitalManifestation = new DigitalManifestation(doi);
        digitalManifestation.setUsage(list);
        digitalManifestation.calculateTotalRequests();
        return ResponseEntity.ok(digitalManifestation);
    }

    @GetMapping("/ebookcounter/identifiers")
    public ResponseEntity<DigitalManifestation> getAllEbookCountersForIdentifiers(@RequestParam("doi") String doi, @RequestParam("isbn") String isbn) {
        if (!"".equals(doi)) {
            List<EbookCounter> list = ebookCounterRepository.findAllByDoi(doi);
            if (list.size() > 0) {
                DigitalManifestation digitalManifestation = new DigitalManifestation(doi);
                digitalManifestation.setUsage(list);
                digitalManifestation.calculateTotalRequests();
                return ResponseEntity.ok(digitalManifestation);
            }
        }
        return ResponseEntity.ok(getDigitalManifestation(isbn));
    }

    @GetMapping("/ebookcounter/isbns/{isbns}")
    public ResponseEntity<List<DigitalManifestation>> getAllEbookCountersForIsbns(@PathVariable("isbns") String isbns) {
        String[] isbnList;
        if (isbns.contains(";"))
            isbnList = isbns.split(";");
        else
            isbnList = new String[]{isbns};
        List<DigitalManifestation> counterCollections = new ArrayList<>();
        for (String isbnInd : isbnList)
            counterCollections.add(getDigitalManifestation(isbnInd));
        return ResponseEntity.ok(counterCollections);
    }

    private DigitalManifestation getDigitalManifestation(String isbn) {
        DigitalManifestation digitalManifestation = new DigitalManifestation(isbn);
        List<EbookCounter> list = ebookCounterRepository.findAllByOnlineIsbn(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByPrintIsbn(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByDoi(isbn);
        if (list.size() == 0)
            list = ebookCounterRepository.findAllByProprietary(isbn);
        digitalManifestation.setUsage(list);
        digitalManifestation.calculateTotalRequests();
        return digitalManifestation;
    }

    @GetMapping("/journalcounter/getForYear/{issn}")
    public ResponseEntity<?> getAlJournalCountersForIssn(@PathVariable("issn") String issn, @RequestParam("year") int year) {
        CounterCollection counterCollection = new CounterCollection(issn);
        List<JournalCounter> list = journalCounterRepository.findAllByOnlineIssnAndYearOrderByMonth(issn, year);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByPrintIssnAndYearOrderByMonth(issn, year);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByDoiAndYearOrderByMonth(issn, year);
        if (list.size() == 0)
            list = journalCounterRepository.findAllByProprietaryAndYearOrderByMonth(issn, year);
        counterCollection.setCounters(list);
        counterCollection.calculateTotalRequests();
        return ResponseEntity.ok(counterCollection);
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
