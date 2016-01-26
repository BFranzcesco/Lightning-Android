package francescoboschini.com.lightning;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());
    }
}
