package unidue.ub.counterretrieval.datarepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.data.CounterStats;
import unidue.ub.counterretrieval.model.data.EbookCounter;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "ebookcounter", path = "ebookcounter")
public interface EbookCounterRepository extends PagingAndSortingRepository<EbookCounter, String> {

    List<EbookCounter> findAllByOnlineIsbn(@Param("onlineIssn") String onlineIssn);

    List<EbookCounter> findAllByPrintIsbn(@Param("printIssn") String printIssn);

    List<EbookCounter> findAllByDoi(@Param("doi") String doi);

    List<EbookCounter> findAllByProprietary(@Param("proprietaryIdentifier") String proprietaryIdentifier);

    List<EbookCounter> findAllByPublisher(@Param("publisher") String publisher);

    List<EbookCounter> findAllByPlatform(@Param("platform") String platform);

}
