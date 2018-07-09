package unidue.ub.counterretrieval.counterbuilder;

import unidue.ub.counterretrieval.model.data.Counter;

import java.util.List;

public class CounterCollection {

    CounterCollection(List<Counter> counters) {
        this.counters = counters;
    }

    private List<Counter> counters;

    public List<Counter> getCounters() {
        return counters;
    }
}
