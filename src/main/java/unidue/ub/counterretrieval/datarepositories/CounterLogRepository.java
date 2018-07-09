package unidue.ub.counterretrieval.datarepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.data.CounterLog;

@RepositoryRestResource(collectionResourceRel = "counterlog", path = "counterlog")
public interface CounterLogRepository extends PagingAndSortingRepository<CounterLog, String> {
	
}
