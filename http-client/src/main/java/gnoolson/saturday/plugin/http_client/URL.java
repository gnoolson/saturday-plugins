package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class URL {

    private final String value;

    /*
     *
     *
     * */
    public URL(String value) {
        if (value == null)
            throw new IllegalArgumentException("URL value cannot be null"); // +

        this.value = value.trim();
        if (this.value.isEmpty())
            throw new IllegalArgumentException("URL value cannot be empty"); // +
    }

    public static URL of(String value) {
        return new URL(value);
    }

}
