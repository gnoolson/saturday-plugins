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

            Method method = request.getMethod();
            conn.setRequestMethod(method.name());

            for (String key : request.getHeaders().keySet()) {
                conn.setRequestProperty(key, request.getHeaders().get(key));
            }

            boolean useOutput = !request.getBody().isEmpty() && (method == Method.PUT || method == Method.POST || method == Method.DELETE);
            if (useOutput) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = request.getBody().getValue().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            try {
                String responseBodyStr = IOUtils.toString(is, StandardCharsets.UTF_8);
                Map<String, List<String>> headers = conn.getHeaderFields();
                return new Response(
                        status,
                        responseBodyStr.isEmpty() ? Body.empty() : Body.of(responseBodyStr),
                        headers);

            } finally {
                if (is != null)
                    is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

}
