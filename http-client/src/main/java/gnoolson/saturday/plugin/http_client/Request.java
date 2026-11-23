package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode
@ToString
@Getter
public class Request {

    private final URL url;
    private final Method method;
    private final Map<String, String> headers;
    private final Body body;
    private final int connectionTimeout;

    /*
    *
    *
    * */
    public Request(URL url, Method method, Map<String, String> headers, Body body, int connectionTimeout) {
        if(url == null)
            throw new IllegalArgumentException("\"URL\" value cannot be null");
        if(method == null)
            throw new IllegalArgumentException("\"Method\" value cannot be null");
        if(headers == null)
            throw new IllegalArgumentException("\"Headers\" value cannot be null");
        if(body == null)
            throw new IllegalArgumentException("\"Body\" value cannot be null");

        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.connectionTimeout = connectionTimeout;
    }
}
