package org.bee.telemed;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a session for a virtual meeting via Zoom in the context of medical or professional interactions.
 * This class handles session details such as start and end times, connectivity issues, and session statuses,
 * providing methods to manage and record session activities effectively.
 */
public class Session  {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String remarks;
    private String connectivityIssues;
    private String zoomLink;
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
        this.startTime =   LocalDateTime.now();
        this.zoomLink = zoomLink;
        this.sessionStatus = SessionStatus.ONGOING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getConnectivityIssues() {
        return connectivityIssues;
    }

    public void setConnectivityIssues(String connectivityIssues) {
        this.connectivityIssues = connectivityIssues;
    }

    public String getZoomLink() {return zoomLink;}
    public void setZoomLink(String zoomLink) {this.zoomLink = zoomLink;}

    public SessionStatus getSessionStatus(){return sessionStatus;};
    public void setSessionStatus(SessionStatus sessionStatus){this.sessionStatus = sessionStatus;}

    public LocalDateTime getEndTime(){return endTime;}
    public void setEndTime(LocalDateTime time){this.endTime= time;}
    ;

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
