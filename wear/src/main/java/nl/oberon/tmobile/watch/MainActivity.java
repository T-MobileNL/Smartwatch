package nl.oberon.tmobile.watch;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import nl.oberon.tmobile.datalayer.WearDataHelper;
import nl.oberon.tmobile.datalayer.DatalayerUtil;
import nl.oberon.tmobile.datalayer.model.Bundles;
import nl.oberon.tmobile.watchapp.R;
import nl.oberon.tmobile.watchapp.databinding.ActivityMainWatchBinding;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Main activity that displays a viewpager with bundle data.
 * syncs data on every onResume called.
 */
public class MainActivity extends Activity {

    private static final int DEFAULT_OFFSCREEN_PAGE_COUNT = 4;

    private PostPaidBundleGridViewPagerAdapter viewPagerAdapter;
    private ActivityMainWatchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_watch);

        viewPagerAdapter = new PostPaidBundleGridViewPagerAdapter();
        binding.gridView.setOffscreenPageCount(DEFAULT_OFFSCREEN_PAGE_COUNT);
        binding.gridView.setAdapter(viewPagerAdapter);

        binding.pageIndicator.setPager(binding.gridView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.setShowLoader(true);

        //receive updates
        DatalayerUtil.getDataEvents(this)
                .cast(Object.class) //cast to object to ignore the emitted items
                .startWith(new Object()) //start with an empty stub
                .flatMap(new Func1<Object, Observable<Bundles>>() {
                    @Override
                    public Observable<Bundles> call(Object object) {
                        return WearDataHelper.getBundle(MainActivity.this, true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUpdateAction());

        //send a message to receive updates
        WearDataHelper.sendRefresh(this)
                .subscribeOn(Schedulers.io())
                .subscribe(); //ignore results
    }

    private Subscriber<Bundles> createUpdateAction() {
        return new Subscriber<Bundles>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                binding.setShowLoader(false);
                binding.setShowMessage(true);
            }

            @Override
            public void onNext(Bundles bundles) {
                binding.setShowLoader(false);
                if (bundles != null) {
                    viewPagerAdapter.setBundles(MainActivity.this, bundles.getBundles());
                    viewPagerAdapter.notifyDataSetChanged();
                    binding.setShowMessage(false);
                } else {
                    binding.setShowMessage(true);
                }
            }
        };
    }

}
