package org.ratha.virtualbookstore.repository;

import org.ratha.virtualbookstore.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

    boolean existsByTitleIgnoreCase(String lowerCase);

    boolean existsByCategoryId(Long id);
}
