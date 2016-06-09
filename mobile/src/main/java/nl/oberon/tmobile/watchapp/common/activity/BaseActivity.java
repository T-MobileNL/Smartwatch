package nl.oberon.tmobile.watchapp.common.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nl.oberon.tmobile.watchapp.common.Application;
import nl.oberon.tmobile.watchapp.common.network.CapiService;

/**
 *
 * A BaseActivity that uses databinding to inflate the view.
 *  Any subclasses need to provide the class of the databinding layout and the layout Id.
 */
public abstract class BaseActivity<D extends ViewDataBinding> extends AppCompatActivity{

    protected D binding;

    /**
     * @return the layout resource to inflate
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutRes());
    }

    /**
     * @return binding of the layout
     */
    public D getBinding() {
        return binding;
    }

    /**
     * @return CapiService for api calls
     */
    public CapiService getCapiService() {
        return ((Application) getApplication()).getCapiService();
    }
}
