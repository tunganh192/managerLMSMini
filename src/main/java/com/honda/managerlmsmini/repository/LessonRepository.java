package com.honda.managerlmsmini.repository;

import com.honda.managerlmsmini.entity.Lesson;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> findByCourseIdAndStatus(Long courseId, Integer status, Pageable pageable);

    Optional<Lesson> findByIdAndStatus(Long id, Integer status);

    @Query("select l.id from Lesson l where l.course.id=:courseId")
    List<Long> findIdsByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Query(
            "update Lesson l set l.status=0, l.modifiedDate=CURRENT_TIMESTAMP where l.course.id=:courseId and l.status<>0")
    int softDeleteByCourseId(@Param("courseId") Long courseId);
}
