package unidue.ub.counterretrieval.model.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name="ebook_counter")
public class EbookCounter extends Counter {

    @Id
    private String id;

    @Column(columnDefinition = "TEXT")
    private String title = "";

    private String profile;

    @Column(columnDefinition = "TEXT")
    private String publisher = "";

    private String doi = "";

    @Column(columnDefinition = "TEXT")
    private String platform = "";

    private String proprietary = "";

    @Column(name="print_isbn")
    private String printIsbn = "";

    @Column(name="online_isbn")
    private String onlineIsbn = "";

    private String isni = "";

    @Column(name="section_requests")
    private String sectionRequests = "";

    private int year;

    private int month;

    @Column(name="html_requests")
    private long htmlRequests = 0L;

    @Column(name="html_requests_mobile")
    private long htmlRequestsMobile = 0L;

    @Column(name="pdf_requests")
    private long pdfRequests = 0L;

    @Column(name="pdf_requests_mobile")
    private long pdfRequestsMobile = 0L;

    @Column(name="ps_requests")
    private long psRequests = 0L;

    @Column(name="ps_requests_mobile")
    private long psRequestsMobile = 0L;

    @Column(name="total_requests")
    private long totalRequests = 0L;

    @Column(name="epub_request")
    private long epubRequest = 0L;

    public EbookCounter() {
        LocalDate today = LocalDate.now();
        month = today.getMonthValue();
        year = today.getYear();
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public EbookCounter(String onlineIsbn,String platform, int month, int year) {
        this.onlineIsbn = onlineIsbn;
        this.platform = platform;
        this.year = year;
        this.month = month;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public EbookCounter setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public String getDoi() {
        return doi;
    }

    public EbookCounter setDoi(String doi) {
        this.doi = doi;
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public String getProprietary() {
        return proprietary;
    }

    public EbookCounter setProprietary(String proprietary) {
        this.proprietary = proprietary;
        return this;
    }

    public String getPrintIsbn() {
        return printIsbn;
    }

    public EbookCounter setPrintIsbn(String printIsbn) {
        this.printIsbn = printIsbn;
        return this;
    }

    public String getOnlineIsbn() {
        return onlineIsbn;
    }

    public void setOnlineIsbn(String onlineIsbn) {
        this.onlineIsbn = onlineIsbn;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public String getIsni() {
        return isni;
    }

    public EbookCounter setIsni(String isni) {
        this.isni = isni;
        return this;
    }

    public String getSectionRequests() {
        return sectionRequests;
    }

    public void setSectionRequests(String sectionRequests) {
        this.sectionRequests = sectionRequests;
    }

    public int getYear() {
        return year;

    }

    public void setYear(int year) {
        this.year = year;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getHtmlRequests() {
        return htmlRequests;
    }

    public void setHtmlRequests(long htmlRequests) {
        this.htmlRequests = htmlRequests;
    }

    public long getHtmlRequestsMobile() {
        return htmlRequestsMobile;
    }

    public void setHtmlRequestsMobile(long htmlRequestsMobile) {
        this.htmlRequestsMobile = htmlRequestsMobile;
    }

    public long getPdfRequests() {
        return pdfRequests;
    }

    public void setPdfRequests(long pdfRequests) {
        this.pdfRequests = pdfRequests;
    }

    public long getPdfRequestsMobile() {
        return pdfRequestsMobile;
    }

    public void setPdfRequestsMobile(long pdfRequestsMobile) {
        this.pdfRequestsMobile = pdfRequestsMobile;
    }

    public long getPsRequests() {
        return psRequests;
    }

    public void setPsRequests(long psRequests) {
        this.psRequests = psRequests;
    }

    public long getPsRequestsMobile() {
        return psRequestsMobile;
    }

    public void setPsRequestsMobile(long psRequestsMobile) {
        this.psRequestsMobile = psRequestsMobile;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getEpubRequest() {
        return epubRequest;
    }

    public void setEpubRequest(long epubRequest) {
        this.epubRequest = epubRequest;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void caluclateId() {
        if (onlineIsbn.isEmpty()) {
            if (printIsbn.isEmpty()) {
                if (doi.isEmpty())
                    id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + proprietary + platform;
                else
                    id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + doi + platform;
            } else
                id = String.valueOf(year) + "-" + String.valueOf(month) + "-" + printIsbn + platform;
        } else
            id = String.valueOf(year) + "-" + String.valueOf(month) + "-"  + onlineIsbn + platform;
    }
}
