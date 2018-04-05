package org.stevenlowes.university.seassignment.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains various methods to help with creating, versioning, and upgrading the database.
 */
public class Database {
    private static boolean exists() {
        try {
            return Tx.execute((conn) -> {
                ResultSet rs = conn.prepareStatement(Tx.readScript("exists")).executeQuery();
                return rs.next();
            });
        }
        catch (SQLException e) {
            return false;
        }
    }

    /**
     * Upgrades the database to the newest exists.
     * <p>
     * Does this by checking the upgrade_paths table for the current exists in the start_version column of the table. This row then points to a script which will update the database to a higher
     * exists. This process is repeated until the database is updated to the newest exists.
     *
     * @throws SQLException Error encountered when running upgrade scripts.
     */
    private static void create() throws SQLException {
        Tx.execute(conn -> conn.prepareStatement(Tx.readScript("create")).execute());
    }

    public static void start() throws SQLException {
        if (!exists()) {
            create();
        }
    }

    public static void dropAll() throws SQLException {
        Tx.execute(conn -> conn.prepareStatement("DROP DATABASE Pxvhn44_hospital; CREATE DATABASE Pxvhn44_hospital;").execute());
    }

    public static void main(String[] args) throws SQLException {
        dropAll();
    }
}
