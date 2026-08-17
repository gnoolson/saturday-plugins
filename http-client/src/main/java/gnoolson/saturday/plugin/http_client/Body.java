package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class Body {

    private final String value;

    /*
     *
     *
     * */
    public Body(String value) {
        if (value == null)
            throw new IllegalArgumentException("Body value cannot be null"); // +

        this.value = value;
    }

    public static Body of(String value) {
        return new Body(value);
    }

    public static Body empty() {
        return new Body("");
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

}
