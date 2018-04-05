package org.stevenlowes.university.seassignment.guis.interpolators;

public class EaseInPowInterpolator extends EasingInterpolator {
    public EaseInPowInterpolator() {
        super(EasingMode.EASE_IN);
    }

    @Override
    protected double baseCurve(double v) {
        return Math.pow(v, 3);
    }
}
