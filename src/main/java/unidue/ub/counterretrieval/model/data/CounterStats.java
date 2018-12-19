package unidue.ub.counterretrieval.model.data;

public class CounterStats {

    private String identifier;

    private int month;

    private int year;

    private long requests;

    private long items;

    public CounterStats() {
        this.identifier = "";
        this.month = 0;
        this.year = 0;
        this.requests = 0L;
        this.items = 0L;
    }

    public CounterStats(String identifier, int month, int year, long requests, long items) {
        if (identifier == null) {
            this.identifier = "";
        } else {
            this.identifier = identifier;
        }
        this.month = month;
        this.year = year;
        this.requests = requests;
        this.items = items;
    }

    public long getItems() {
        return items;
    }

    public void setItems(long items) {
        this.items = items;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public long getRequests() {
        return requests;
    }

    public void setRequests(long requests) {
        this.requests = requests;
    }
}
