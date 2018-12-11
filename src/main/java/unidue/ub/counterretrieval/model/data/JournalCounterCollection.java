package unidue.ub.counterretrieval.model.data;

import java.util.List;

public class JournalCounterCollection {

    private String identifier;

    private Long totalRequests;

    private List<JournalCounter> journalCounters;

    public JournalCounterCollection(String identifer) {
        this.identifier = identifer;
        this.totalRequests = 0L;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public List<JournalCounter> getJournalCounters() {
        return journalCounters;
    }

    public void setJournalCounters(List<JournalCounter> journalCounters) {
        this.journalCounters = journalCounters;
    }

    public void calculateTotalRequests() {
        if (journalCounters.size() > 0)
            for (JournalCounter journalCounter : journalCounters)
                this.totalRequests += journalCounter.getTotalRequests();
    }
}
