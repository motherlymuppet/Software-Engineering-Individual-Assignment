package org.stevenlowes.university.seassignment.dbao;

import org.jetbrains.annotations.NotNull;
import org.stevenlowes.university.seassignment.database.Tx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Consultant {
    @NotNull
    private static final String scriptFolder = "consultants/";
    private final long id;
    @NotNull
    private String title;
    @NotNull
    private String surname;

    private Consultant(long id, @NotNull String title, @NotNull String surname) {
        this.id = id;
        this.title = title;
        this.surname = surname;
    }

    public Consultant(@NotNull ResultSet rs) throws SQLException {
        this(rs.getLong("consultants.id"), rs.getString("consultants.title"), rs.getString("consultants.surname"));
    }

    public Consultant(long id) throws SQLException {
        this(Tx.<ResultSet>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "read");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs;
        }));
    }

    public Consultant(@NotNull String title, @NotNull String surname) throws SQLException {
        this(Tx.<Long>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "create");
            prep.setString(1, title);
            prep.setString(2, surname);
            prep.execute();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);
        }));
    }

    public static List<Consultant> list() throws SQLException {
        return Tx.execute(conn -> {
            ResultSet rs = Tx.prep(conn, scriptFolder + "list").executeQuery();
            List<Consultant> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Consultant(rs));
            }
            return list;
        });
    }

    public void update(@NotNull String title, @NotNull String surname) throws SQLException {
        Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "update");
            prep.setString(1, title);
            prep.setString(2, surname);
            prep.setLong(3, id);
            return prep.execute();
        });

        this.title = title;
        this.surname = surname;
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
    public String getTitle() {
        return title;
    }

    @NotNull
    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return getTitle() + " " + getSurname();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Consultant that = (Consultant) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
