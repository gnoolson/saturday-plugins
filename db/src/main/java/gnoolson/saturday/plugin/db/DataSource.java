package gnoolson.saturday.plugin.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.SQLException;

public class DataSource {

    private final org.apache.commons.dbcp.BasicDataSource dataSource;
    private final String id;
    private final ConnectionParams connectionParams;

    /*
     *
     *
     * */
    public DataSource(String id, BasicDataSource dataSource, ConnectionParams connectionParams) {
        this.id = id;
        this.dataSource = dataSource;
        this.connectionParams = connectionParams;
    }

    public String getId() {
        return id;
    }

    public int getActiveConnections() {
        return dataSource.getNumActive();
    }

    public int getIdleConnections() {
        return dataSource.getNumIdle();
    }

    public Connection getConnection(TransactionLevel transactionLevel) {
        try {
            java.sql.Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(transactionLevel.getLevel());

            return new Connection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e); // +
        }
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    public ConnectionParams getConnectionParams() {
        return connectionParams;
    }

    /*
     *
     *
     * */
    @Getter
    @RequiredArgsConstructor
    public enum TransactionLevel {
        READ_UNCOMMITTED(1),
        READ_COMMITTED(2),
        REPEATABLE_READ(4),
        SERIALIZABLE(8);

        private final int level;
    }

}
