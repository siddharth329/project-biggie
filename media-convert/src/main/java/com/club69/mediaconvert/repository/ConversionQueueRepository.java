package com.club69.mediaconvert.repository;

import com.club69.mediaconvert.model.ConversionQueue;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.QueryParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversionQueueRepository extends JpaRepository<ConversionQueue, UUID> {
    List<ConversionQueue> findAllByMediaFileId(UUID mediaFileId);

    @Transactional
    @Modifying
    @Query(value = """
        select * from claim_next_job(:workerId, :lockTimeoutMinutes)
        """, nativeQuery = true)
    Optional<ConversionQueue> claimNextJob(@Nonnull @QueryParam("workerId") String workerId,
                                           @Nonnull @QueryParam("lockTimeoutMinutes") Integer lockTimeoutMinutes);

    @Transactional
    @Modifying
    @Query(value = """
        select * from complete_job(:jobId, :workerId)
        """, nativeQuery = true)
    Boolean completeJob(@Nonnull @QueryParam("jobId") UUID jobId,
                        @Nonnull @QueryParam("workerId") String workerId);

    @Transactional
    @Modifying
    @Query(value = """
        select * from fail_job(:workerId, :lockTimeoutMinutes, :errorMessage)
        """, nativeQuery = true)
    Boolean failJob(@Nonnull @QueryParam("jobId") UUID jobId,
                    @Nonnull @QueryParam("workerId") String workerId,
                    @Nonnull @QueryParam("errorMessage") String errorMessage);
}
