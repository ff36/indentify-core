/*
 * Created Jul 29, 2014.
 * Copyright 2014 SOLiD Inc ALL RIGHTS RESERVED. 
 * Developer: Tarka L'Herpiniere <tarka@solid.com>.
 */
package com.auth8.security;

import com.auth8.persistent.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs cryto functions to determine the legitimacy of an authentication
 * request.
 *
 * @version 1.0.0
 * @since Build 140729.140400
 * @author Tarka L'Herpiniere
 * @author <tarka@solid.com>
 */
public class Signature {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private static final Logger LOG = LoggerFactory.getLogger(Signature.class);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new instance of Signature
     */
    public Signature() {
    }
//</editor-fold>

    /**
     * Sign the Request URL to match against the provided signature.
     *
     * @param stringToSign
     * @param publicKey
     * @return
     * @throws IOException
     */
    public static String signUrl(String stringToSign, String publicKey) throws IOException {

        ObjectContext context = new ServerRuntime("cayenne-project.xml").newContext();
        Expression userE = ExpressionFactory.likeExp(User.PUBLIC_KEY_PROPERTY, publicKey);
        SelectQuery userQ = new SelectQuery(User.class, userE);
        User user = (User) context.performQuery(userQ).get(0);

        return encodeBytes(
                HmacSha1(
                        user.getPrivateKey(),
                        utf8Encode(stringToSign)));
    }

    /**
     * Sign the upgrade package MD5 checksum to match generate the zip password.
     *
     * @param stringToSign
     * @param privateKey
     * @return
     * @throws IOException
     */
    public static String signUpgradePackage(String stringToSign, String privateKey) throws IOException {
        return encodeBytes(HmacSha1(privateKey, utf8Encode(stringToSign)));
    }

    /**
     * UTF8 encoder.
     *
     * @param data
     * @return
     */
    private static byte[] utf8Encode(String data) {
        try {
            //printBytes(data.getBytes("UTF-8"));
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("COULD NOT ENCODE URF8 AWS HEADER", ex);
        }

        return null;
    }

    /**
     * HMAC SHA1 Signature encryption.
     *
     * @param secretKey
     * @param data
     * @return
     */
    private static byte[] HmacSha1(String secretKey, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            //printBytes(secretKey.getBytes());
            SecretKeySpec keySpec = new SecretKeySpec(
                    secretKey.getBytes(),
                    "HmacSHA1");
            mac.init(keySpec);

            return mac.doFinal(data);
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            LOG.error("HMAC SHA1 REQUEST SIGNATURE ERROR", ex);
        }

        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="Private fields">
    /* ********  P R I V A T E   F I E L D S  ******** */
    /**
     * Maximum line length (76) of Base64 output.
     */
    private final static int MAX_LINE_LENGTH = 76;
    /**
     * The equals sign (=) as a byte.
     */
    private final static byte EQUALS_SIGN = (byte) '=';
    /**
     * The new line character (\n) as a byte.
     */
    private final static byte NEW_LINE = (byte) '\n';
    /**
     * Preferred encoding.
     */
    private final static String PREFERRED_ENCODING = "UTF-8";

    /* ********  S T A N D A R D   B A S E 6 4   A L P H A B E T  ******** */
    /**
     * The 64 valid Base64 values.
     */
    //private final static byte[] ALPHABET;
    /* Host platform me be something funny like EBCDIC, so we hardcode these values. */
    private final static byte[] _STANDARD_ALPHABET = {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
        (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
        (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
        (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
        (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
        (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'
    };

    /**
     * <p>
     * Encodes up to three bytes of the array <var>source</var>
     * and writes the resulting four Base64 bytes to <var>destination</var>. The
     * source and destination arrays can be manipulated anywhere along their
     * length by specifying
     * <var>srcOffset</var> and <var>destOffset</var>. This method does not
     * check to make sure your arrays are large enough to accomodate
     * <var>srcOffset</var> + 3 for the <var>source</var> array or
     * <var>destOffset</var> + 4 for the <var>destination</var> array. The
     * actual number of significant bytes in your array is given by
     * <var>numSigBytes</var>.</p>
     * <p>
     * This is the lowest level of the encoding methods with all possible
     * parameters.</p>
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param numSigBytes the number of significant bytes in your array
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    private static byte[] encode3to4(
            byte[] source, int srcOffset, int numSigBytes,
            byte[] destination, int destOffset) {
        byte[] ALPHABET = _STANDARD_ALPHABET;

        //           1         2         3
        // 01234567890123456789012345678901 Bit position
        // --------000000001111111122222222 Array position from threeBytes
        // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
        //          >>18  >>12  >> 6  >> 0  Right shift necessary
        //                0x3f  0x3f  0x3f  Additional AND
        // Create buffer with zero-padding if there are only one or two
        // significant bytes passed in the array.
        // We have to shift left 24 in order to flush out the 1's that appear
        // when Java treats a value as negative that is cast from a byte to an int.
        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0) | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0) | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
                return destination;

            case 2:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            case 1:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
                destination[destOffset + 2] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                return destination;

            default:
                return destination;
        }   // end switch
    }

    public static String encodeBytes(byte[] source) {
        boolean breakLines = false;
        int len = source.length;
        int len43 = len * 4 / 3;
        byte[] outBuff = new byte[(len43) // Main 4:3
                + ((len % 3) > 0 ? 4 : 0) // Account for padding
                + (breakLines ? (len43 / MAX_LINE_LENGTH) : 0)]; // New lines
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for (; d < len2; d += 3, e += 4) {
            encode3to4(source, d, 3, outBuff, e);

            lineLength += 4;
            if (breakLines && lineLength == MAX_LINE_LENGTH) {
                outBuff[e + 4] = NEW_LINE;
                e++;
                lineLength = 0;
            }   // end if: end of line
        }   // en dfor: each piece of array

        if (d < len) {
            encode3to4(source, d, len - d, outBuff, e);
            e += 4;
        }   // end if: some padding needed

        // Return value according to relevant encoding.
        try {
            return new String(outBuff, 0, e, PREFERRED_ENCODING);
        } // end try
        catch (java.io.UnsupportedEncodingException uue) {
            return new String(outBuff, 0, e);
        }   // end catch
    }   // end encodeBytes
//</editor-fold>

}
