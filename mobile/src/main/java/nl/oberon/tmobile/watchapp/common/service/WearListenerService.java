package nl.oberon.tmobile.watchapp.common.service;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import nl.oberon.tmobile.datalayer.WearDataHelper;
import nl.oberon.tmobile.watchapp.common.Application;
import nl.oberon.tmobile.watchapp.common.helper.CapiHelper;
import nl.oberon.tmobile.watchapp.common.helper.PreferenceHelper;
import nl.oberon.tmobile.watchapp.common.network.CapiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Service that listens to any message event defined in the manifest.
 *  whenever it receives a message it fetches the bundles from the capi.
 */
public class WearListenerService extends WearableListenerService {

    private static final String TAG = "WearListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        CapiHelper.getBundles(getCapiService(), PreferenceHelper.getToken(this))
                .flatMap(bundles -> WearDataHelper.syncBundles(this, bundles))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dataItemResult -> {
                            Log.d(TAG, "datasynced");
                        }, throwable -> {
                            Log.d(TAG, "data sync failed :" + throwable.getMessage());
                        }
                );
    }

    public CapiService getCapiService() {
        return ((Application) getApplication()).getCapiService();
    }
}
