package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    @Query(value = "SELECT * FROM page p WHERE p.path = ?1 and p.site_id = ?2 LIMIT 1", nativeQuery = true)
    Page findByPathAndSiteId(String path, int siteId);

    @Query(value = "SELECT * FROM page p WHERE p.site_id = ?1", nativeQuery = true)
    List<Page> findBySiteId(int siteId);

    @Query(value = "SELECT count(*) FROM page p WHERE p.site_id = ?1", nativeQuery = true)
    int getCountPagesOnSite(int siteId);
}
