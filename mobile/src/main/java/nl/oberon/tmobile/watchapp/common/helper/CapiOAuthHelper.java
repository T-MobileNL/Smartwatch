package nl.oberon.tmobile.watchapp.common.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import nl.oberon.tmobile.watchapp.R;
import nl.oberon.tmobile.watchapp.common.network.CapiService;
import okhttp3.Credentials;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;

/**
 * Helper class for fetching a Capi token
 */
public class CapiOAuthHelper {

    /**
     * @param context
     * @return the url that the user should be redirected to to login
     */
    public static String getLoginUrl(Context context) {
        return context.getString(R.string.oauth_host) + String.format(context.getString(R.string.oauth_permissions), context.getString(R.string.oauth_clientkey));
     }

    /**
     *  fetches the access token using the access code in the url.
     *  the url must be the url redirected from the login site,
     *  and must contain a query parameter "code".
     *
     *  calls onError on the subscriber if the url is not valid or an error occured when fetching the token.
     *
     * @param context
     * @param url
     * @param capiService
     * @return Observable that emits the oauth access token.
     */
    public static Observable<String> fetchCapiToken(Context context, String url, CapiService capiService) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Uri uri = Uri.parse(url);

                if (uri.getQueryParameter("error") != null) {
                    String error = uri.getQueryParameter("error");
                    String error_reason = uri.getQueryParameter("error_reason");
                    String error_description = uri.getQueryParameter("error_description");
                    subscriber.onError(new Throwable(String.format("error: %s because %s, \n%s", error, error_reason, error_description)));
                    return;
                }

                String auth = Credentials.basic(context.getString(R.string.oauth_clientkey), "");

                capiService.getAccessToken(auth, "authorization_code", uri.getQueryParameter("code"), url)
                        .subscribe(
                                oAuthToken -> {
                                    subscriber.onNext(oAuthToken.getToken());
                                    subscriber.onCompleted();
                                },
                                subscriber::onError
                        );
            }
        });
    }

}
