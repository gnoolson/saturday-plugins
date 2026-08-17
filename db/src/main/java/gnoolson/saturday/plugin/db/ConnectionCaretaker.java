package gnoolson.saturday.plugin.db;

import java.util.ArrayList;
import java.util.List;

public class ConnectionCaretaker {

    private final List<Connection> connections = new ArrayList<>(1);

    /*
     *
     *
     * */
    public void closeOpenedConnection() {
        for (Connection connection : connections) {
            connection.close();
        }
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

}
