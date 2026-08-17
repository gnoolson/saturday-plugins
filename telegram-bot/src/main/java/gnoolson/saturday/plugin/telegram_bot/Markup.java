package gnoolson.saturday.plugin.telegram_bot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Markup {

    private final List<List<Button>> keyboard;

    /*
     *
     *
     * */
    public Markup() {
        this.keyboard = new ArrayList<>(0);
    }

    public boolean isEmpty() {
        return this.keyboard.isEmpty();
    }

}
