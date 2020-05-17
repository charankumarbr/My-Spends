package in.phoenix.myspends.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import in.phoenix.myspends.components.AppScope;
import in.phoenix.myspends.components.ApplicationContext;
import in.phoenix.myspends.util.AppPref;

@Module(includes = ContextModule.class)
public class AppPrefModule {

    @AppScope
    @Provides
    public AppPref providesAppPref(@ApplicationContext Context context) {
        return new AppPref(context);
    }

}
