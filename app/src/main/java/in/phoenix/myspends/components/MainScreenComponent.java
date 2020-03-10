package in.phoenix.myspends.components;

import dagger.Component;
import in.phoenix.myspends.modules.MainScreenModule;
import in.phoenix.myspends.ui.activity.AppSetupActivity;
import in.phoenix.myspends.ui.activity.MainActivity;

/**
 * Author: Charan Kumar
 * Date: 2019-05-03
 */
@Component(modules = {MainScreenModule.class})
public interface MainScreenComponent {

    void inject(MainActivity mainActivity);

}
