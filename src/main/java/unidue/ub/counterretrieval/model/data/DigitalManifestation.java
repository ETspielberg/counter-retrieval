package unidue.ub.counterretrieval.model.data;

import java.util.List;

public class DigitalManifestation {

    private String identifier;

    private List<EbookCounter> usage;

    private Long totalRequests;

    public DigitalManifestation(String identifier) {
        this.identifier = identifier;
        this.totalRequests = 0L;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<EbookCounter> getUsage() {
        return usage;
    }

    public void setUsage(List<EbookCounter> usage) {
        this.usage = usage;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void calculateTotalRequests() {
        if (usage.size() > 0)
            for (EbookCounter ebookCounter : usage)
                this.totalRequests += ebookCounter.getTotalRequests();
    }
}
