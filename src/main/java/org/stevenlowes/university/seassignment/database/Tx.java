package org.stevenlowes.university.seassignment.database;


import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A class for simplifying running queries on a database.
 * <p>
 * By running queries in a transaction, we ensure that when something goes wrong everything gets rolled back cleanly.
 *
 * @param <T> What you want to return from the database. Use Void (capital V) or boolean for update/delete.
 */
public class Tx<T> {
    @NotNull
    private final Connection connection;
    @NotNull
    private final Transaction<T> transaction;

    private Tx(@NotNull Transaction<T> transaction) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://mysql.dur.ac.uk:3306/Pxvhn44_hospital?allowMultiQueries=true", "xvhn44", "boston93");
        connection.setAutoCommit(false);
        this.transaction = transaction;
    }

    /**
     * WARNING {@link Tx}
     * <p>
     * Supply some code to run in a transaction. Handles creating a connection, committing and rolling back the transaction (if necessary), closing the connection, and protects the database from any
     * errors.
     * <p>
     * Look out for <b>TxHandler Warnings</b>. These are in the documentation to warn you that a method uses a transaction. Do not call these methods from within a {@link Transaction}. The method will
     * not be safe to run and will not be cleanly rolled back in the case of an error.
     *
     * @param transaction The code to run. The output of this is the return value of this method.
     * @param <T>         The type that is returned from the database. Use Void (capital V) to return nothing.
     *
     * @return The value returned by transaction
     *
     * @throws SQLException An error was thrown when running the transaction.
     */
    @NotNull
    public static <T> T execute(Transaction<T> transaction) throws SQLException {
        return new Tx<>(transaction).fire();
    }

    /**
     * Read a script from the resources folder.
     *
     * @param names The names of the scripts, no .sql or /sql or whatever. File must end in sql.
     *
     * @return The text in the script
     */
    @NotNull
    public static String readScript(String... names) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        Arrays.stream(names).map(name -> {
            try {
                InputStream res = Tx.class.getResourceAsStream("/sql/" + name + ".sql");
                return IOUtils.toString(res, Charset.defaultCharset());
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            catch (NullPointerException e) {
                System.out.println("Failed to read script with name " + name);
                e.printStackTrace();
                throw e;
            }
        }).filter(Objects::nonNull).forEachOrdered(sj::add);
        return sj.toString();
    }

    /**
     * Parse all rows from result set and combine into a list.
     *
     * @param rs        The resultSet to parse.
     * @param rowParser A transaction which parses one row of the resultSet and returns the object represented in that row. Null values are filtered from the list. This transaction should not call
     *                  rs.next().
     * @param <T>       The type of object represented in each row of the RS.
     *
     * @return The list of all objects represented by the RS.
     *
     * @throws SQLException Something went wrong with your row parser code when retrieving values from the RS.
     */
    @NotNull
    public static <T> List<T> parseRS(ResultSet rs, RowParser<T> rowParser) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            T elem = rowParser.get(rs);
            if (elem != null) {
                list.add(elem);
            }
        }
        return list;
    }

    public static PreparedStatement prep(Connection connection, String... scriptPaths) throws SQLException {
        String script = Tx.readScript(scriptPaths);
        return connection.prepareStatement(script, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Run the transaction. Handle committing the transaction / rolling it back, and closing the connection.
     *
     * @return The value returned by the transaction supplied to execute
     *
     * @throws SQLException transaction supplied to execute threw an error.
     */
    @NotNull
    private T fire() throws SQLException {
        try {
            T ret = transaction.apply(connection);
            connection.commit();
            return ret;
        }
        catch (Exception ex) {
            connection.rollback();
            throw new SQLException("Error when executing transaction", ex);
        }
        finally {
            connection.close();
        }
    }

    /**
     * It's basically just a {@code Function<Connection, T>} that allows for throwing an {@link SQLException}
     *
     * @param <T> The type that is returned by the transaction. Can't be null, so use boolean and return true if you must.
     */
    public interface Transaction<T> {
        @NotNull
        T apply(@NotNull Connection connection) throws SQLException;
    }

    /**
     * Basically just a supplier that throws SQLException.
     */
    public interface RowParser<T> {
        @Nullable
        T get(ResultSet resultSet) throws SQLException;
    }
}