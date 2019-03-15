package unidue.ub.counterretrieval.model.data;

public abstract class Counter implements Comparable<Counter>{

    private int month;

    private int year;

    private String profile;

    public long totalRequests;

    /**
     * compares one COUNTER report to the other. Allows for time-dependent ordering of COUNTER reports.
     * @param other the other COUNTER report, the actual one is compared to
     * return long 1, if the actual report dates later than the other one and -1 if it dates earlier.
     */
    public int compareTo(Counter other) {
        if (this.year > other.year)
            return 1;
        else if (this.year < other.year)
            return -1;
        else {
            if (this.month < other.month)
                return -1;
            else if (this.month > other.month)
                return 1;
            else
                return this.month - other.month;
        }
    }
}
