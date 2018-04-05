package org.stevenlowes.university.seassignment.dbao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stevenlowes.university.seassignment.database.Tx;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Patient {
    @NotNull
    private static final String scriptFolder = "patients/";
    private final long id;
    @NotNull
    private String surname;
    @NotNull
    private String firstName;
    @NotNull
    private String address1;
    @NotNull
    private String address2;
    @Nullable
    private String address3;
    @Nullable
    private String address4;
    @Nullable
    private String address5;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private String phoneNumber;
    private long practiceId;
    @NotNull
    private String nextOfKinSurname;
    @NotNull
    private String nextOfKinFirstName;
    @NotNull
    private String nextOfKinPhoneNumber;
    private long riskCategoryId;
    private long consultantId;

    private Patient(long id,
                    @NotNull String surname,
                    @NotNull String firstName,
                    @NotNull String address1,
                    @NotNull String address2,
                    @Nullable String address3,
                    @Nullable String address4,
                    @Nullable String address5,
                    @NotNull LocalDate dateOfBirth,
                    @NotNull String phoneNumber,
                    long practiceId,
                    @NotNull String nextOfKinSurname,
                    @NotNull String nextOfKinFirstName,
                    @NotNull String nextOfKinPhoneNumber,
                    long riskCategoryId,
                    long consultantId) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.practiceId = practiceId;
        this.nextOfKinSurname = nextOfKinSurname;
        this.nextOfKinFirstName = nextOfKinFirstName;
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
        this.riskCategoryId = riskCategoryId;
        this.consultantId = consultantId;
    }

    public Patient(@NotNull ResultSet rs) throws SQLException {
        this(rs.getLong("patients.id"),
             rs.getString("patients.surname"),
             rs.getString("patients.first_name"),
             rs.getString("patients.address_1"),
             rs.getString("patients.address_2"),
             rs.getString("patients.address_3"),
             rs.getString("patients.address_4"),
             rs.getString("patients.address_5"),
             rs.getDate("patients.date_of_birth").toLocalDate(),
             rs.getString("patients.phone"),
             rs.getLong("patients.practice_id"),
             rs.getString("patients.next_of_kin_surname"),
             rs.getString("patients.next_of_kin_first_name"),
             rs.getString("patients.next_of_kin_phone"),
             rs.getLong("patients.risk_category_id"),
             rs.getLong("patients.consultant_id"));
    }

    public Patient(long id) throws SQLException {
        this(Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "read");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs;
        }));
    }

    public Patient(@NotNull String surname,
                   @NotNull String firstName,
                   @NotNull String address1,
                   @NotNull String address2,
                   @Nullable String address3,
                   @Nullable String address4,
                   @Nullable String address5,
                   @NotNull LocalDate dateOfBirth,
                   @NotNull String phoneNumber,
                   long practiceId,
                   @NotNull String nextOfKinSurname,
                   @NotNull String nextOfKinFirstName,
                   @NotNull String nextOfKinPhoneNumber,
                   long riskCategoryId,
                   long consultantId,
                   @NotNull List<Long> prescribedMedicinesIds,
                   @NotNull List<Long> prescribedTreatmentsIds) throws SQLException {
        this(Tx.<Long>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "create");
            prep.setString(1, surname);
            prep.setString(2, firstName);
            prep.setString(3, address1);
            prep.setString(4, address2);
            prep.setString(5, address3);
            prep.setString(6, address4);
            prep.setString(7, address5);
            prep.setDate(8, Date.valueOf(dateOfBirth));
            prep.setString(9, phoneNumber);
            prep.setLong(10, practiceId);
            prep.setString(11, nextOfKinSurname);
            prep.setString(12, nextOfKinFirstName);
            prep.setString(13, nextOfKinPhoneNumber);
            prep.setLong(14, riskCategoryId);
            prep.setLong(15, consultantId);

            prep.execute();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            generatedKeys.next();
            long id = generatedKeys.getLong(1);
            setMtm(conn, id, prescribedMedicinesIds, prescribedTreatmentsIds);
            return id;
        }));
    }

    private static void setMtm(Connection conn, long patientId, List<Long> prescribedmedicinesIDs, List<Long> prescribedtreatmentsIDs) throws SQLException {
        String folder = scriptFolder + "setmtm/";

        List<String> scriptPaths = new ArrayList<>();
        scriptPaths.add(folder + "patient_id_variable");

        scriptPaths.add(folder + "clear_prescribed_medicines");
        String addmedicinesPath = folder + "add_prescribed_medicines";
        for (int i = 0; i < prescribedmedicinesIDs.size(); i++) {
            scriptPaths.add(addmedicinesPath);
        }

        scriptPaths.add(folder + "clear_prescribed_treatments");
        String addtreatmentsPath = folder + "add_prescribed_treatments";
        for (int i = 0; i < prescribedtreatmentsIDs.size(); i++) {
            scriptPaths.add(addtreatmentsPath);
        }

        PreparedStatement mtmPrep = Tx.prep(conn, scriptPaths.toArray(new String[scriptPaths.size()]));
        int i = 0;
        mtmPrep.setLong(++i, patientId);
        for (Long id : prescribedmedicinesIDs) {
            mtmPrep.setLong(++i, id);
        }
        for (Long id : prescribedtreatmentsIDs) {
            mtmPrep.setLong(++i, id);
        }

        mtmPrep.execute();
    }

    public static List<Patient> list() throws SQLException {
        return Tx.execute(conn -> {
            ResultSet rs = Tx.prep(conn, scriptFolder + "list").executeQuery();
            List<Patient> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Patient(rs));
            }
            return list;
        });
    }

    public void update(@NotNull String surname,
                       @NotNull String firstName,
                       @NotNull String address1,
                       @NotNull String address2,
                       @Nullable String address3,
                       @Nullable String address4,
                       @Nullable String address5,
                       @NotNull LocalDate dateOfBirth,
                       @NotNull String phoneNumber,
                       long practiceId,
                       @NotNull String nextOfKinSurname,
                       @NotNull String nextOfKinFirstName,
                       @NotNull String nextOfKinPhoneNumber,
                       long riskCategoryId,
                       long consultantId,
                       @NotNull List<Long> prescribedmedicinesIds,
                       @NotNull List<Long> prescribedtreatmentsIds) throws SQLException {
        Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "update");
            prep.setString(1, surname);
            prep.setString(2, firstName);
            prep.setString(3, address1);
            prep.setString(4, address2);
            prep.setString(5, address3);
            prep.setString(6, address4);
            prep.setString(7, address5);
            prep.setDate(8, Date.valueOf(dateOfBirth));
            prep.setString(9, phoneNumber);
            prep.setLong(10, practiceId);
            prep.setString(11, nextOfKinSurname);
            prep.setString(12, nextOfKinFirstName);
            prep.setString(13, nextOfKinPhoneNumber);
            prep.setLong(14, riskCategoryId);
            prep.setLong(15, consultantId);
            prep.setLong(16, id);
            prep.execute();

            setMtm(conn, id, prescribedmedicinesIds, prescribedtreatmentsIds);
            return true;
        });

        this.surname = surname;
        this.firstName = firstName;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.practiceId = practiceId;
        this.nextOfKinSurname = nextOfKinSurname;
        this.nextOfKinFirstName = nextOfKinFirstName;
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
        this.riskCategoryId = riskCategoryId;
        this.consultantId = consultantId;
    }

    public void delete() throws SQLException {
        Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "delete");
            prep.setLong(1, id);
            return prep.execute();
        });
    }

    public long getId() {
        return id;
    }

    @NotNull
    public String getSurname() {
        return surname.substring(0, 1).toUpperCase() + surname.substring(1);
    }

    @NotNull
    public String getFirstName() {
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
    }

    @NotNull
    public String getAddress1() {
        return address1;
    }

    @NotNull
    public String getAddress2() {
        return address2;
    }

    @Nullable
    public String getAddress3() {
        return address3;
    }

    @Nullable
    public String getAddress4() {
        return address4;
    }

    @Nullable
    public String getAddress5() {
        return address5;
    }

    @NotNull
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @NotNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getPracticeId() {
        return practiceId;
    }

    @NotNull
    public String getNextOfKinSurname() {
        return nextOfKinFirstName.substring(0, 1).toUpperCase() + nextOfKinFirstName.substring(1);
    }

    @NotNull
    public String getNextOfKinFirstName() {
        return nextOfKinSurname.substring(0, 1).toUpperCase() + nextOfKinSurname.substring(1);
    }

    @NotNull
    public String getNextOfKinPhoneNumber() {
        return nextOfKinPhoneNumber;
    }

    public long getRiskCategoryId() {
        return riskCategoryId;
    }

    public long getConsultantId() {
        return consultantId;
    }

    public Practice getPractice() throws SQLException {
        return new Practice(practiceId);
    }

    public RiskCategory getRiskCategory() throws SQLException {
        return new RiskCategory(riskCategoryId);
    }

    public Consultant getConsultant() throws SQLException {
        return new Consultant(consultantId);
    }

    public List<Treatment> getTreatments() throws SQLException {
        return Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "prescribed_treatments");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            return Tx.parseRS(rs, resultSet -> new Treatment(rs));
        });
    }

    public List<Medicine> getMedicines() throws SQLException {
        return Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "prescribed_medicines");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            return Tx.parseRS(rs, resultSet -> new Medicine(rs));
        });
    }

    public List<Consultation> getConsultations() throws SQLException {
        return Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "consultations");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            return Tx.parseRS(rs, Consultation::new);
        });
    }

    public String getPostcode() {
        if (address5 != null && !address5.isEmpty()) {
            return address5.toUpperCase();
        }
        else if (address4 != null && !address4.isEmpty()) {
            return address4.toUpperCase();
        }
        else if (address3 != null && !address3.isEmpty()) {
            return address3.toUpperCase();
        }
        else if (!address2.isEmpty()) {
            return address2.toUpperCase();
        }
        else if (!address1.isEmpty()) {
            return address1.toUpperCase();
        }
        return null;
    }

    @Override
    public String toString() {
        return getId() + ": " + getFirstName() + " " + getSurname() + " (" + getDateOfBirth().toString() + ") " + getAddress1() + ", " + getPostcode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Patient patient = (Patient) o;
        return id == patient.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}