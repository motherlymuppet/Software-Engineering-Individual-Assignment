package org.stevenlowes.university.seassignment.dbao;

import org.jetbrains.annotations.NotNull;
import org.stevenlowes.university.seassignment.database.Tx;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RiskCategory {
    @NotNull
    private static final String scriptFolder = "risk_categories/";
    private final long id;
    @NotNull
    private String name;

    private RiskCategory(long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public RiskCategory(@NotNull ResultSet rs) throws SQLException {
        this(rs.getLong("risk_categories.id"), rs.getString("risk_categories.name"));
    }

    public RiskCategory(long id) throws SQLException {
        this(Tx.<ResultSet>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "read");
            prep.setLong(1, id);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs;
        }));
    }

    public RiskCategory(@NotNull String name) throws SQLException {
        this(Tx.<Long>execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "create");
            prep.setString(1, name);
            prep.execute();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);
        }));
    }

    public static List<RiskCategory> list() throws SQLException {
        return Tx.execute(conn -> {
            ResultSet rs = Tx.prep(conn, scriptFolder + "list").executeQuery();
            List<RiskCategory> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new RiskCategory(rs));
            }
            return list;
        });
    }

    public void update(@NotNull String name) throws SQLException {
        Tx.execute(conn -> {
            PreparedStatement prep = Tx.prep(conn, scriptFolder + "update");
            prep.setString(1, name);
            prep.setLong(2, id);
            return prep.execute();
        });

        this.name = name;
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
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RiskCategory that = (RiskCategory) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
