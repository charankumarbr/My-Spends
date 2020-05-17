package in.phoenix.myspends.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import in.phoenix.myspends.components.AppScope;
import in.phoenix.myspends.components.ApplicationContext;

@Module
public class ContextModule {

    private Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @ApplicationContext
    @AppScope
    @Provides
    public Context providesContext() {
        return mContext.getApplicationContext();
    }
}
