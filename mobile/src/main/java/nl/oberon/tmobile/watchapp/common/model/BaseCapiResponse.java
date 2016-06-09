package nl.oberon.tmobile.watchapp.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BaseCapiResponse {

    @SerializedName("Resources") List<Resource> resources = new ArrayList<>();

    public Resource getResource(String label) {
        for (Resource resource : resources) {
            if (resource.getLabel().equalsIgnoreCase(label)) return resource;
        }
        return null;
    }
}
