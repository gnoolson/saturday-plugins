package gnoolson.saturday.plugin.serial;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final Serial serial;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return serial;
    }

}
