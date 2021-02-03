package net.scales.flows.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

public class FileUtils {

    /**
     * Converts the file to string
     */
    public static String convertToString(InputStream stream) throws IOException {
        return IOUtils.toString(stream, StandardCharsets.UTF_8);
    }

    /**
     * Hashes the file with SHA-256 algorithm
     */
    public static String sha256(InputStream stream) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] block = new byte[4096];
        int length;

        while ((length = stream.read(block)) > 0) {
            digest.update(block, 0, length);
        }

        return DatatypeConverter.printHexBinary(digest.digest());
    }

}