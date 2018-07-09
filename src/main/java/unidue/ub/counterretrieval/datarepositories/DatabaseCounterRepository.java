package unidue.ub.counterretrieval.datarepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.data.DatabaseCounter;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "databasecounter", path = "databasecounter")
public interface DatabaseCounterRepository extends PagingAndSortingRepository<DatabaseCounter, String> {

    List<DatabaseCounter> getAllByPlatform(@Param("platform") String platform);
	
}
