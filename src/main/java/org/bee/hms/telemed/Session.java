package org.bee.hms.telemed;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bee.utils.JSONSerializable;

/**
 * Represents a session for a virtual meeting via Zoom in the context of medical or professional interactions.
 * This class handles session details such as start and end times, connectivity issues, and session statuses,
 * providing methods to manage and record session activities effectively.
 */
public class Session implements JSONSerializable {
    /** Unique identifier for the session */
    private String id;

    /** Session start time */
    private LocalDateTime startTime;

    /** Session end time */
    private LocalDateTime endTime;

    /** General notes or comments about the session */
    private String remarks;

    /** Description of any technical difficulties encountered */
    private String connectivityIssues;

    /** URL for joining Zoom */
    private String zoomLink;

    /** Current state of the session (e.g., SCHEDULED, COMPLETED) */
    private SessionStatus sessionStatus;

    /**
     * Constructs a new Session instance with a specified Zoom link for virtual meetings.
     * This constructor initializes the session with a unique identifier, sets the start time to the current time,
     * and assigns the provided Zoom link. It also sets the initial session status to ONGOING, indicating that the session
     * is active from the time of creation.
     *
     * @param zoomLink A string representing the Zoom meeting link for the session. This link is crucial for participants to join the virtual session.
     */
    public Session(String zoomLink) {
        this.id = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.zoomLink = Objects.requireNonNull(zoomLink);
        this.sessionStatus = SessionStatus.ONGOING;
    }

    /**
     * Constructs a Session instance from JSON with default values
     * <p>
     * Default values applied when:
     * <ul>
     * <li>ID is null: Generates random UUID</li>
     * <li>startTime is null: Uses current timestamp</li>
     * <li>sessionStatus is null: Sets to ONGOING</li>
     * </ul>
     *
     * @param id Unique session identifier
     * @param startTime When the session began
     * @param zoomLink URL for virtual session
     * @param sessionStatus Current state of the session
     */
    @JsonCreator
    public Session(
            @JsonProperty("id") String id,
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("zoomLink") String zoomLink,
            @JsonProperty("sessionStatus") SessionStatus sessionStatus) {

        this.id = (id != null) ? id : UUID.randomUUID().toString();
        this.startTime = (startTime != null) ? startTime : LocalDateTime.now();
        this.zoomLink = zoomLink;
        this.sessionStatus = (sessionStatus != null) ? sessionStatus : SessionStatus.ONGOING;
    }

    /**
     * Gets the unique session identifier
     * @return Session ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique session identifier
     * @param id New session ID
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * Gets the session start timestamp.
     * @return When the session began
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the session start timestamp.
     * @param startTime When the session began
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the session remarks/notes.
     * @return Free-text comments about the session
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the session remarks/notes.
     * @param remarks Free-text comments about the session
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets any reported connectivity issues.
     * @return Description of technical difficulties during session
     */
    public String getConnectivityIssues() {
        return connectivityIssues;
    }

    /**
     * Sets the connectivity issues encountered during the session
     * @param connectivityIssues Description of technical difficulties
     */
    public void setConnectivityIssues(String connectivityIssues) {
        this.connectivityIssues = connectivityIssues;
    }

    /**
     * Gets the Zoom meeting link for the session
     * @return Virtual session URL
     */
    public String getZoomLink() {
        return zoomLink;
    }

    /**
     * Sets the Zoom meeting link for the session
     * @param zoomLink Zoom URL
     */
    public void setZoomLink(String zoomLink) {
        this.zoomLink = zoomLink;
    }

    /**
     * Gets the current status of the session
     * @return Session lifecycle state
     */
    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    /**
     * Sets the current status of the session.
     * @param sessionStatus New session state
     */
    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    /**
     * Gets the end time of the session.
     * @return Session conclusion timestamp
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the session.
     * @param time Session conclusion timestamp
     */
    public void setEndTime(LocalDateTime time) {
        this.endTime = time;
    }


    /**
     * Marks the session as completed by updating the session status and recording the end time.
     * This method sets the session status to COMPLETED and captures the current time as the end time of the session.
     * It should be called when all session activities are concluded to ensure the session is properly closed in the system.
     */
    public void endSession(){
        setSessionStatus(SessionStatus.COMPLETED);
        setEndTime(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) && Objects.equals(startTime, session.startTime)  && Objects.equals(remarks, session.remarks) && Objects.equals(connectivityIssues, session.connectivityIssues);
        //&& Objects.equals(medicines, session.medicines)
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, remarks, connectivityIssues);
        //add medicines back later
    }
}
