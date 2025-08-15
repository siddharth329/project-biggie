package com.club69.mediaconvert.repository;

import com.club69.mediaconvert.dto.ConversionQueueStatus;
import com.club69.mediaconvert.model.ConversionQueue;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversionQueueRepository extends JpaRepository<ConversionQueue, UUID> {
    List<ConversionQueue> findAllByMediaFileId(UUID mediaFileId);
    List<ConversionQueue> findAllByIdIn(List<UUID> conversionIds);

    @Query(value = """
        SELECT status, COUNT(*) as count
        FROM conversionqueue
        GROUP BY status;
        """, nativeQuery = true)
    ConversionQueueStatus getConversionQueueStatus();

    @Transactional
    @Query(value = """
        select * from claim_next_job(:workerId, :lockTimeoutMinutes)
        """, nativeQuery = true)
    Optional<ConversionQueue> claimNextJob(@Nonnull @Param("workerId") String workerId,
                                           @Nonnull @Param("lockTimeoutMinutes") Integer lockTimeoutMinutes);

    @Transactional
    @Query(value = """
        select * from complete_job(:jobId, :workerId)
        """, nativeQuery = true)
    Boolean completeJob(@Nonnull @Param("jobId") UUID jobId,
                        @Nonnull @Param("workerId") String workerId);

    @Transactional
    @Query(value = """
        select * from fail_job(:jobId, :workerId, :errorMessage)
        """, nativeQuery = true)
    Boolean failJob(@Nonnull @Param("jobId") UUID jobId,
                    @Nonnull @Param("workerId") String workerId,
                    @Nonnull @Param("errorMessage") String errorMessage);
}
