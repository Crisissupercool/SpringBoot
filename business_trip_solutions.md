# Business Trip App - Aufgaben Lösungen

## Aufgabe 1: Meeting Entity vervollständigen

### a) BusinessTrip-Meeting Modell mit JPA Beziehungen

Die Entities sind bereits korrekt implementiert mit den bidirektionalen Beziehungen:

**BusinessTrip.java** (bereits korrekt):
```java
@OneToMany(mappedBy = "businessTrip")
@JsonManagedReference
private List<Meeting> meetings;
```

**Meeting.java** (bereits korrekt):
```java
@ManyToOne
@JoinColumn(name = "business_trip_idfs")
@JsonBackReference
private BusinessTrip businessTrip;
```

### b) MeetingRepository
Das MeetingRepository ist bereits vorhanden und korrekt implementiert:

```java
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    List<Meeting> findByTitle(String title); 
}
```

### c) HomeController Erweiterung
Der HomeController könnte erweitert werden:

```java
@GetMapping("/meetings")
@ResponseBody
public List<Meeting> getAllMeetings() {
    log.info("Getting all meetings");
    return (List<Meeting>) meetingRepository.findAll();
}
```

### d) HTML-Seite mit Thymeleaf
Eine neue HTML-Seite für Meetings würde so aussehen:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Business Trip Meetings</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Business Trip Meetings</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Business Trip</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="meeting : ${meetings}">
                    <td th:text="${meeting.id}"></td>
                    <td th:text="${meeting.title}"></td>
                    <td th:text="${meeting.description}"></td>
                    <td th:text="${meeting.businessTrip.title}"></td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
```

### e) Sortierte Liste mit Comparator
```java
@GetMapping("/meetings/sorted")
public List<Meeting> getSortedMeetings() {
    List<Meeting> meetings = (List<Meeting>) meetingRepository.findAll();
    meetings.sort(Comparator.comparing(Meeting::getTitle));
    return meetings;
}
```

## Aufgabe 2: Vervollständigung der Business-Trip App

### Verbesserte Controller mit ResponseEntity

**BusinessTripController.java** (erweitert):
```java
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
```

**MeetingController.java** (erweitert):
```java
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
```

### Erweiterte Repository-Methoden

**BusinessTripRepository.java** (erweitert):
```java
public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {
    List<BusinessTrip> findByTitle(String title);
    List<BusinessTrip> findByStartTripBetween(LocalDateTime start, LocalDateTime end);
    List<BusinessTrip> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT bt FROM BusinessTrip bt WHERE bt.endTrip > :currentDate")
    List<BusinessTrip> findUpcomingTrips(@Param("currentDate") LocalDateTime currentDate);
}
```

**MeetingRepository.java** (erweitert):
```java
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    List<Meeting> findByTitle(String title);
    List<Meeting> findByBusinessTripId(Long businessTripId);
    List<Meeting> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT m FROM Meeting m WHERE m.businessTrip.id = :tripId ORDER BY m.title")
    List<Meeting> findByBusinessTripIdOrderByTitle(@Param("tripId") Long tripId);
}
```

## Aufgabe 3: Tests mit Postman

### Postman Collection Endpoints

**Business Trips:**
- GET `/v1/trips` - Alle Business Trips abrufen
- GET `/v1/trips/{id}` - Einzelnen Business Trip abrufen
- POST `/v1/trips` - Neuen Business Trip erstellen
- PUT `/v1/trips/{id}` - Business Trip aktualisieren
- DELETE `/v1/trips/{id}` - Business Trip löschen

**Meetings:**
- GET `/v1/meetings` - Alle Meetings abrufen
- GET `/v1/meetings/{id}` - Einzelnes Meeting abrufen
- POST `/v1/meetings` - Neues Meeting erstellen
- PUT `/v1/meetings/{id}` - Meeting aktualisieren
- DELETE `/v1/meetings/{id}` - Meeting löschen

### Test-Beispiele

**POST /v1/trips** Body:
```json
{
    "title": "Berlin Tech Conference",
    "description": "Annual technology conference in Berlin",
    "startTrip": "2024-09-15T09:00:00",
    "endTrip": "2024-09-18T17:00:00"
}
```

**POST /v1/meetings** Body:
```json
{
    "title": "AI Workshop",
    "description": "Introduction to Artificial Intelligence",
    "businessTrip": {
        "id": 1
    }
}
```

## Aufgabe 4: Clean Code Prinzipien

### Umgesetzte Clean Code Prinzipien:

1. **Single Responsibility Principle (SRP)**: 
   - Jede Klasse hat eine klare Verantwortung
   - Controller nur für HTTP-Requests
   - Repositories nur für Datenbankoperationen
   - Entities nur für Datenmodellierung

2. **KISS (Keep It Simple, Stupid)**:
   - Einfache, verständliche Methodennamen
   - Klare Struktur ohne unnötige Komplexität

3. **DRY (Don't Repeat Yourself)**:
   - Verwendung von Spring Boot Annotationen
   - Wiederverwendbare Repository-Interfaces

4. **Testbarkeit**:
   - Dependency Injection ermöglicht einfaches Testen
   - Klare Trennung der Schichten

### Empfohlene Verbesserungen:

1. **DTO-Klassen** für API-Responses
2. **Service-Layer** zwischen Controller und Repository
3. **Validation** mit Bean Validation
4. **Exception Handling** mit @ControllerAdvice
5. **Logging** mit SLF4J für bessere Nachverfolgung

## Realistische Testdaten

### Erweiterte Testdaten im CommandLineRunner:

```java
@Bean
public CommandLineRunner demoData(BusinessTripRepository businessTripRepository, MeetingRepository meetingRepository) {
    return (args) -> {
        // Business Trips
        BusinessTrip bt01 = new BusinessTrip(null, "Berlin Tech Summit", "Annual technology conference in Berlin", 
            LocalDateTime.of(2024, 9, 15, 9, 0), LocalDateTime.of(2024, 9, 18, 17, 0));
        BusinessTrip bt02 = new BusinessTrip(null, "Zurich Finance Forum", "International finance summit in Zurich", 
            LocalDateTime.of(2024, 10, 5, 8, 30), LocalDateTime.of(2024, 10, 8, 18, 0));
        BusinessTrip bt03 = new BusinessTrip(null, "Milan Design Week", "Design and innovation conference in Milan", 
            LocalDateTime.of(2024, 11, 12, 9, 0), LocalDateTime.of(2024, 11, 15, 16, 0));
        BusinessTrip bt04 = new BusinessTrip(null, "Amsterdam AI Conference", "Artificial Intelligence conference", 
            LocalDateTime.of(2024, 12, 3, 9, 0), LocalDateTime.of(2024, 12, 6, 17, 30));
        BusinessTrip bt05 = new BusinessTrip(null, "Barcelona Mobile World", "Mobile technology exhibition", 
            LocalDateTime.of(2025, 2, 24, 8, 0), LocalDateTime.of(2025, 2, 27, 19, 0));

        // Speichern der Business Trips
        bt01 = businessTripRepository.save(bt01);
        bt02 = businessTripRepository.save(bt02);
        bt03 = businessTripRepository.save(bt03);
        bt04 = businessTripRepository.save(bt04);
        bt05 = businessTripRepository.save(bt05);

        // Meetings
        Meeting m01 = new Meeting(null, "Keynote: Future of Tech", "Opening keynote on technology trends", bt01);
        Meeting m02 = new Meeting(null, "AI Workshop", "Hands-on artificial intelligence workshop", bt01);
        Meeting m03 = new Meeting(null, "Blockchain Panel", "Panel discussion on blockchain technology", bt01);
        Meeting m04 = new Meeting(null, "Finance Regulations", "Update on financial regulations", bt02);
        Meeting m05 = new Meeting(null, "Investment Strategies", "Modern investment approaches", bt02);
        Meeting m06 = new Meeting(null, "Design Thinking", "Creative design methodologies", bt03);
        Meeting m07 = new Meeting(null, "UX Best Practices", "User experience design guidelines", bt03);
        Meeting m08 = new Meeting(null, "Machine Learning Basics", "Introduction to ML concepts", bt04);
        Meeting m09 = new Meeting(null, "Deep Learning Advanced", "Advanced neural network topics", bt04);
        Meeting m10 = new Meeting(null, "5G Technology", "Overview of 5G mobile networks", bt05);

        // Speichern der Meetings
        meetingRepository.save(m01);
        meetingRepository.save(m02);
        meetingRepository.save(m03);
        meetingRepository.save(m04);
        meetingRepository.save(m05);
        meetingRepository.save(m06);
        meetingRepository.save(m07);
        meetingRepository.save(m08);
        meetingRepository.save(m09);
        meetingRepository.save(m10);

        log.info("Demo data loaded successfully!");
    };
}
```

## Zusammenfassung

Die Business Trip App wurde erfolgreich erweitert mit:

- ✅ Vollständigen JPA-Beziehungen zwischen BusinessTrip und Meeting
- ✅ Erweiterten Repository-Methoden
- ✅ RESTful API mit korrekten HTTP-Status-Codes
- ✅ ResponseEntity für alle Controller-Methoden
- ✅ CORS-Konfiguration für Frontend-Integration
- ✅ Realistischen Testdaten
- ✅ Clean Code Prinzipien
- ✅ Dokumentation für Postman-Tests

Die App ist bereit für die Verwendung mit einem Frontend und kann mit Tools wie Postman getestet werden.