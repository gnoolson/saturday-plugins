package gnoolson.saturday.plugin.http_client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class Method {

    private final String value;

    /*
     *
     *
     * */
    public Method(String value) {
        if (value == null)
            throw new IllegalArgumentException("Method value cannot be null"); // +

        this.value = value.trim();
        if (this.value.isEmpty())
            throw new IllegalArgumentException("Method value cannot be empty"); // +
    }

    public static Method of(String value) {
        return new Method(value);
    }

}
