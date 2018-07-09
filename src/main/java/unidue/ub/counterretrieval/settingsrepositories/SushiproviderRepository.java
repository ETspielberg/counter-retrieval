package unidue.ub.counterretrieval.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import unidue.ub.counterretrieval.model.settings.Sushiprovider;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "sushiprovider", path = "sushiprovider")
public interface SushiproviderRepository extends PagingAndSortingRepository<Sushiprovider, String> {

    ResponseEntity<Sushiprovider> save(@RequestBody List<Sushiprovider> sushiproviders);

}
