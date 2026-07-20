package com.honda.managerlmsmini.repository;

import com.honda.managerlmsmini.entity.Course;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCodeIgnoreCaseAndStatus(String code, Integer status);

    boolean existsByCodeIgnoreCaseAndStatusAndIdNot(String code, Integer status, Long id);

    Optional<Course> findByIdAndStatus(Long id, Integer status);

    List<Course> findAllByIdInAndStatus(Collection<Long> ids, Integer status);

    List<Course> findAllByStatusOrderByIdDesc(Integer status);

    @Query("select c from Course c where c.status=1 "
            + "and (:keyword is null or :keyword='' or lower(c.name) like lower(concat('%',:keyword,'%')) or lower(c.code) like lower(concat('%',:keyword,'%'))) "
            + "and (:name is null or :name='' or lower(c.name) like lower(concat('%',:name,'%'))) "
            + "and (:code is null or :code='' or lower(c.code) like lower(concat('%',:code,'%'))) "
            + "and (:minDuration is null or c.duration >= :minDuration) "
            + "and (:maxDuration is null or c.duration <= :maxDuration)")
    Page<Course> search(
            @Param("keyword") String keyword,
            @Param("name") String name,
            @Param("code") String code,
            @Param("minDuration") Integer minDuration,
            @Param("maxDuration") Integer maxDuration,
            Pageable pageable);
}
