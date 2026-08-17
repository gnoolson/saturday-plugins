package gnoolson.saturday.plugin.timer;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final Timer timer;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return timer;
    }

}
