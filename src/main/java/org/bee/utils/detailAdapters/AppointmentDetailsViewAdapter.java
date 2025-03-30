package org.bee.utils.detailAdapters;

import org.bee.hms.humans.Doctor;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.hms.telemed.Session;
import org.bee.hms.telemed.SessionStatus;
import org.bee.ui.details.IDetailsViewAdapter;
import org.bee.ui.views.DetailsView;
import org.bee.utils.ReflectionHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Adapter for displaying Appointment details.
 * This adapter configures an ObjectDetailsView to show appointment information
 * organized into relevant sections.
 */
public class AppointmentDetailsViewAdapter implements IDetailsViewAdapter<Appointment> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public DetailsView<Appointment> configureView(DetailsView<Appointment> view, Appointment appointment) {
        addDetailsFromProperties(view, "Appointment Information", appointment, new String[][] {
                {"ID", "appointmentId", "Not available"}
        });

        LocalDateTime appointmentTime = (LocalDateTime) ReflectionHelper.propertyAccessor("appointmentTime", null).apply(appointment);
        if (appointmentTime != null) {
            view.addDetail("Appointment Information", "Appointment Time", DATE_FORMATTER.format(appointmentTime));
        }


        Object statusObj = ReflectionHelper.propertyAccessor("status", null).apply(appointment);
        if (statusObj != null) {
            view.addDetail("Appointment Information", "Status", statusObj.toString());
        } else {
            view.addDetail("Appointment Information", "Status", "Not set");
        }

        Session session = appointment.getSession();
        String doctorNotes = ReflectionHelper.stringPropertyAccessor("doctorNotes", null).apply(appointment);
        if (session != null && doctorNotes != null && !doctorNotes.isEmpty()) {
            view.addDetail("Session Information", "Start Time", String.valueOf(session.getStartTime()));
            view.addDetail("Session Information", "End Time", String.valueOf(session.getEndTime()));
            view.addDetail("Session Information", "Doctor Notes", doctorNotes);
            view.addDetail("Session Information", "Remarks", session.getRemarks());
            if (session.getSessionStatus() == SessionStatus.ONGOING) {
                view.addDetail("Session Information", "Zoom Link", session.getZoomLink());
            }
        } else {
            view.addDetail("Session Information", "Session", "Not available");
        }

        MedicalCertificate mc = appointment.getMc();
        if (mc != null) {
            view.addDetail("Medical Certificate", "Start Date", String.valueOf(mc.getStartDate()));
            view.addDetail("Medical Certificate", "End Date", String.valueOf(mc.getEndDate()));
            view.addDetail("Medical Certificate", "Remarks", mc.getRemarks());
        } else {
            view.addDetail("Medical Certificate", "", "Not issued");
        }


        Doctor doctor = appointment.getDoctor();
        if (doctor != null) {
            view.addDetail("Doctor Information", "Doctor Name", doctor.getName());
            view.addDetail("Doctor Information", "Staff ID", doctor.getStaffId());
        } else {
            view.addDetail("Doctor Information", "Doctor", "Not assigned");
        }

        return view;
    }

    @Override
    public String getObjectTypeName() {
        return "Appointment";
    }
}
