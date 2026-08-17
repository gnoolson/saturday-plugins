package gnoolson.saturday.plugin.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class SQL {

    private final String value;

    /*
     *
     *
     * */
    public SQL(String value) {
        if (value == null)
            throw new IllegalArgumentException("SQL value cannot be null"); // +

        this.value = value.trim();
        if (this.value.isEmpty())
            throw new IllegalArgumentException("SQL value cannot be empty"); // +
    }

    public static SQL of(String value) {
        return new SQL(value);
    }

}
