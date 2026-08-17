package gnoolson.saturday.plugin.db;

import gnoolson.saturday_plugin_api.Output;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.*;

public class DBImpl implements DB {

    private final Map<String, DataSource> map = new HashMap<>(); // id : data_source_wrapper

    /*
     *
     *
     * */
    @Override
    public synchronized DataSource getOrCreate(String id, String path) {
        return map.computeIfAbsent(id, (dataSourceId) -> {
            ConnectionParams connectionParams = getFromProperties(path);
            return createDataSourceWrapper(connectionParams, id);
        });
    }

    @Override
    public synchronized void destroy(String id) {
        try {
            DataSource dataSourceWrapper = map.remove(id);
            if (dataSourceWrapper != null) {
                BasicDataSource dataSource = dataSourceWrapper.getDataSource();
                if (!dataSource.isClosed())
                    dataSource.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);// +
        }
    }

    @Override
    public synchronized void shutdown() {
        for (DataSource dataSource : map.values()) {
            destroy(dataSource.getId());
        }
    }

    @Override
    public synchronized List<DataSourceInfo> getInfo() {
        List<DataSourceInfo> result = new ArrayList<>(1);

        for (DataSource value : map.values()) {
            result.add(new DataSourceInfo(
                    value.getId(),
                    value.getConnectionParams().getDriverPath(),
                    value.getConnectionParams().getDriverClassName(),
                    value.getConnectionParams().getJdbcUrl(),
                    value.getConnectionParams().getUsername(),
                    value.getActiveConnections(),
                    value.getIdleConnections()
            ));
        }

        return result;
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

    /*
     *
     *
     *
     * */
    private DataSource createDataSourceWrapper(ConnectionParams connectionParams, String id) {
        try {
            DataSource dataSourceWrapper;

            File file = ResourceUtils.getFile(connectionParams.getDriverPath());
            URL[] urls = {file.toURI().toURL()};
            URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
            Class<?> driverClass = Class.forName(connectionParams.getDriverClassName(), true, loader);
            java.sql.Driver targetDriver = (java.sql.Driver) driverClass.newInstance();
            java.sql.DriverManager.registerDriver(new DriverShim(targetDriver));

            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassLoader(loader);
            dataSource.setDriverClassName(connectionParams.getDriverClassName());
            dataSource.setUrl(connectionParams.getJdbcUrl());
            dataSource.setUsername(connectionParams.getUsername());
            dataSource.setPassword(connectionParams.getPassword());

            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTimeBetweenEvictionRunsMillis(30000);
            dataSource.setRemoveAbandoned(true);
            dataSource.setRemoveAbandonedTimeout(60);

            java.sql.Connection testConn = dataSource.getConnection();
            testConn.close();

            dataSourceWrapper = new DataSource(id, dataSource, connectionParams);
            return dataSourceWrapper;
        } catch (Exception e) {
            throw new RuntimeException(e);// +
        }
    }

    private ConnectionParams getFromProperties(String path) {
        try {
            Properties prop = new Properties();
            File file = ResourceUtils.getFile(path);

            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            }

            return new ConnectionParams(
                    prop.getProperty("db.driver_path"),
                    prop.getProperty("db.driver_class_name"),
                    prop.getProperty("db.jdbc_url"),
                    prop.getProperty("db.username"),
                    prop.getProperty("db.password")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);// +
        }
    }

    private static class DriverShim implements java.sql.Driver {
        private final java.sql.Driver delegate;

        public DriverShim(java.sql.Driver delegate) {
            this.delegate = delegate;
        }

        public boolean acceptsURL(String url) throws java.sql.SQLException {
            return delegate.acceptsURL(url);
        }

        public java.sql.Connection connect(String url, java.util.Properties info) throws java.sql.SQLException {
            return delegate.connect(url, info);
        }

        public int getMajorVersion() {
            return delegate.getMajorVersion();
        }

        public int getMinorVersion() {
            return delegate.getMinorVersion();
        }

        public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws java.sql.SQLException {
            return delegate.getPropertyInfo(url, info);
        }

        public boolean jdbcCompliant() {
            return delegate.jdbcCompliant();
        }

        public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
            return delegate.getParentLogger();
        }
    }

}
