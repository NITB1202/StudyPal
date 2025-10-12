package com.study.studypal.user.repository;

import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
  @Query(
      """
    SELECT u
    FROM User u
    WHERE LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
      AND u.id <> :userId
      AND (:cursor IS NULL OR u.id > :cursor)
    ORDER BY u.id ASC
    """)
  List<User> searchByNameWithCursor(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("cursor") UUID cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(u)
    FROM User u
    WHERE LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
      AND u.id <> :userId
    """)
  long countByName(@Param("userId") UUID userId, @Param("keyword") String keyword);

  @Query(
      """
    SELECT new com.study.studypal.user.dto.response.UserDetailResponseDto(
    u.id,
    u.name,
    a.email,
    u.dateOfBirth,
    u.gender,
    u.avatarUrl
    )
    FROM User u JOIN Account a ON u.id = a.user.id
    WHERE u.id = :userId
    """)
  Optional<UserDetailResponseDto> getUserProfile(@Param("userId") UUID userId);
}
