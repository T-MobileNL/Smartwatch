package nl.oberon.tmobile.watchapp.screen.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import nl.oberon.tmobile.datalayer.WearDataHelper;
import nl.oberon.tmobile.watchapp.R;
import nl.oberon.tmobile.watchapp.common.activity.BaseActivity;
import nl.oberon.tmobile.watchapp.common.helper.CapiHelper;
import nl.oberon.tmobile.watchapp.common.helper.CapiOAuthHelper;
import nl.oberon.tmobile.watchapp.common.helper.DialogHelper;
import nl.oberon.tmobile.watchapp.common.helper.PreferenceHelper;
import nl.oberon.tmobile.watchapp.common.util.IntentUtil;
import nl.oberon.tmobile.watchapp.databinding.ActivityMainBinding;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The main and only activity of the app
 *  This activity displays wether the user should log in or is already logged in.
 *
 *  when this activity is launched by the scheme defined in the manifest
 *  it uses the url to fetch a Capi token from the capi and saves it.
 *
 *  if the user is logged in and has never synced data to the wear layer it syncs it.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private Subscription subscribe;
    private boolean isFetchingToken = false;
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if the activity is opened through the scheme intent filter, fetch the token
        if (getIntent() != null && getString(R.string.app_scheme).equalsIgnoreCase(getIntent().getScheme())) {
            ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.authenticating_title), getString(R.string.authenticating_text), false, false);

            isFetchingToken = true;
            CapiOAuthHelper.fetchCapiToken(this, getIntent().getDataString(), getCapiService())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(token -> {
                        isFetchingToken = false;
                        progressDialog.dismiss();
                        PreferenceHelper.saveToken(this, token);
                        syncDataIfFirstTime();
                    }, t -> {
                        isFetchingToken = false;
                        progressDialog.dismiss();
                        DialogHelper.displayEror(this, t);
                    });
        }

        getBinding().loginButton.setOnClickListener(v -> showLogin());
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncDataIfFirstTime();
    }

    /**
     * Syncs the data if it is the
     */
    private void syncDataIfFirstTime() {
        String token = PreferenceHelper.getToken(this);
        getBinding().setIsLoggedIn(token != null);
        if (token == null || PreferenceHelper.hasSynced(this) || isFetchingToken) return;

        if (progressDialog != null) progressDialog.cancel();
        progressDialog = ProgressDialog.show(this, null, getString(R.string.syncing_data));

        subscribe = CapiHelper.getBundles(getCapiService(), token)
                .flatMap(bundles -> WearDataHelper.syncBundles(this, bundles))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dataItemResult -> {
                            PreferenceHelper.setHasSynced(MainActivity.this, true);
                            progressDialog.dismiss();
                        }, throwable -> {
                            progressDialog.dismiss();
                            DialogHelper.displayEror(MainActivity.this, throwable);
                        }
                );
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

    /**
     * Method to show the browser with the log in url
     *
     * logs the user out by deleting preferences when already logged in.
     */
    private void showLogin() {
        if (getBinding().getIsLoggedIn()) {
            //if a user is already logged in reset preferences
            PreferenceHelper.resetPreference(this);
            CapiHelper.flushCache();
        }
        IntentUtil.openUrl(this, CapiOAuthHelper.getLoginUrl(this));
    }
}