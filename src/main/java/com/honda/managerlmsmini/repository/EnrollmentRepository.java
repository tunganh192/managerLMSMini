package com.honda.managerlmsmini.repository;

import com.honda.managerlmsmini.entity.Enrollment;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByIdAndStatus(Long id, Integer status);

    @Query("select e from Enrollment e join fetch e.student join fetch e.course where e.id=:id and e.status=1")
    Optional<Enrollment> findActiveById(@Param("id") Long id);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query(
            "select e from Enrollment e join fetch e.student join fetch e.course where e.student.id in :studentIds and e.course.id in :courseIds")
    List<Enrollment> findByStudentsAndCourses(
            @Param("studentIds") Collection<Long> studentIds, @Param("courseIds") Collection<Long> courseIds);

    @Query(
            value =
                    "select e from Enrollment e join fetch e.student join fetch e.course where e.status=1 and e.course.id=:courseId",
            countQuery = "select count(e) from Enrollment e where e.status=1 and e.course.id=:courseId")
    Page<Enrollment> findActiveByCourse(@Param("courseId") Long courseId, Pageable pageable);

    @Modifying
    @Query(
            "update Enrollment e set e.status=0, e.modifiedDate=CURRENT_TIMESTAMP where e.student.id=:studentId and e.status<>0")
    int softDeleteByStudentId(@Param("studentId") Long studentId);

    boolean existsByCourseIdAndStatus(Long courseId, Integer status);
}
