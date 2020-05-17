package in.phoenix.myspends.modules;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.phoenix.myspends.components.AppSetupScope;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.ui.activity.AppSetupActivity;

/**
 * Author: Charan Kumar
 * Date: 2019-05-02
 */
@Module
public class AppSetupModule {

    private final AppSetupActivity appSetupActivity;

    private ArrayList<Currency> currencies;

    public AppSetupModule(AppSetupActivity appSetupActivity, ArrayList<Currency> currencies) {
        this.appSetupActivity = appSetupActivity;
        this.currencies = currencies;
    }

    @AppSetupScope
    @Provides
    public AppSetupActivity providesContext() {
        return appSetupActivity;
    }

    @AppSetupScope
    @Provides
    public ArrayList<Currency> providesCurrencies() {
        return currencies;
    }
}
