package gnoolson.saturday.plugin.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Connection {

    private final java.sql.Connection javaSqlConnection;

    /*
     *
     *
     * */
    public void commit() {
        try {
            if (this.javaSqlConnection.isClosed())
                throw new IllegalStateException("Connection is closed"); // +

            this.javaSqlConnection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage()); // +
        }
    }

    public void rollback() {
        try {
            if (this.javaSqlConnection.isClosed())
                throw new IllegalStateException("Connection is closed"); // +

            this.javaSqlConnection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage()); // +
        }
    }

    public void close() {
        try {
            if (this.javaSqlConnection.isClosed())
                return;

            this.javaSqlConnection.rollback();
            this.javaSqlConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());// +
        }
    }

    public int update(SQL sql) {
        try {
            if (this.javaSqlConnection.isClosed())
                throw new IllegalStateException("Connection is closed");// +

            try (Statement stmt = javaSqlConnection.createStatement()) {
                return stmt.executeUpdate(sql.getValue());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());// +
        }
    }

    public List<Map<String, String>> query(SQL sql) {
        try {
            if (this.javaSqlConnection.isClosed())
                throw new IllegalStateException("Connection is closed"); // +

            try (Statement stmt = javaSqlConnection.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(sql.getValue());
                ResultSetMetaData meta = resultSet.getMetaData();
                int colCount = meta.getColumnCount();

                List<Map<String, String>> result = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        String key = meta.getColumnName(i);
                        String value = resultSet.getString(i);
                        row.put(key, value);
                    }
                    result.add(row);
                }

                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());// +
        }
    }

}
