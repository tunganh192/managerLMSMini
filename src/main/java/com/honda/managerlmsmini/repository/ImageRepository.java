package com.honda.managerlmsmini.repository;

import com.honda.managerlmsmini.entity.Image;
import com.honda.managerlmsmini.enums.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByIdAndObjectTypeAndObjectIdAndStatus(
            Long id, ObjectType objectType, Long objectId, Integer status);

    List<Image> findByObjectTypeAndObjectIdAndStatusOrderByIdAsc(ObjectType type, Long objectId, Integer status);

    @Query(
            "select i from Image i where i.objectType=:type and i.objectId in :ids and i.status=:status order by i.id asc")
    List<Image> findActiveByObjects(@Param("type") ObjectType type, @Param("ids") List<Long> ids, @Param("status") Integer status);

    long countByObjectTypeAndObjectIdAndMediaRoleAndStatus(
            ObjectType type, Long objectId, MediaRole role, Integer status);

    @Modifying
    @Query(
            "update Image i set i.status=:status, i.modifiedDate=CURRENT_TIMESTAMP where i.objectType=:type and i.objectId=:objectId and i.id in :ids and i.status<>:status")
    int updateStatusByIds(
            @Param("type") ObjectType type,
            @Param("objectId") Long objectId,
            @Param("ids") Collection<Long> ids,
            @Param("status") Integer status);

    @Modifying
    @Query(
            "update Image i set i.status=0, i.modifiedDate=CURRENT_TIMESTAMP where i.objectType=:type and i.objectId=:objectId and i.status<>0")
    int softDeleteByObject(@Param("type") ObjectType type, @Param("objectId") Long objectId);

    @Modifying
    @Query(
            "update Image i set i.status=0, i.modifiedDate=CURRENT_TIMESTAMP where i.objectType=:type and i.objectId in :ids and i.status<>0")
    int softDeleteByObjects(@Param("type") ObjectType type, @Param("ids") Collection<Long> ids);

    @Modifying
    @Query(
            "update Image i set i.status=0, i.modifiedDate=CURRENT_TIMESTAMP where i.objectType=:type and i.objectId=:objectId and i.mediaRole=:role and i.status<>0")
    int softDeleteByRole(
            @Param("type") ObjectType type, @Param("objectId") Long objectId, @Param("role") MediaRole role);

    @Modifying
    @Query(
            "update Image i set i.mediaRole=:secondaryRole, i.modifiedDate=CURRENT_TIMESTAMP where i.objectType=:type and i.objectId=:objectId and i.mediaRole=:primaryRole and i.status=1 and i.id<>:selectedId")
    int demoteCurrentPrimary(
            @Param("type") ObjectType type,
            @Param("objectId") Long objectId,
            @Param("selectedId") Long selectedId,
            @Param("primaryRole") MediaRole primaryRole,
            @Param("secondaryRole") MediaRole secondaryRole);
}
