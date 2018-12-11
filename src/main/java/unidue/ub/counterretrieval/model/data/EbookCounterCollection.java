package unidue.ub.counterretrieval.model.data;

import java.util.List;

public class EbookCounterCollection {

    private String identifier;

    private List<EbookCounter> ebookCounters;

    private Long totalRequests;

    public EbookCounterCollection(String identifier) {
        this.identifier = identifier;
        this.totalRequests = 0L;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<EbookCounter> getEbookCounters() {
        return ebookCounters;
    }

    public void setEbookCounters(List<EbookCounter> ebookCounters) {
        this.ebookCounters = ebookCounters;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void calculateTotalRequests() {
        if (ebookCounters.size() > 0)
            for (EbookCounter ebookCounter : ebookCounters)
                this.totalRequests += ebookCounter.getTotalRequests();
    }
}
