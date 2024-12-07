package searchengine.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexForSearch;

import java.util.List;


@Repository
public interface IndexForSearchRepository extends JpaRepository<IndexForSearch, Integer> {

    @Query(value = "select * from index_for_search i where i.page_id = :pageId and i.lemma_id = :lemmaId", nativeQuery = true)
    IndexForSearch indexExist(@Param("pageId") Integer pageId, @Param("lemmaId") Integer lemmaId);

    @Transactional
    @Modifying
    @Query(value = "delete from index_for_search i where i.page_id = :pageId", nativeQuery = true)
    void deleteAllByPageId(@Param("pageId") Integer pageId);

    @Query(value = "select * from index_for_search i where i.page_id = :pageId", nativeQuery = true)
    List<IndexForSearch> findAllByPageId(@Param("pageId") Integer pageId);
}
