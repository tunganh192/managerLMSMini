package com.honda.managerlmsmini.repository;

import com.honda.managerlmsmini.entity.Student;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Student> findByIdAndStatus(Long id, Integer status);

    List<Student> findAllByStatusOrderByIdDesc(Integer status);

    List<Student> findAllByIdInAndStatus(Collection<Long> ids, Integer status);

    @Query("select s from Student s where s.status=1 "
            + "and (:keyword is null or :keyword='' or lower(s.name) like lower(concat('%',:keyword,'%')) or lower(s.email) like lower(concat('%',:keyword,'%')) or s.phone like concat('%',:keyword,'%')) "
            + "and (:name is null or :name='' or lower(s.name) like lower(concat('%',:name,'%'))) "
            + "and (:email is null or :email='' or lower(s.email) like lower(concat('%',:email,'%'))) "
            + "and (:phone is null or :phone='' or s.phone like concat('%',:phone,'%'))")
    Page<Student> search(
            @Param("keyword") String keyword,
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            Pageable pageable);
}
