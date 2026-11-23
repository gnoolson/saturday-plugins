package gnoolson.saturday.plugin.event_bus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class EventInfo {

    private final String eventName;
    private final Set<String> scriptIds;

}
