package nl.oberon.tmobile.datalayer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A model to simply hold all a list of bundles.
 */
public class Bundles {

    @SerializedName("bundles") private List<Bundle> bundles = new ArrayList<>();

    /**
     * @return List of bundles
     */
    public List<Bundle> getBundles() {
        return bundles;
    }
}
