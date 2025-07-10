package ch.clip.trips.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.clip.trips.ex.TriptNotFoundException;
import ch.clip.trips.model.BusinessTrip;
import ch.clip.trips.repo.BusinessTripRepository;

@RestController
@RequestMapping("/v1")
public class BusinessTripController {

    @Autowired
    private BusinessTripRepository tripRepository;

    @CrossOrigin(origins = "*")
    @GetMapping("/trips")
    public ResponseEntity<List<BusinessTrip>> getAllTrips() {
        List<BusinessTrip> trips = tripRepository.findAll();
        if (trips.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(trips); // 200 OK
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/trips/{id}")
    public ResponseEntity<BusinessTrip> getTripById(@PathVariable Long id) {
        return tripRepository.findById(id)
                .map(trip -> ResponseEntity.ok(trip)) // 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/trips")
    public ResponseEntity<BusinessTrip> createTrip(@RequestBody BusinessTrip newTrip) {
        BusinessTrip savedTrip = tripRepository.save(newTrip);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip); // 201 Created
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/trips/{id}")
    public ResponseEntity<BusinessTrip> updateTrip(@RequestBody BusinessTrip newTrip, @PathVariable Long id) {
        return tripRepository.findById(id)
                .map(trip -> {
                    trip.setTitle(newTrip.getTitle());
                    trip.setDescription(newTrip.getDescription());
                    trip.setStartTrip(newTrip.getStartTrip());
                    trip.setEndTrip(newTrip.getEndTrip());
                    return ResponseEntity.ok(tripRepository.save(trip)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/trips/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}