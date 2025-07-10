package ch.clip.trips.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ch.clip.trips.model.Meeting;
import ch.clip.trips.repo.MeetingRepository;

@RestController
@RequestMapping("/v1")
public class MeetingController {
    private static final Logger log = LoggerFactory.getLogger(MeetingController.class);

    @Autowired
    private MeetingRepository meetingRepository;

    @CrossOrigin(origins = "*")
    @GetMapping("/meetings")
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = (List<Meeting>) meetingRepository.findAll();
        if (meetings.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(meetings); // 200 OK
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/meetings/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        return meetingRepository.findById(id)
                .map(meeting -> ResponseEntity.ok(meeting)) // 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/meetings")
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting newMeeting) {
        Meeting savedMeeting = meetingRepository.save(newMeeting);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMeeting); // 201 Created
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/meetings/{id}")
    public ResponseEntity<Meeting> updateMeeting(@RequestBody Meeting newMeeting, @PathVariable Long id) {
        return meetingRepository.findById(id)
                .map(meeting -> {
                    meeting.setTitle(newMeeting.getTitle());
                    meeting.setDescription(newMeeting.getDescription());
                    meeting.setBusinessTrip(newMeeting.getBusinessTrip());
                    return ResponseEntity.ok(meetingRepository.save(meeting)); // 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/meetings/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        if (meetingRepository.existsById(id)) {
            meetingRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}