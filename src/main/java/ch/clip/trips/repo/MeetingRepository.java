package ch.clip.trips.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ch.clip.trips.model.Meeting;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    List<Meeting> findByTitle(String title);
    List<Meeting> findByBusinessTripId(Long businessTripId);
    List<Meeting> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT m FROM Meeting m WHERE m.businessTrip.id = :tripId ORDER BY m.title")
    List<Meeting> findByBusinessTripIdOrderByTitle(@Param("tripId") Long tripId);
}