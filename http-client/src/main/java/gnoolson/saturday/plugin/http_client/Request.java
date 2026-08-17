package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class Request {

    private final URL url;
    private final Method method;
    private final Map<String, String> headers;
    private final Body body;
    private final int connectionTimeout;

}
