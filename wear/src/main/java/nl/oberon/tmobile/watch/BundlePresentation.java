package nl.oberon.tmobile.watch;

import android.content.Context;

import java.util.List;

import nl.oberon.tmobile.datalayer.model.Bucket;
import nl.oberon.tmobile.datalayer.model.Bundle;
import nl.oberon.tmobile.watchapp.BuildConfig;
import nl.oberon.tmobile.watchapp.R;

/**
 * A presentation class to present bundles and buckets in the view.
 */
public class BundlePresentation {

    private static final double PERCENTAGE_RED = 0.2; // use red if the remaining value is less than 20%
    private static final double PERCENTAGE_ORANGE = 0.5;//use orange if less than half is remaining
    private static final String UNIT_KB = "KB";
    private static final String UNIT_MB = "MB";
    private static final long PRETTY_UNLIMITED = 900000000;
    private int color;
    private String title;
    private String value;
    private String unit = "";
    private int sortOrder;

    public BundlePresentation(Context context, Bundle bundle) {
        title = bundle.getName();
        sortOrder = bundle.getSortOrder();
        color = context.getResources().getColor(R.color.green);

        if (bundle.getBuckets() != null && !bundle.getBuckets().isEmpty()){
            setValueByBuckets(context, bundle.getBuckets());
        } else {
            setValueByBundle(context, bundle);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public int getValueColor() {
        return color;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the respresentation based on the buckets
     *
     * @param context
     * @param buckets
     */
    private void setValueByBuckets(Context context, List<Bucket> buckets) {
        long remaining = 0, limit=0;
        for (Bucket bucket : buckets) {
            remaining += bucket.getRemainingValue();

            if (bucket.getLimitValue() > limit) { //gets the highest bucket limit.
                limit = bucket.getLimitValue();
            }
        }

        //if the users limit is over 900GB it is pretty much unlimited... therefor we show unlimited
        if (limit > PRETTY_UNLIMITED && UNIT_KB.equalsIgnoreCase(buckets.get(0).getUnit())) {
            setUnlimited(context);
            return;
        }

        if(buckets.size() == 1){//if size is exactly one use that representation
            setValueByPresentation(buckets.get(0).getRemainingValuePresentation());
        } else if (UNIT_KB.equalsIgnoreCase(buckets.get(0).getUnit())){ //else if the units are kb show it in mb
            unit = UNIT_MB;
            value = ""+(remaining/1024);
        } else { //just show it.
            unit = buckets.get(0).getUnit();
            value = ""+remaining;
        }
        setValueColor(context, remaining, limit);
    }

    /**
     * sets the representation for unlimited data bundles
     * @param context
     */
    private void setUnlimited(Context context) {
        unit = UNIT_MB;
        title = context.getString(nl.oberon.tmobile.watchapp.R.string.unlimited_data);
        color = context.getResources().getColor(R.color.tmobile_pink);
        value = "\u221E";
    }

    /**
     * Sets the presentation based on bundle
     * @param context
     * @param bundle
     */
    private void setValueByBundle(Context context, Bundle bundle) {
        //set value and unit
        if (bundle.getFreeUnitsPresentation() != null) {
            setValueByPresentation(bundle.getFreeUnitsPresentation());
        } else {
            setValueByPresentation(bundle.getUsageAmountPresentation());
        }
        //set bundle color it can give a number format exception
        try {
            if (bundle.getFreeUnits() != null && bundle.getMaxUnits() != null) {
                setValueColor(context, Long.parseLong(bundle.getFreeUnits()), Long.parseLong(bundle.getMaxUnits()));
            } else if (bundle.getUsageAmount() != null && bundle.getMaxUnits() != null) {
                long usageAmount = Long.parseLong(bundle.getUsageAmount());
                long max = Long.parseLong(bundle.getMaxUnits());
                setValueColor(context, max - usageAmount, max);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    /**
     * sets the unit and the value based on the the respresentation
     *
     * @param presentation
     */
    private void setValueByPresentation(String presentation) {
        String[] strings = presentation.split(" ");
        value = strings[0];
        if (strings.length > 1) unit = strings[1]; //if the text does not contain a space character
    }

    /**
     * Sets value color based ont he remaining value and the max value
     * @param context
     * @param remainingValue
     * @param maxValue
     */
    private void setValueColor(Context context, long remainingValue, long maxValue) {
        double remainingPercentage = getPercentage(remainingValue, maxValue);
        if (remainingPercentage < PERCENTAGE_RED) {
            color = context.getResources().getColor(R.color.usage_red);
        } else if (remainingPercentage < PERCENTAGE_ORANGE) {
            color = context.getResources().getColor(R.color.usage_orange);
        } else {
            color = context.getResources().getColor(R.color.usage_green);
        }
    }

    /**
     * returns the percentage of the usedValue/maxValue but returns 1 when usedValue is higher than the maxValue
     * @param usedValue
     * @param maxValue
     * @return
     */
    private double getPercentage(double usedValue, double maxValue) {
        if (usedValue > maxValue) return 1;
        return usedValue / maxValue;
    }
}
