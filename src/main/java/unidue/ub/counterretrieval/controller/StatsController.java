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

    final private String selectPublisher = "SELECT publisher as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items";

    final private String selectPlatform = "SELECT publisher as identifier, month, year, sum(total_requests) as requests, COUNT(total_requests) as items";

    final private String selectPublisherYearly = "SELECT publisher as identifier, year, sum(total_requests) as requests, COUNT(total_requests) as items";

    final private String selectPlatformYearly = "SELECT platform as identifier, year, sum(total_requests) as requests, COUNT(total_requests) as items";

    final private String groupBy = " GROUP BY month, year, publisher, platform";

    final private String groupByYearly = " GROUP BY year, publisher, platform";

    final private String ebook = " From ebook_counter";

    final private String journal = " From journal_counter";

    final private String getEbookStatsByPublisher = selectPublisher + ebook + groupBy;

    final private String getEbookStatsByPlatform = selectPlatform + ebook + groupBy;

    final private String getJournalStatsByPublisher = selectPublisher + journal + groupBy;

    final private String getJournalStatsByPlatform = selectPlatform + journal + groupBy;

    final private String getEbookStatsForPublisher = selectPlatform + ebook + " WHERE publisher = ?" + groupBy;

    final private String getEbookStatsForPlatform = selectPublisher + ebook + " WHERE platform = ?" + groupBy;

    final private String getJournalStatsForPublisher = selectPlatform + journal + " WHERE publisher = ?" + groupBy;

    final private String getJournalStatsForPlatform = selectPublisher + journal + " WHERE platform = ?" + groupBy;

    final private String getEbookStatsByYearsByPublisher = selectPublisherYearly + ebook + groupByYearly;

    final private String getEbookStatsByYearsByPlatform = selectPlatformYearly + ebook + groupByYearly;

    final private String getEbookStatsForYearByPublisher = selectPublisherYearly + ebook +" where year = ?" + groupByYearly;

    final private String getEbookStatsForYearByPlatform = selectPlatformYearly + ebook + " where year = ?" + groupByYearly;

    final private String getJournalStatsByYearsByPublisher = selectPublisherYearly + journal + groupByYearly;

    final private String getJournalStatsByYearsByPlatform = selectPlatformYearly + journal + groupByYearly ;

    final private String getJournalStatsForYearByPublisher = selectPublisherYearly + journal + " where year = ?" + groupByYearly;

    final private String getJournalStatsForYearByPlatform = selectPlatformYearly + journal + " where year = ?" + groupByYearly;


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

    @GetMapping("/ebookcounter/statsByYearsByplatform")
    public ResponseEntity<?> getEbookCounterStatsByYearsByPlatform() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsByYearsByPlatform, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsByYearsBypublisher")
    public ResponseEntity<?> getEbookCounterStatsByYearsByPublisher() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsByYearsByPublisher, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsForYearByplatform")
    public ResponseEntity<?> getEbookCounterStatsByYearByPlatform(int year) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsForYearByPlatform, new Object[]{year}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/ebookcounter/statsForYearBypublisher")
    public ResponseEntity<?> getEbookCounterStatsForYearByPublisher(int year) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getEbookStatsForYearByPublisher, new Object[]{year}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsByYearsByplatform")
    public ResponseEntity<?> getJournalCounterStatsByYearsByPlatform() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsByYearsByPlatform, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsByYearsBypublisher")
    public ResponseEntity<?> getJournalCounterStatsByYearsByPublisher() {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsByYearsByPublisher, new Object[]{}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsForYearByplatform")
    public ResponseEntity<?> getJournalCounterStatsForYearByPlatform(int year) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsForYearByPlatform, new Object[]{year}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }

    @GetMapping("/journalcounter/statsForYearBypublisher")
    public ResponseEntity<?> getJournalCounterStatsForYearByPublisher(int year) {
        List<CounterStats> counterStatss = new ArrayList<>(jdbcTemplate.query(getJournalStatsForYearByPublisher, new Object[]{year}, (rs, rowNum) -> new CounterStats(rs.getString("identifier"), 0, rs.getInt("year"), rs.getLong("requests"), rs.getLong("items"))));
        return ResponseEntity.ok(counterStatss);
    }
}
