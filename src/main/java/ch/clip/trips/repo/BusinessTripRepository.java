package ch.clip.trips.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ch.clip.trips.model.BusinessTrip;

public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {
    List<BusinessTrip> findByTitle(String title);
    List<BusinessTrip> findByStartTripBetween(LocalDateTime start, LocalDateTime end);
    List<BusinessTrip> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT bt FROM BusinessTrip bt WHERE bt.endTrip > :currentDate")
    List<BusinessTrip> findUpcomingTrips(@Param("currentDate") LocalDateTime currentDate);
}