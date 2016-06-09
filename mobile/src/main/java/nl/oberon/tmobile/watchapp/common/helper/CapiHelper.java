package nl.oberon.tmobile.watchapp.common.helper;

import nl.oberon.tmobile.datalayer.model.Bundles;
import nl.oberon.tmobile.watchapp.common.network.CapiService;
import nl.oberon.tmobile.watchapp.common.model.Account;
import nl.oberon.tmobile.watchapp.common.model.EmptyResponse;
import nl.oberon.tmobile.watchapp.common.model.PostpaidBundleStatus;
import nl.oberon.tmobile.watchapp.common.model.PostpaidDataBundleStatus;
import nl.oberon.tmobile.watchapp.common.model.Subscription;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 *  A class used to abstract away the CAPI calls needed.
 *  see https://capi.t-mobile.nl/documentation/api-usage/flow for how the capi works
 *
 *  Every method in this helper returns an observable that makes a serperate capi call except for getBundles,
 *  The methods are hierarchical and depend on one another like a tree. getRootUrl is the root Observable.
 *
 *  for example:
 *      - getPostPaidBundleStatus needs the url from a subscription (getSubscription)
 *      - getSubscription needs the url from an account (getAccount)
 *      - getAccount need the rootUrl of the capi (getRootUrl)
 */
public class CapiHelper {

    private static Observable<String> rootUrlObservable;
    private static Observable<Account> accountObservable;
    private static Observable<Subscription> subscriptionObservable;

    /**
     *  Method that takes the results from getPostPaidBundleStatus and getPostPaidDataBundleStatus
     *  and "zips" it into one observable that omits a Bundles item containing a list of all the bundles emitted.
     *
     * @param capiService
     * @param token
     * @return Observable that emits Bundles
     */
    public static Observable<Bundles> getBundles(CapiService capiService, String token) {
        return Observable.zip(CapiHelper.getPostPaidBundleStatus(capiService, token), CapiHelper.getPostPaidDataBundleStatus(capiService, token), (postpaidBundleStatus, postpaidDataBundleStatus) -> {
            Bundles bundles = new Bundles();
            bundles.getBundles().addAll(postpaidBundleStatus.getBundles());
            bundles.getBundles().addAll(postpaidDataBundleStatus.getBundles());
            return bundles;
        });
    }

    /**
     * get the PostPaidBundleStatus
     *
     * @param capiService
     * @param token
     * @return Observable that emits the PostPaidBundleStatus
     */
    public static Observable<PostpaidBundleStatus> getPostPaidBundleStatus(CapiService capiService, String token) {
        return getSubscription(capiService, token)
                .flatMap(subscription -> capiService.getPostpaidBundle(getAuth(token), subscription.getResource("PostpaidBundleStatus").getUrl()).subscribeOn(Schedulers.io()));
    }

    /**
     * get the PostPaidDataBundleStatus
     *
     * @param capiService
     * @param token
     * @return Observable that emits the PostPaidDataBundleStatus once
     */
    public static Observable<PostpaidDataBundleStatus> getPostPaidDataBundleStatus(CapiService capiService, String token) {
        return getSubscription(capiService, token)
                .flatMap(subscription -> capiService.getPostpaidDataBundle(getAuth(token), subscription.getResource("PostpaidDataBundleStatus").getUrl()).subscribeOn(Schedulers.io()));
    }

    /**
     * Method that flatmaps the getAccount and getSubscription call to one observable that gets cached
     *
     * @param capiService
     * @param token to authenticate for api calls
     * @return Observable that caches and emits an account once per subscriber.
     */
    private static Observable<Subscription> getSubscription(CapiService capiService, String token) {
        if (subscriptionObservable == null){
            subscriptionObservable = getAccount(capiService, token)
                    .flatMap(account ->
                            capiService.getSubscription(getAuth(token), account.getResource("Subscription").getUrl(), new String[]{"PostpaidBundlestatus", "PostpaidDataBundlestatus"})
                                    .subscribeOn(Schedulers.io()))
                    .flatMap(subscription -> Observable.create((Observable.OnSubscribe<Subscription>) subscriber -> {//Create a new observable to check wether the user is a postpaid user
                        if (subscription.getCustomerNumber() == null){
                            //if there is no customer number, the user is not a postpaid customer
                            subscriber.onError(new Throwable("This application is only meant for postpaid customers."));
                        } else {
                            subscriber.onNext(subscription);
                            subscriber.onCompleted();
                        }
                    }))

                    .cache();
        }
        return subscriptionObservable;
    }

    /**
     *  Method that flatmaps the getRootUrl and getAccount call to one observable that gets cached
     *
     * @param capiService
     * @param token to authenticate for api calls
     * @return Observable that caches and emits an account once per subscriber.
     */
    private static Observable<Account> getAccount(CapiService capiService, String token) {
        if (accountObservable == null){
            accountObservable = getRootUrl(capiService)
                    .flatMap(url ->
                            capiService.getAccount(getAuth(token), url, new String[]{"Subscription"})
                                    .subscribeOn(Schedulers.io())
                    ).cache();
        }
        return accountObservable;
    }

    /**
     * gets the rootUrl from the response of a request to the base url capi.t-mobile.nl
     *
     * @param capiService
     * @return Observable that caches and emits the rooturl once per subscriber.
     */
    private static Observable<String> getRootUrl(CapiService capiService) {
        if (rootUrlObservable == null){
            rootUrlObservable = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {

                    capiService.getRoot().enqueue(new Callback<EmptyResponse>() {
                        @Override
                        public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
                            subscriber.onNext(extractRootUrl(response.headers().get("Link")));
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onFailure(Call<EmptyResponse> call, Throwable t) {
                            subscriber.onError(t);
                        }
                    });

                }
            }).cache();
        }
        return rootUrlObservable;
    }

    /**
     * method to flush cache and remove existing observables.
     * Needed for when a user logs out.
     */
    public static void flushCache(){
        rootUrlObservable = null;
        accountObservable = null;
        subscriptionObservable = null;
    }

    /**
     * Extract root url from Capi header
     * @param link
     * @return a String containing url
     */
    private static String extractRootUrl(String link) {
        return link.substring(link.indexOf('<') + 1, link.indexOf('>'));
    }

    /**
     *
     * @param token
     * @return a String to be used for an Authorization header
     */
    private static String getAuth(String token) {
        return "Bearer " + token;
    }
}