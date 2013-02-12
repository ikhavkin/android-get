package net.codevolve.aget;

import android.app.Application;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.runners.model.InitializationError;
import roboguice.RoboGuice;
import roboguice.inject.ContextScope;
import roboguice.test.RobolectricRoboTestRunner;

import java.lang.reflect.Method;

public class InjectedTestRunner extends RobolectricRoboTestRunner {
    public InjectedTestRunner(Class<?> testClass) throws
            InitializationError {
        super(testClass);
    }

    @Override
    public void prepareTest(Object test) {
        super.prepareTest(test);

        Application application = Robolectric.application;
        // Override some system components for our tests.
        RoboGuice.setBaseApplicationInjector(application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(application)).with(new TestModule()));

        Injector injector = RoboGuice.getInjector(application);
        ContextScope scope = injector.getInstance(ContextScope.class);
        scope.enter(application);
        injector.injectMembers(test);
    }

    @Override
    public void afterTest(Method method) {
        super.afterTest(method);

        RoboGuice.util.reset();
    }
}
