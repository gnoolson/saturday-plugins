package gnoolson.saturday.plugin.timer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Info {

    private final UUID id;
    private final String name;
    private final long delay;

}
