package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    @Query(value = "select * from lemma t where t.lemma = :lemma and t.site_id = :siteId for update", nativeQuery = true)
    Lemma getLemmaInSite(String lemma, Integer siteId);

    @Query(value = "select * from lemma t where t.lemma = :lemma LIMIT 1", nativeQuery = true)
    Lemma lemmaExist(String lemma);

    @Query(value = "select count(distinct lemma) from lemma", nativeQuery = true)
    int countDistinctLemmas();

    @Query(value = "select sum(l.frequency) from lemma l join site s on s.id = l.site_id where l.lemma = :lemma and s.status = 'INDEXED'", nativeQuery = true)
    Optional<Integer> countLemmaOnIndexedSite(String lemma);

    @Query(value = "select * from lemma t where t.site_id = :siteId", nativeQuery = true)
    List<Lemma> findBySiteId(Integer siteId);
}
