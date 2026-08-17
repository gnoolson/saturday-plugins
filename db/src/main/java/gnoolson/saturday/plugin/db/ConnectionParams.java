package gnoolson.saturday.plugin.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class ConnectionParams {

    private final String driverPath;
    private final String driverClassName;
    private final String jdbcUrl;
    private final String username;
    private final String password;

}
