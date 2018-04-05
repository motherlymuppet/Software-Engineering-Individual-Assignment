package org.stevenlowes.university.seassignment.guis.interpolators;

public class EaseOutPowInterpolator extends EasingInterpolator {
    public EaseOutPowInterpolator() {
        super(EasingMode.EASE_OUT);
    }

    @Override
    protected double baseCurve(double v) {
        return Math.pow(v, 3);
    }
}
