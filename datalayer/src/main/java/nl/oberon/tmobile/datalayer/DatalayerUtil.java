package nl.oberon.tmobile.datalayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 *  A datalayer utility class for working with wear devices.
 */
public class DatalayerUtil {

    /**
     *  Method to send a message to all other nodes.
     *
     * @param googleApiClient a connected googleApiClient
     * @param path path to send message to
     * @param data payload to send
     * @return Observable that emits whether the message was sent
     */
    public static Observable<Boolean> getSendMessage(final GoogleApiClient googleApiClient, final String path, final byte[] data) {
        return NodeUtil.getRemoteNodeId(googleApiClient)
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final String nodeId) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(final Subscriber<? super Boolean> subscriber) {
                                Wearable.MessageApi.sendMessage(googleApiClient, nodeId, path, data).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        subscriber.onNext(sendMessageResult.getStatus().isSuccess());
                                        subscriber.onCompleted();
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     * Method that puts String data into the Wearable.DataApi layer.
     *
     * @param context
     * @param key key value of the data
     * @param data the data as string
     * @param path the path to store
     * @return Obsservable that emits the result
     */
    public static Observable<DataApi.DataItemResult> putData(Context context, final String key, final String data, final String path) {
        return getGoogleApiClient(context)
                .flatMap(new Func1<GoogleApiClient, Observable<DataApi.DataItemResult>>() {
                    @Override
                    public Observable<DataApi.DataItemResult> call(final GoogleApiClient googleApiClient) {
                        return Observable.create(new Observable.OnSubscribe<DataApi.DataItemResult>() {
                            @Override
                            public void call(final Subscriber<? super DataApi.DataItemResult> subscriber) {
                                PutDataMapRequest putDataMapReq = PutDataMapRequest.create(path);
                                putDataMapReq.getDataMap().putString(key, data);
                                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
                                Wearable.DataApi.putDataItem(googleApiClient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                    @Override
                                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                                        subscriber.onNext(dataItemResult);
                                        subscriber.onCompleted();
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     * An Observable that emits whenever data changes in the wear datalayer
     *
     * @param context
     * @return Observable that emits DataEventBuffer
     */
    public static Observable<DataEventBuffer> getDataEvents(Context context) {
        return getGoogleApiClient(context)
                .flatMap(new Func1<GoogleApiClient, Observable<DataEventBuffer>>() {
                    @Override
                    public Observable<DataEventBuffer> call(final GoogleApiClient googleApiClient) {
                        return Observable.create(new Observable.OnSubscribe<DataEventBuffer>() {
                            @Override
                            public void call(final Subscriber<? super DataEventBuffer> subscriber) {
                                Wearable.DataApi.addListener(googleApiClient, new DataApi.DataListener() {
                                    @Override
                                    public void onDataChanged(DataEventBuffer dataEventBuffer) {
                                        subscriber.onNext(dataEventBuffer);
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     *  Observable that creates and connects the googleapiclient and emits it.
     *
     * @param context
     * @return Observable that emits a connected googleapiclient once
     */
    public static Observable<GoogleApiClient> getGoogleApiClient(final Context context) {
        return Observable.create(new Observable.OnSubscribe<GoogleApiClient>() {
            @Override
            public void call(final Subscriber<? super GoogleApiClient> subscriber) {

                final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                        //Only need Wearable API for datalayer
                        .addApi(Wearable.API)
                        .build();

                mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        subscriber.onNext(mGoogleApiClient);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        subscriber.onError(new Throwable("GoogleApiClient: connection suspended"));
                    }
                });

                mGoogleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        switch (connectionResult.getErrorCode()) {
                            case ConnectionResult.API_UNAVAILABLE:
                                subscriber.onError(new Throwable(context.getString(R.string.no_wear_api)));
                                break;
                            default:
                                subscriber.onError(new Throwable(connectionResult.toString()));
                        }
                    }
                });

                mGoogleApiClient.connect();
            }
        });
    }

    /**
     * @param nodeId
     * @param path
     * @return a URI to store data to.
     */
    public static Uri getUriForDataItem(String nodeId, String path) {
        return new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).authority(nodeId).path(path).build();
    }

}
