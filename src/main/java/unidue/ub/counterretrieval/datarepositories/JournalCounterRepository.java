package unidue.ub.counterretrieval.datarepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.data.JournalCounter;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "journalcounter", path = "journalcounter")
public interface JournalCounterRepository extends PagingAndSortingRepository<JournalCounter, String> {

    public List<JournalCounter> findAllByOnlineIssn(@Param("onlineIssn") String onlineIssn);

    public List<JournalCounter> findAllByPrintIssn(@Param("printIssn") String printIssn);

    public List<JournalCounter> findAllByDoi(@Param("doi") String doi);

    public List<JournalCounter> findAllByProprietary(@Param("proprietary") String proprietary);
}
