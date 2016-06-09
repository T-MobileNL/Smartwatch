package nl.oberon.tmobile.watchapp.common.network;

import nl.oberon.tmobile.watchapp.common.model.Account;
import nl.oberon.tmobile.watchapp.common.model.EmptyResponse;
import nl.oberon.tmobile.watchapp.common.model.OAuthToken;
import nl.oberon.tmobile.watchapp.common.model.PostpaidBundleStatus;
import nl.oberon.tmobile.watchapp.common.model.PostpaidDataBundleStatus;
import nl.oberon.tmobile.watchapp.common.model.Subscription;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Capi Service using retrofit all methods returns an observable
 */
public interface CapiService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8"
    })
    @FormUrlEncoded
    @POST("/oauth2/token")
    Observable<OAuthToken> getAccessToken(@Header("Authorization") String auth, @Field("grant_type") String type , @Field("code") String code, @Field("redirect_uri") String redirectUri);

    @Headers({"Accept: application/json"})
    @GET("/")
    Call<EmptyResponse> getRoot();

    @Headers({"Accept: application/json,application/vnd.capi.tmobile.nl.account.V1+json"})
    @GET
    Observable<Account> getAccount(@Header("Authorization") String auth, @Url String url, @Query("resourcelabel") String[] labels);

    @Headers({"Accept: application/json,application/vnd.capi.tmobile.nl.subscription.V1+json"})
    @GET
    Observable<Subscription> getSubscription(@Header("Authorization") String auth, @Url String url, @Query("resourcelabel") String[] labels);

    @Headers({"Accept: application/json,application/vnd.capi.tmobile.nl.postpaiddatabundlestatus.V4+json"})
    @GET
    Observable<PostpaidDataBundleStatus> getPostpaidDataBundle(@Header("Authorization") String auth, @Url String url);

    @Headers({"Accept: application/json,application/vnd.capi.tmobile.nl.postpaidbundlestatus.V2+json"})
    @GET
    Observable<PostpaidBundleStatus> getPostpaidBundle(@Header("Authorization") String auth, @Url String url);

}
