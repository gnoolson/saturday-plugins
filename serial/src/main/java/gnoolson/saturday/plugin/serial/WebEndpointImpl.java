package gnoolson.saturday.plugin.serial;

import gnoolson.saturday_plugin_api.*;
import org.pf4j.Extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Extension
public class WebEndpointImpl implements WebEndpoint {

    private Serial serial;

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
        SerialPlugin serialPlugin = (SerialPlugin) saturdayPlugin;
        this.serial = serialPlugin.getSerial();
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

        List<Info> infoList = serial.getInfo();
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("infoList", infoList);

        return new ThymeleafContent(content, infoMap);
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
