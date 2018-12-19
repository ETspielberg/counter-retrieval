package unidue.ub.counterretrieval.datarepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.data.CounterStats;
import unidue.ub.counterretrieval.model.data.JournalCounter;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "journalcounter", path = "journalcounter")
public interface JournalCounterRepository extends PagingAndSortingRepository<JournalCounter, String> {

    List<JournalCounter> findAllByOnlineIssn(@Param("onlineIssn") String onlineIssn);

    List<JournalCounter> findAllByPrintIssn(@Param("printIssn") String printIssn);

    List<JournalCounter> findAllByDoi(@Param("doi") String doi);

    List<JournalCounter> findAllByProprietary(@Param("proprietary") String proprietary);

    List<JournalCounter> findAllByPublisher(@Param("publisher") String publisher);

    List<JournalCounter> findAllByPlatform(@Param("platform") String platform);
}
