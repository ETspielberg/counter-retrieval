package unidue.ub.counterretrieval.model.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name="database_counter")
public class DatabaseCounter extends Counter {

    private String title = "";

    private String platform = "";

    private String profile;

    private String publisher = "";

    private int month;

    private int year;

    @Column(name="regular_searches")
    private long regularSearches = 0L;

    @Column(name="federated_and_automated_searches")
    private long federatedAndAutomatedSearches = 0L;

    @Column(name="result_clicks")
    private long resultClicks = 0L;

    @Column(name="record_views")
    private long recordViews = 0L;

    @Id
    private String id;

    public DatabaseCounter() {
        LocalDate today = LocalDate.now();
        month = today.getMonthValue();
        year = today.getYear();
        platform = "";
        publisher = "";
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public DatabaseCounter(String publisher,String platform, int year, int month) {
        this.platform = platform;
        this.publisher = publisher;
        this.month = month;
        this.year = year;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }

    public long getRegularSearches() {
        return regularSearches;
    }

    public void setRegularSearches(long regularSearches) {
        this.regularSearches = regularSearches;
    }

    public long getFederatedAndAutomatedSearches() {
        return federatedAndAutomatedSearches;
    }

    public void setFederatedAndAutomatedSearches(long federatedAndAutomatedSearches) {
        this.federatedAndAutomatedSearches = federatedAndAutomatedSearches;
    }

    public long getResultClicks() {
        return resultClicks;
    }

    public void setResultClicks(long resultClicks) {
        this.resultClicks = resultClicks;
    }

    public long getRecordViews() {
        return recordViews;
    }

    public void setRecordViews(long recordViews) {
        this.recordViews = recordViews;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void caluclateId() {
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + publisher + platform;
    }
}
