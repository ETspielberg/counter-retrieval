package unidue.ub.counterretrieval.model.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="counter_log")
public class CounterLog {

    @Id
    private String id;

    private String sushiprovider;

    @Column(name="report_type")
    private String reportType;

    private String status;

    private int month;

    private int year;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String error;

    private Date timestamp;

    public CounterLog() {
        this.timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSushiprovider() {
        return sushiprovider;
    }

    public void setSushiprovider(String sushiprovider) {
        this.sushiprovider = sushiprovider;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void calculateId() {
        this.id = this.sushiprovider + "-" + this.reportType + "-" + String.valueOf(this.year) +  "-" + String.valueOf(this.month);
    }
}
