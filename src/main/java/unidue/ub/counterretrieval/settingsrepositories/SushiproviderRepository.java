package unidue.ub.counterretrieval.settingsrepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "sushiprovider", path = "sushiprovider")
public interface SushiproviderRepository extends PagingAndSortingRepository<Sushiprovider, String> {

    @Query(value = "SELECT identifier FROM profile where dtype='Sushiprovider' and status='running'", nativeQuery = true)

    List<String> getActiveIdentifiers();
}
