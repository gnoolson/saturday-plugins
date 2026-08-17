package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday_plugin_api.Output;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpClientImpl implements HttpClient {

    public Response send(Request request) {
        try {
            URL url = new URL(request.getUrl().getValue());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(request.getConnectionTimeout() * 1000);

            conn.setRequestMethod(request.getMethod().getValue());
            for (String key : request.getHeaders().keySet()) {
                conn.setRequestProperty(key, request.getHeaders().get(key));
            }

            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(request.getBody().getValue().getBytes());
            }

            int status = conn.getResponseCode();
            InputStream is;

            if (status >= 200 && status < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            String responseBodyStr = IOUtils.toString(is, StandardCharsets.UTF_8);

            Map<String, List<String>> headers = conn.getHeaderFields();

            return new Response(
                    conn.getResponseCode(),
                    responseBodyStr.isEmpty() ? Body.empty() : Body.of(responseBodyStr),
                    headers);

        } catch (Exception e) {
            throw new RuntimeException(e); // +
        }
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

}
