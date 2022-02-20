package com.ef.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT ip, count(ip) as number FROM request WHERE (time_stamp>?1 AND time_stamp<?2) GROUP BY ip HAVING number>?3", nativeQuery = true)
    List<?> findExceededIps(String startTimeStamp, String endTimeStamp, int threshold);
}
