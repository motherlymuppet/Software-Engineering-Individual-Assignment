package org.stevenlowes.university.seassignment.dbao;

import org.jetbrains.annotations.NotNull;
import org.stevenlowes.university.seassignment.database.Tx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Consultation {
    @NotNull
    private static final String scriptFolder = "consultations/";
    private final long id;
    @NotNull
    private Instant time;
    private long practiceId;
    private long consultantId;
    private long patientId;

    private Consultation(long id, @NotNull Instant time, long practiceId, long consultantId, long patientId) {
        this.id = id;
        this.time = time;
        this.practiceId = practiceId;
        this.consultantId = consultantId;
        this.patientId = patientId;
    }

    public Consultation(@NotNull ResultSet rs) throws SQLException {
        this(rs.getLong("consultations.id"),
             rs.getTimestamp("consultations.time").toInstant(),
             rs.getLong("consultations.practice_id"),
             rs.getLong("consultations.consultant_id"),
             rs.getLong("consultations.patient_id"));
    }

    public Consultation(long id) throws SQLException {
        this(Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "read");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs;
        }));
    }

    public Consultation(@NotNull Instant time, long practiceId, long consultantId, long patientId) throws SQLException {
        this(Tx.<Long>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "create");
            prep.setTimestamp(1, Timestamp.from(time));
            prep.setLong(2, practiceId);
            prep.setLong(3, consultantId);
            prep.setLong(4, patientId);
            prep.execute();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);
        }));
    }

    public static List<Consultation> list() throws SQLException {
        return Tx.execute(conn -> {
            ResultSet rs = Tx.prep(conn, scriptFolder + "list").executeQuery();
            List<Consultation> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Consultation(rs));
            }
            return list;
        });
    }

    public void update(@NotNull Instant time, long practiceId, long consultantId, long patientId) throws SQLException {
        Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "update");
            prep.setTimestamp(1, Timestamp.from(time));
            prep.setLong(2, practiceId);
            prep.setLong(3, consultantId);
            prep.setLong(4, patientId);
            prep.setLong(5, id);
            return prep.execute();
        });

        this.time = time;
        this.practiceId = practiceId;
        this.consultantId = consultantId;
        this.patientId = patientId;
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
    public Instant getTime() {
        return time;
    }

    public long getPracticeId() {
        return practiceId;
    }

    public long getConsultantId() {
        return consultantId;
    }

    public long getPatientId() {
        return patientId;
    }

    public Practice getPractice() throws SQLException {
        return new Practice(practiceId);
    }

    public Consultant getConsultant() throws SQLException {
        return new Consultant(consultantId);
    }

    public Patient getPatient() throws SQLException {
        return new Patient(patientId);
    }

    @Override
    public String toString() {
        OffsetDateTime offsetDateTime = time.atOffset(ZoneOffset.UTC);
        int hours = offsetDateTime.getHour();
        int mins = offsetDateTime.getMinute();
        try {
            return getConsultant().toString() + "@" + getPractice().toString() + " " + hours + ":" + mins;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return "Error Retrieving practice/consultant";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Consultation that = (Consultation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
