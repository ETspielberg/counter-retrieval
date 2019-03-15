package unidue.ub.counterretrieval.model.data;

import java.util.List;

public class CounterCollection {

    private String identifier;

    private Long totalRequests;

    private List<? extends Counter> counters;

    public CounterCollection(String identifer) {
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

    public List<? extends Counter> getCounters() {
        return counters;
    }

    public void setCounters(List<? extends Counter> counters) {
        this.counters = counters;
    }

    public void calculateTotalRequests() {
        if (counters.size() > 0)
            for (Counter counter : counters)
                this.totalRequests += counter.totalRequests;
    }
}
