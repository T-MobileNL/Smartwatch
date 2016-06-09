package nl.oberon.tmobile.datalayer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import nl.oberon.tmobile.datalayer.model.Bundles;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Simplified datalayer for communnicating with wear devices.
 * Makes use of datalayerutil.
 * All methods are static and accessible from anywhere.
 */
public class WearDataHelper {

    private static final String REFRESH_PATH = "/refresh";
    private static final String BUNDLE_KEY = "bundles";
    private static final String DATA_PATH = "/data";

    /**
     * Method to get a observable that emits a Bundles object from the datalayer
     * see {@link nl.oberon.tmobile.datalayer.model.Bundle}
     *
     * @param context
     * @param remote  whether the bundle should be retrieved from the a remote node or local node
     * @return An observable emitting a bundle from the wear DataApi.
     */
    public static Observable<Bundles> getBundle(Context context, final boolean remote) {
        return DatalayerUtil.getGoogleApiClient(context)
                .flatMap(new Func1<GoogleApiClient, Observable<Bundles>>() {
                    @Override
                    public Observable<Bundles> call(final GoogleApiClient googleApiClient) {
                        Observable<String> nodeId = remote ? NodeUtil.getRemoteNodeId(googleApiClient) : NodeUtil.getLocalNodeId(googleApiClient);
                        return nodeId.flatMap(new Func1<String, Observable<Bundles>>() {
                            @Override
                            public Observable<Bundles> call(String nodeId) {
                                return getDataItem(googleApiClient, nodeId);
                            }
                        });
                    }
                });
    }

    /**
     * Syncs Bundles object to the datalayer so and wear connected device can access it.
     *
     * @param context
     * @param bundles
     * @return Observable that emits the result
     */
    public static Observable<DataApi.DataItemResult> syncBundles(Context context, Bundles bundles) {
        return DatalayerUtil.putData(context, BUNDLE_KEY, new Gson().toJson(bundles), DATA_PATH);
    }

    /**
     * Sends a empty message to wear devices with path {@link WearDataHelper#REFRESH_PATH}
     *
     * @param context
     * @return Observable that emits a boolean whether the message has been sent.
     */
    public static Observable<Boolean> sendRefresh(Context context) {
        return DatalayerUtil.getGoogleApiClient(context)
                .flatMap(new Func1<GoogleApiClient, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(GoogleApiClient googleApiClient) {
                        return DatalayerUtil.getSendMessage(googleApiClient, REFRESH_PATH, null);
                    }
                });
    }

    /**
     * This methods gets the string data from the wear DataApi and uses gson to deserialize json to a bundles object.
     *
     * @param googleApiClient a connected googleApiClient
     * @param nodeId          the node to get the data from
     * @return Observable that emits Bundles
     */
    private static Observable<Bundles> getDataItem(final GoogleApiClient googleApiClient, final String nodeId) {
        return Observable.create(new Observable.OnSubscribe<Bundles>() {
            @Override
            public void call(final Subscriber<? super Bundles> subscriber) {
                final PendingResult<DataApi.DataItemResult> dataItem = Wearable.DataApi.getDataItem(googleApiClient, DatalayerUtil.getUriForDataItem(nodeId, DATA_PATH));
                dataItem.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if (dataItemResult.getDataItem() == null) {
                            subscriber.onError(new Throwable("No data"));
                            return;
                        }
                        String string = DataMapItem.fromDataItem(dataItemResult.getDataItem()).getDataMap().getString(BUNDLE_KEY);
                        Bundles bundles = new Gson().fromJson(string, Bundles.class);
                        subscriber.onNext(bundles);
                        subscriber.onCompleted();
                    }
                });
            }
        }).observeOn(Schedulers.io());
    }
}
