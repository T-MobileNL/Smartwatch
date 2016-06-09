package nl.oberon.tmobile.datalayer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A bundle model defined by the T-Mobile capi
 *  example capi doc: https://capi.t-mobile.nl/documentation/resources/postpaiddatabundlestatus
 *  this model uses GSON annotations for serialization and deserialization
 */
public class Bundle {

    @SerializedName("Type") private String type;
    @SerializedName("Name") private String name;
    @SerializedName("StartDate") private String startDate;
    @SerializedName("ValidUntilDate") private String validUntilDate;
    @SerializedName("SortOrder") private int sortOrder;
    @SerializedName("FreeUnits") private String freeUnits;
    @SerializedName("FreeUnitsPresentation") private String freeUnitsPresentation;
    @SerializedName("Unit") private String unit;
    @SerializedName("MaxUnits") private String maxUnits;
    @SerializedName("MaxUnitsPresentation") private String maxUnitPresentation;
    @SerializedName("UsageAmount") private String usageAmount;
    @SerializedName("UsageAmountPresentation") private String usageAmountPresentation;
    @SerializedName("Buckets") private List<Bucket> buckets;

    /**
     * @return Type of bundle
     */
    public String getType() {
        return type;
    }

    /**
     * @return The description of the bundle
     */
    public String getName() {
        return name;
    }

    /**
     * @return The string of the startdate, this string is not formatted.
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @return The string of the expiry date, this string is not formatted.
     */
    public String getValidUntilDate() {
        return validUntilDate;
    }

    /**
     * @return The order in which the bundles/offers should be displayed.
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * @return Number of units.
     */
    public String getFreeUnits() {
        return freeUnits;
    }

    /**
     * @return Presentable form of free units.
     */
    public String getFreeUnitsPresentation() {
        return freeUnitsPresentation;
    }

    /**
     * @return Type of unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @return Maximum number of units.
     */
    public String getMaxUnits() {
        return maxUnits;
    }

    /**
     * @return Presentable form of max units.
     */
    public String getMaxUnitPresentation() {
        return maxUnitPresentation;
    }

    /**
     * @return Used amount.
     */
    public String getUsageAmount() {
        return usageAmount;
    }

    /**
     * @return Presentable form of usage amount.
     */
    public String getUsageAmountPresentation() {
        return usageAmountPresentation;
    }

    /**
     * @return The list for all buckets.
     */
    public List<Bucket> getBuckets() {
        return buckets;
    }
}
