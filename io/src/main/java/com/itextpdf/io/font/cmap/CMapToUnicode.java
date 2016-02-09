package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a CMap file.
 *
 * @author Ben Litchfield (ben@benlitchfield.com)
 */
public class CMapToUnicode extends AbstractCMap {

    public static CMapToUnicode EmptyCMapToUnicodeMap = new CMapToUnicode(true);

    private Map<Integer, String> singleByteMappings;
    private Map<Integer, String> doubleByteMappings;

    private CMapToUnicode(boolean emptyCMap) {
        singleByteMappings = Collections.emptyMap();
        doubleByteMappings = Collections.emptyMap();
    }

    /**
     * Creates a new instance of CMap.
     */
    public CMapToUnicode() {
        //default constructor
        singleByteMappings = new HashMap<>();
        doubleByteMappings = new HashMap<>();
    }

    /**
     * This will tell if this cmap has any one byte mappings.
     *
     * @return true If there are any one byte mappings, false otherwise.
     */
    public boolean hasOneByteMappings() {
        return !singleByteMappings.isEmpty();
    }

    /**
     * This will tell if this cmap has any two byte mappings.
     *
     * @return true If there are any two byte mappings, false otherwise.
     */
    public boolean hasTwoByteMappings() {
        return !doubleByteMappings.isEmpty();
    }

    /**
     * This will perform a lookup into the map.
     *
     * @param code   The code used to lookup.
     * @param offset The offset into the byte array.
     * @param length The length of the data we are getting.
     * @return The string that matches the lookup.
     */
    //TODO change to char[]?
    public String lookup(byte[] code, int offset, int length) {

        String result = null;
        Integer key;
        if (length == 1) {
            key = code[offset] & 0xff;
            result = singleByteMappings.get(key);
        } else if (length == 2) {
            int intKey = code[offset] & 0xff;
            intKey <<= 8;
            intKey += code[offset + 1] & 0xff;
            key = intKey;
            result = doubleByteMappings.get(key);
        }

        return result;
    }

    public char[] lookup(byte[] code) {
        String result = lookup(code, 0, code.length);
        return result != null ? result.toCharArray() : null;
    }

    public Map<Integer, Integer> createReverseMapping() throws java.io.IOException {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, String> entry : singleByteMappings.entrySet()) {
            result.put(convertToInt(entry.getValue()), entry.getKey());
        }
        for (Map.Entry<Integer, String> entry : doubleByteMappings.entrySet()) {
            result.put(convertToInt(entry.getValue()), entry.getKey());
        }
        return result;
    }

    public IntHashtable createDirectMapping() {
        IntHashtable result = new IntHashtable();
        for (Map.Entry<Integer, String> entry : singleByteMappings.entrySet()) {
            result.put(entry.getKey(), convertToInt(entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : doubleByteMappings.entrySet()) {
            result.put(entry.getKey(), convertToInt(entry.getValue()));
        }
        return result;
    }

    private int convertToInt(String s) {
        int value = 0;
        for (int i = 0; i < s.length() - 1; i++) {
            value += s.charAt(i);
            value <<= 8;
        }
        value += s.charAt(s.length() - 1);
        return value;
    }

    void addChar(int cid, String uni) {
        doubleByteMappings.put(cid, uni);
    }

    void addChar(int cid, char[] uni) {
        doubleByteMappings.put(cid, new String(uni));
    }

    @Override
    void addChar(String mark, CMapObject code) {
        try {
            String dest = createStringFromBytes((byte[])code.getValue());
            if (mark.length() == 1) {
                singleByteMappings.put((int) mark.charAt(0), dest);
            } else if (mark.length() == 2) {
                int intSrc = mark.charAt(0);
                intSrc <<= 8;
                intSrc |= mark.charAt(1);
                doubleByteMappings.put(intSrc, dest);
            } else {
                Logger logger = LoggerFactory.getLogger(CMapToUnicode.class);
                logger.warn(LogMessageConstant.TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException();
        }

    }

    private String createStringFromBytes(byte[] bytes) throws java.io.IOException {
        String retval;
        if (bytes.length == 1) {
            retval = String.valueOf((char)(bytes[0] & 0xFF));
        } else {
            char[] chars = new char[]{(char)(bytes[0] & 0xFF), (char)(bytes[1] & 0xFF)};
            retval = new String(chars);
        }
        return retval;
    }

    public static CMapToUnicode getIdentity() {
        CMapToUnicode uni = new CMapToUnicode();
        for (int i = 0; i < 65537; i++) {
            uni.addChar(i, Utilities.convertFromUtf32(i));
        }
        return uni;
    }
}