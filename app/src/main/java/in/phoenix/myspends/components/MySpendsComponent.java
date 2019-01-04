package in.phoenix.myspends.components;

import dagger.Component;
import in.phoenix.myspends.modules.AppPrefModule;
import in.phoenix.myspends.util.AppPref;

@AppScope
@Component(modules = {AppPrefModule.class})
public interface MySpendsComponent {

    AppPref getAppPref();

}
