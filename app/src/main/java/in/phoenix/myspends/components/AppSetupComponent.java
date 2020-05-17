package in.phoenix.myspends.components;

import dagger.Component;
import in.phoenix.myspends.modules.AppSetupModule;
import in.phoenix.myspends.ui.activity.AppSetupActivity;

/**
 * Author: Charan Kumar
 * Date: 2019-04-29
 */
@AppSetupScope
@Component(modules = {AppSetupModule.class})
public interface AppSetupComponent {

    void inject(AppSetupActivity appSetupActivity);

}
