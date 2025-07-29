create table conversionqueue (
    id                uuid not null primary key,
    createdat         timestamp(6),
    endtime           timestamp(6),
    errormessage      varchar(255),
    inputbucketname   varchar(255),
    lockedat          timestamp(6),
    lockedby          varchar(255),
    maxretries        integer,
    conversionrequest jsonb,
    mediafileid       uuid,
    metadata          varchar(255),
    objectkey         varchar(255),
    outprefix         varchar(255),
    outputbucketname  varchar(255),
    retrycount        integer,
    starttime         timestamp(6),
    status            varchar(255)
        constraint conversionqueue_status_check
            check ((status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'RETRYING'::character varying, 'CANCELLED'::character varying])::text[])),
    updatedat         timestamp(6)
);


-- Function to atomically claim a job (prevents race conditions)
CREATE OR REPLACE FUNCTION claim_next_job(worker_id TEXT, lock_timeout_minutes INTEGER DEFAULT 30)
    RETURNS SETOF conversionqueue AS $$
DECLARE
    claimed_job_id UUID;
BEGIN
    -- First, release any stale locks (jobs locked too long ago)
    UPDATE conversionqueue
    SET status = 'PENDING', lockedby = NULL, lockedat = NULL
    WHERE status = 'IN_PROGRESS'
      AND lockedat < NOW() - INTERVAL '1 minute' * lock_timeout_minutes;

    -- Atomically claim the next available job
    UPDATE conversionqueue
    SET
        status = 'IN_PROGRESS',
        lockedby = worker_id,
        lockedat = NOW(),
        starttime = CASE WHEN starttime IS NULL THEN NOW() ELSE starttime END
    WHERE id = (
        SELECT id
        FROM conversionqueue
        WHERE status = 'PENDING'
        ORDER BY createdat DESC
        LIMIT 1
            FOR UPDATE SKIP LOCKED  -- This prevents blocking and race conditions
    )
    RETURNING id INTO claimed_job_id;

    -- Return the claimed job details
    IF claimed_job_id IS NOT NULL THEN
        RETURN QUERY
            SELECT *
            FROM conversionqueue cq
            WHERE cq.id = claimed_job_id;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Function to complete a job-------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION complete_job(job_id UUID, worker_id TEXT)
    RETURNS BOOLEAN AS $$
BEGIN
    UPDATE conversionqueue
    SET
        status = 'COMPLETED',
        endtime = NOW(),
        lockedby = NULL,
        lockedat = NULL
    WHERE id = job_id
      AND lockedby = worker_id
      AND status = 'IN_PROGRESS';

    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Function to fail a job (with retry logic)--------------------------------------------------------------
CREATE OR REPLACE FUNCTION fail_job(job_id UUID, worker_id TEXT, error_msg TEXT)
    RETURNS BOOLEAN AS $$
DECLARE
    current_retries INTEGER;
    max_retries INTEGER;
BEGIN
    SELECT retrycount, maxretries
    INTO current_retries, max_retries
    FROM conversionqueue
    WHERE id = job_id;

    IF current_retries < max_retries THEN
        -- Retry the job
        UPDATE conversionqueue
        SET
            status = 'RETRYING',
            retrycount = retrycount + 1,
            errormessage = error_msg,
            lockedby = NULL,
            lockedat = NULL
        WHERE id = job_id
          AND lockedby = worker_id;

        -- After a short delay, make it available for retry
        UPDATE conversionqueue
        SET status = 'PENDING'
        WHERE id = job_id AND status = 'RETRYING';
    ELSE
        -- Max retries reached, mark as failed
        UPDATE conversionqueue
        SET
            status = 'FAILED',
            errormessage = error_msg,
            endtime = NOW(),
            lockedby = NULL,
            lockedat = NULL
        WHERE id = job_id
          AND lockedby = worker_id;
    END IF;

    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;