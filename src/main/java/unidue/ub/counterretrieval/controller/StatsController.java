package unidue.ub.counterretrieval.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import unidue.ub.counterretrieval.model.data.CounterStats;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StatsController {

    private final JdbcTemplate jdbcTemplate;

    private final Logger log = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    public StatsController(JdbcTemplate jdbcTemplate, @Qualifier("dataDataSource") DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplate.setDataSource(dataSource);
    }

    final private String getEbookStatsByPublisher = "SELECT publisher as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM ebook_counter GROUP BY publisher, month, year;";

    final private String getEbookStatsByPlatform = "SELECT platform as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM ebook_counter GROUP BY platform, month, year";

    final private String getJournalStatsByPublisher = "SELECT publisher as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM journal_counter GROUP BY publisher, month, year";

    final private String getJournalStatsByPlatform = "SELECT platform as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM journal_counter GROUP BY platform, month, year";

    final private String getEbookStatsForPublisher = "SELECT platform as identifier, publisher, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM ebook_counter WHERE publisher = ? GROUP BY publisher, month, year, platform;";

    final private String getEbookStatsForPlatform = "SELECT publisher as identifier, platform, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM ebook_counter WHERE platform = ? GROUP BY platform, month, year, publisher;";

    final private String getJournalStatsForPublisher = "SELECT platform as identifier, publisher, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM journal_counter WHERE publisher = ? GROUP BY publisher, month, year, platform;";

    final private String getJournalStatsForPlatform = "SELECT publisher as identifier, platform, month, year, sum(total_requests) as requests, COUNT(total_requests) as items FROM journal_counter WHERE platform = ? GROUP BY publisher, platform, month, year;";


    @GetMapping("/ebookcounter/statsForpublisher")
    public ResponseEntity<List<CounterStats>> getEbookCounterStatsForPublisher(String identifier) {
        log.info(identifier);
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsForPublisher, new Object[]{identifier}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsForplatform")
    public ResponseEntity<?> getEbookCounterStatsForPlatform(String identifier) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsForPlatform, new Object[]{identifier}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsBypublisher")
    public ResponseEntity<?> getEbookCounterStatsByPublisher() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsByPublisher, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsByplatform")
    public ResponseEntity<?> getEbookCounterStatsByPlatform() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsByPlatform, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsForpublisher")
    public ResponseEntity<?> getJournalCounterStatsForPublisher(String identifier) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsForPublisher, new Object[]{identifier}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsForplatform")
    public ResponseEntity<?> getJournalCounterStatsForPlatform(String identifier) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsForPlatform, new Object[]{identifier}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsBypublisher")
    public ResponseEntity<?> getJournalCounterStatsByPublisher() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsByPublisher, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsByplatform")
    public ResponseEntity<?> getJournalCounterStatsByPlatform() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsByPlatform, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), rs.getInt("month"), rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }
}
