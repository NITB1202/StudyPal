package com.study.studypal.repositories;

import com.study.studypal.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
    SELECT u
    FROM User u
    WHERE LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
      AND u.id <> :userId
      AND (:cursor IS NULL OR u.id > :cursor)
    ORDER BY u.id ASC
    """)
    List<User> searchByNameWithCursor(@Param("userId") UUID userId,
                                      @Param("keyword") String keyword,
                                      @Param("cursor") UUID cursor,
                                      Pageable pageable);

    @Query("""
    SELECT COUNT(u)
    FROM User u
    WHERE LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
      AND u.id <> :userId
    """)
    long countByName(@Param("userId") UUID userId,
                     @Param("keyword") String keyword);

}