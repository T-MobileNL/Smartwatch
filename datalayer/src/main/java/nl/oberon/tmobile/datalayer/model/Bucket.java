package nl.oberon.tmobile.datalayer.model;

import com.google.gson.annotations.SerializedName;

/**
 * A bucket model defined by the T-Mobile capi
 * example capi doc: https://capi.t-mobile.nl/documentation/resources/postpaiddatabundlestatus
 * this model uses GSON annotations for serialization and deserialization
 */
public class Bucket {

    @SerializedName("Type") private String type;
    @SerializedName("LimitValue") private int limitValue;
    @SerializedName("LimitValuePresentation") private String limitValuePresentation;
    @SerializedName("UsedValue") private int usedValue;
    @SerializedName("UsedValuePresentation") private String usedValuePresentation;
    @SerializedName("RemainingValue") private int remainingValue;
    @SerializedName("RemainingValuePresentation") private String remainingValuePresentation;
    @SerializedName("SortOrder") private int sortOrder;
    @SerializedName("Unit") private String unit;

    /**
     * @return bucket type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The upper bound limit of units that can be used from the bundle.
     */
    public int getLimitValue() {
        return limitValue;
    }

    /**
     * @return Presentable form of LimitValue. This value could also be text when the bundle is unlimited.
     */
    public String getLimitValuePresentation() {
        return limitValuePresentation;
    }

    /**
     * @return The value of units used from the bundle.
     */
    public int getUsedValue() {
        return usedValue;
    }

    /**
     * @return Presentable form of UsedValue
     */
    public String getUsedValuePresentation() {
        return usedValuePresentation;
    }

    /**
     * @return The remaining part of units of the bundle.
     */
    public int getRemainingValue() {
        return remainingValue;
    }

    /**
     * @return The presentable form of RemainingValue.
     */
    public String getRemainingValuePresentation() {
        return remainingValuePresentation;
    }

    /**
     * @return The order in which the buckets should be displayed
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * @return Type of unit. usually KB for data
     */
    public String getUnit() {
        return unit;
    }
}
