package gnoolson.saturday.plugin.cache;

import gnoolson.saturday_plugin_api.*;
import org.pf4j.Extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Extension
public class WebEndpointImpl implements WebEndpoint {

    private Cache cache;

    /*
     *
     *
     * */
    @Override
    public Content getContent(String language, String path, Map<String, String> requestParams) {
        if (path.endsWith("/doc")) {
            return getDocContent(language);
        }

        return getInfoContent(language);
    }

    @Override
    public Map<String, Object> executeAction(Map<String, Object> requestBody) {
        return Collections.emptyMap();
    }

    @Override
    public void setup(SaturdayPlugin saturdayPlugin) {
        CachePlugin cachePlugin = (CachePlugin) saturdayPlugin;
        this.cache = cachePlugin.getCache();
    }

    /*
     *
     *
     * */
    private ThymeleafContent getInfoContent(String language) {
        String content;
        if (language.equals("uk")) {
            content = readContent("/content/info_uk.html");
        } else {
            content = readContent("/content/info_en.html");
        }

        Collection<String> keys = cache.getKeys();

        Map<String, Object> models = new HashMap<>(1);
        models.put("keys", keys);

        return new ThymeleafContent(content, models);
    }

    private MarkdownContent getDocContent(String language) {
        String content;
        if (language.equals("uk")) {
            content = readContent("/content/doc_uk.md");
        } else {
            content = readContent("/content/doc_en.md");
        }

        return new MarkdownContent(content);
    }

    private String readContent(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
