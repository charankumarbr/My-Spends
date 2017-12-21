package in.phoenix.myspends.util;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Charan.Br on 12/21/2017.
 */

public final class AppSecurity {

    private static String charset = "UTF-8";

    public static String encrypt(String value) throws Exception {

        if (TextUtils.isEmpty(value)) {
            return null;
        }

        String checkedValue;

        // Create key and cipher
        SecretKeySpec aesKey = new SecretKeySpec(AppConstants.dummy.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(value.getBytes(charset));

        checkedValue = Base64.encodeToString(encrypted, Base64.DEFAULT);
        AppLog.d("AppSecurity", "encrypt: Byte[] Length:" + encrypted.length);
        AppLog.d("AppSecurity", "encrypt: Checked Value::" + checkedValue + " :: Length:" + checkedValue.length());

        return checkedValue;
    }

    public static String decrypt(String checkedValue) throws Exception {

        if (TextUtils.isEmpty(checkedValue)) {
            return null;
        }

        String value;

        // now convert the string to byte array for decryption
        byte[] bb = new byte[checkedValue.length()];
        for (int i = 0; i < checkedValue.length(); i++) {
            bb[i] = (byte) checkedValue.charAt(i);
        }

        // Create key and cipher
        SecretKeySpec aesKey = new SecretKeySpec(AppConstants.dummy.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        // decrypt the checkedvalue
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decrypted = Base64.decode(checkedValue, Base64.DEFAULT);
        byte[] plainText = cipher.doFinal(decrypted);
        value = new String(plainText, charset);
        AppLog.d("AppSecurity", "decrypt: Byte[] Length:" + decrypted.length);
        AppLog.d("AppSecurity", "decrypt: Value:"  + value + " :: Length:" + value.length());

        return value;
    }

    private static String abraCaDabra(String input) {

        int inputLength = input.length();
        int mid = inputLength / 2;
        if (inputLength % 2 == 0) {
            mid--;
        }

        char[] jumbleChar = new char[input.length()];

        int index = 0;
        int lCounter = 0;
        while (index < input.length()) {
            if (index % 2 == 1) {
                //-- odd index --//
                lCounter++;
                jumbleChar[mid + lCounter] = input.charAt(index);

            } else {
                //-- even index --//
                jumbleChar[mid - lCounter] = input.charAt(index);
            }
            index++;
        }

        String jumbledData = String.valueOf(jumbleChar);
        AppLog.d("AppSecurity", "abraCaDabra:: Jumbled String:" + jumbledData + " :: Length:" + jumbledData.length());

        return jumbledData;
    }

    private static String reveal(String jumbledData) {

        int inputLength = jumbledData.length();
        int mid = inputLength / 2;
        if (inputLength % 2 == 0) {
            mid--;
        }

        char[] revealChar = new char[jumbledData.length()];

        int index = 0;
        int counter = 0;
        boolean isMid = true;
        while (index < jumbledData.length()) {
            if (isMid) {
                revealChar[index] = jumbledData.charAt(mid - counter);
                isMid = false;
                counter++;

            } else {
                revealChar[index] = jumbledData.charAt(mid + counter);
                isMid = true;
            }

            index++;
        }

        String revealData = String.valueOf(revealChar);
        AppLog.d("AppSecurity", "reveal:: Reveal String:" + revealData + " :: Length:" + revealData.length());

        return revealData;
    }

}
