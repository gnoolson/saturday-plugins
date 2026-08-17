package gnoolson.saturday.plugin.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataSourceInfo {

    private final String id;
    private final String driverPath;
    private final String driverClassName;
    private final String jdbcUrl;
    private final String username;
    private final int activeConnections;
    private final int idleConnections;

}
