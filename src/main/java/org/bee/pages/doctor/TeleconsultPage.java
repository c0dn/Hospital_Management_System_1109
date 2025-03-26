package org.bee.pages.doctor;

//import org.bee.controllers.MedicinesController;
import org.bee.hms.telemed.Appointment;
import org.bee.hms.telemed.MedicalCertificate;
import org.bee.controllers.AppointmentController;
//import org.bee.hms.Prescription;
import org.bee.ui.*;
import org.bee.ui.views.ListView;
import org.bee.ui.views.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TeleconsultPage extends UiBase {
    private static Appointment appointment;
    private ListView listView;

    public static void setAppointment(Appointment appointment) {
        TeleconsultPage.appointment = appointment;
    }

    @Override
    protected View createView() {
        listView = new ListView(
                this.canvas,
                Color.CYAN
        );
        listView.setTitleHeader("Teleconsult");
        return listView;
    }

    public void OnViewCreated(View parentView) {
        ListView lv = (ListView) parentView;
        lv.attachUserInput("Set Doctor Notes", str -> {
            System.out.println("Enter Doctor Notes :");
            Scanner scanner = new Scanner(System.in);
            String newNotes = scanner.nextLine();
            appointment.setDoctorNotes(newNotes);
            refreshUi();
        });
/*
        lv.attachUserInput("Add Prescription", str -> {
            // -1 sets the function to add mode
            setPrescribeMeds(-1);
            refreshUi();
        });

        lv.attachUserInput("Edit Prescription", str -> {
            int selectedIndex = selectMedicineIndexPrompt();
            if(selectedIndex == -1){
                refreshUi();
                return;
            }
            setPrescribeMeds(selectedIndex);
            refreshUi();
        });

        lv.attachUserInput("Remove Prescription\n", str -> {
            int selectedIndex = selectMedicineIndexPrompt();
            if (selectedIndex == -1) {
                refreshUi();
                return;
            }
            appointment.getBilling().getPrescription().removeMedicineAtIndex(selectedIndex);
            refreshUi();
        });

 */

        lv.attachUserInput("Add Medical Certificate", str->{
            Scanner scanner;

            // according to official government policy, MOM allocates mandatory 14 days MC. Above that, most companies consider it
            // hospitalisation leave. Therefore, telemedicine should not usually provide more than a few days of MC
            // 14 is a very reasonable upper limit to set here. Anymore and they should be inpatient/physical consult.
//            int days = InputHelper.getValidIndex("Medical Certificate No. Days * incl today:", 1, 14);

            scanner = new Scanner(System.in);
            System.out.println("Medical Certificate Remarks: ");
            String remarks = scanner.nextLine();

            LocalDate today = LocalDate.now();
            LocalDate endDay = today.plusDays(12 - 1);
            MedicalCertificate mc = new MedicalCertificate(today.atStartOfDay(), endDay.atTime(23, 59), remarks);
            appointment.setMedicalCertificate(mc);

            refreshUi();
        });
        lv.attachUserInput("Remove Medical Certificate", str->{
            if(appointment.getMc() == null){
                System.out.println("Medical Certificate not yet created. Returning...");
                refreshUi();
                return;
            }
            appointment.setMedicalCertificate(null);
            refreshUi();
        });

        lv.attachUserInput("Finish Consultation", str->{
            System.out.println("Finishing consultation...");
            System.out.println("Calculating Billing for patient...");
            appointment.finishAppointment(appointment.getDoctorNotes());
            AppointmentController.getInstance().updateAppointment(appointment, appointment);
//            canvas.previousPage();
            this.OnBackPressed();
        });
        refreshUi();
    }

/*
    private int selectMedicineIndexPrompt(){
        List<Medicine> medicines = appointment.getBilling().getPrescription().getMedicines();
        if(medicines == null || medicines.isEmpty()){
            System.out.println("No medicines have been added yet, returning...");
            refreshUi();
            return -1;
        }
        return InputHelper.getValidIndex("Select medicine index to edit", medicines);
    }
    /**
     * Sets the medicine, either at the index or add a new medicine when index = -1
     * @param index -1 when adding, otherwise, specifying index will add a new medicine

    private void setPrescribeMeds(int index){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the drug name: ");
        // always use upper case
        String medicineName = scanner.nextLine().trim().toUpperCase();

        System.out.println("Enter the amount: ");
        int medicineAmount = scanner.nextInt();
        // somehow creating a new scanner prevents errors where it may just skip the scanner.
        scanner = new Scanner(System.in);
        Prescription prescription = appointment.getBilling().getPrescription();

        // if the medicine is in the database, automatically fill in the dosage
        // otherwise prompt for the dosage and instructions.
        if (MedicinesController.findAvailableMedicineByName(medicineName) != null) {
            if (index == -1) {
                prescription.addMedicine(medicineName, medicineAmount, "");
            } else {
                prescription.setMedicineAtIndex(medicineName, medicineAmount, "", index);
            }
        } else {
            System.out.println("Enter the dosage/instructions: ");
            String dosage = scanner.nextLine();

            if (index == -1) {
                prescription.addMedicine(medicineName, medicineAmount, dosage);
            } else {
                prescription.setMedicineAtIndex(medicineName, medicineAmount, dosage, index);
            }
        }

        refreshUi();
    }
*/
    private void refreshUi () {
        listView.clear();
        listView.addItem(new TextView(this.canvas, "Patient Name: " + appointment.getPatient().getName(), Color.GREEN, TextStyle.ITALIC));
        //listView.addItem(new TextView(this.canvas, "Patient Contact No: " + appointment.getPatient().getPhoneNumber(), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Appointment Time: " + appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Zoom link: " + appointment.getSession().getZoomLink(), Color.GREEN, TextStyle.ITALIC));
        listView.addItem(new TextView(this.canvas, "Reason: " + appointment.getReason() + "\n", Color.GREEN, TextStyle.ITALIC));
        String MCString = "Medical Certificate not yet given.";

        // separate the editable items with different colours
        if(appointment.getMc() != null){
            MCString = "Medical Certificate: from: " +
                    appointment.getMc().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")) +" to: " +
                    appointment.getMc().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM"));
        }
        listView.addItem(new TextView(this.canvas,
                MCString,
                Color.CYAN,
                TextStyle.ITALIC));

        // "null" looks ugly haha
        String doctorNotes = appointment.getDoctorNotes() == null ? "Not yet set" : appointment.getDoctorNotes();
        listView.addItem(new TextView(this.canvas, "Doctor Notes/Follow up: " + doctorNotes, Color.CYAN, TextStyle.ITALIC));

        // Add prescription details
        //Prescription prescription = appointment.getBilling().getPrescription();
        //if(prescription != null) {
            //List<Medicine> medicines = prescription.getMedicines();
            //if (medicines != null && !medicines.isEmpty()) {
                //listView.addItem(new TextView(this.canvas, "Prescription:", Color.BLUE, TextStyle.BOLD));
                //for (int i =0; i < medicines.size(); i++) {
                    //Medicine medicine = medicines.get(i);
                    //listView.addItem(new TextView(this.canvas, i + ".  - " + medicine.getMedName() + " x " + medicine.getMedQuantity() + " | Dosage: " + medicine.getMedDosage(), Color.GREEN));
                //}
            //}
        //}

        //canvas.setRequireRedraw(true);
    }
}
