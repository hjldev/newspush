package cn.hjl.newspush;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.greendao.database.Database;

import cn.hjl.newspush.di.component.ApplicationComponent;
import cn.hjl.newspush.di.component.DaggerApplicationComponent;
import cn.hjl.newspush.di.module.ApplicationModule;
import cn.hjl.newspush.mvp.entity.DaoMaster;
import cn.hjl.newspush.mvp.entity.DaoSession;
import cn.hjl.newspush.utils.Log;
import cn.hjl.newspush.utils.MyUtils;

import cn.hjl.newspush.mvp.entity.DaoMaster.DevOpenHelper;

/**
 * Created by a2437 on 2016/9/20.
 */
public class App extends Application {

    private RefWatcher refWatcher;
    private ApplicationComponent mApplicationComponent;
    private DaoSession daoSession;

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }

    private static App instance;

    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.init(BuildConfig.LOG_DEBUG);
        instance = this;
        initLeakCanary();
        initActivityLifecycleLogs();
        initStrictMode();
        initDayNightMode();
        initApplicationComponent();
        setUpDataBase();
    }

    private void setUpDataBase() {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "channel-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private void initApplicationComponent() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }


    /**
     * 初始化内存泄漏监测
     */
    private void initLeakCanary() {
        if (BuildConfig.DEBUG) {
            refWatcher = LeakCanary.install(this);
        } else {
            refWatcher = RefWatcher.DISABLED;
        }
    }

    /**
     * activity的生命周期打印
     */
    private void initActivityLifecycleLogs() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.i("=========", activity + "  onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i("=========", activity + "  onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("=========", activity + "  onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i("=========", activity + "  onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i("=========", activity + "  onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.i("=========", activity + "  onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i("=========", activity + "  onActivityDestroyed");
            }
        });
    }


    /**
     * 初始化严苛模式
     */
    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
//                            .penaltyDialog() // 弹出违规提示对话框
                            .penaltyLog() // 在logcat中打印违规异常信息
                            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());
        }
    }
    private void initDayNightMode() {
        if (MyUtils.isNightMode(getInstance())){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


}
