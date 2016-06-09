package nl.oberon.tmobile.datalayer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import rx.Observable;
import rx.Subscriber;

/**
 * Utility Class for getting NodeIds
 */
public class NodeUtil{

    /**
     * Method to get the local node Id
     *
     * @param googleApiClient
     * @return Observable that emits the local node Id once
     */
    public static Observable<String> getLocalNodeId(final GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Wearable.NodeApi.getLocalNode(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        subscriber.onNext(getLocalNodeResult.getNode().getId());
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    /**
     * Method to get all the remote node Ids
     *
     * @param googleApiClient
     * @return Observable that emits every remotenodeId
     */
    public static Observable<String> getRemoteNodeId(final GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        for (Node node : getConnectedNodesResult.getNodes()) {
                            subscriber.onNext(node.getId());
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}
