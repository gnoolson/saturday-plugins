package gnoolson.saturday.plugin.locker;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final Locker locker;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return locker;
    }

}
