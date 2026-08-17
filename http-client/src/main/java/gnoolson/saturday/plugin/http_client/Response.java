package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Response {

    private final int code;
    private final Body body;
    private final Map<String, List<String>> headers;

}
