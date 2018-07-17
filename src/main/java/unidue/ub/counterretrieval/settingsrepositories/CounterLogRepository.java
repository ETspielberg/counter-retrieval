package unidue.ub.counterretrieval.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.settings.CounterLog;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "counterlog", path = "counterlog")
public interface CounterLogRepository extends PagingAndSortingRepository<CounterLog, String> {

    public List<CounterLog> findAllBySushiproviderOrderByYear(String sushiprovider);
}
