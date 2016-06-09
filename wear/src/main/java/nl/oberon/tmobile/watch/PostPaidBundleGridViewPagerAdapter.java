package nl.oberon.tmobile.watch;

import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.oberon.tmobile.datalayer.model.Bucket;
import nl.oberon.tmobile.datalayer.model.Bundle;
import nl.oberon.tmobile.watchapp.databinding.ScreenDatabundleBinding;

/**
 * Adapter class to display the bundles,
 */
public class PostPaidBundleGridViewPagerAdapter extends GridPagerAdapter {

    private List<BundlePresentation> bundlePresentations = new ArrayList<>();

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return bundlePresentations == null ? 1 : bundlePresentations.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int i, int column) {
        if (bundlePresentations == null) return null;

        ScreenDatabundleBinding binding = ScreenDatabundleBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        binding.setItem(bundlePresentations.get(column));

        View view = binding.getRoot();
        viewGroup.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i1, Object o) {
        viewGroup.removeView((View) o);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    public void setBundles(Context context, List<Bundle> bundles) {
        bundlePresentations.clear();
        //for each bundle or bucket add an bundle presentation object
        for (Bundle bundle : bundles) {
            bundlePresentations.add(new BundlePresentation(context, bundle));
        }
        //sort by sortorder
        Collections.sort(bundlePresentations, new Comparator<BundlePresentation>() {
            @Override
            public int compare(BundlePresentation lhs, BundlePresentation rhs) {
                return rhs.getSortOrder() - lhs.getSortOrder();
            }
        });
    }
}
