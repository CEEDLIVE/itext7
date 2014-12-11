package com.itextpdf.canvas.colors;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;

import java.util.Arrays;

public class Color {

    protected PdfColorSpace colorSpace;
    protected float[] colorValue;

    public Color(PdfObject pdfObject, float[] colorValue) throws PdfException {
        this(PdfColorSpace.makeColorSpace(pdfObject, null), colorValue);
    }

    public Color(PdfObject pdfObject) throws PdfException {
        this(pdfObject, null);
    }

    public Color(PdfColorSpace colorSpace, float[] colorValue) {
        this.colorSpace = colorSpace;
        if (colorValue == null)
            this.colorValue = new float[colorSpace.getNumOfComponents()];
        else
            this.colorValue = colorValue;
    }

    public Color(PdfColorSpace colorSpace) {
        this(colorSpace, null);
    }

    public int getNumOfComponents() {
        return colorValue.length;
    }

    public PdfColorSpace getColorSpace() {
        return colorSpace;
    }

    public float[] getColorValue() {
        return colorValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (colorSpace != null ? !colorSpace.equals(color.colorSpace) : color.colorSpace != null) return false;
        if (!Arrays.equals(colorValue, color.colorValue)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = colorSpace != null ? colorSpace.hashCode() : 0;
        result = 31 * result + (colorValue != null ? Arrays.hashCode(colorValue) : 0);
        return result;
    }
}
