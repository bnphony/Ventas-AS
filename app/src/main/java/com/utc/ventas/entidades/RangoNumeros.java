package com.utc.ventas.entidades;

import android.text.InputFilter;
import android.text.Spanned;

public class RangoNumeros implements InputFilter {
    private double minValue;
    private double maxValue;

    public RangoNumeros(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Append the new text to the existing text
            String newValue = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

            // Convert the text to a double
            double input = Double.parseDouble(newValue);

            // Check if the input is within the specified range
            if (isInRange(input)) {
                return null; // Accept the input
            }
        } catch (NumberFormatException ignored) {
            // Ignore if the input cannot be converted to a double
        }

        // Reject the input
        return "";
    }

    private boolean isInRange(double input) {
        return input >= minValue && input <= maxValue;
    }
}
