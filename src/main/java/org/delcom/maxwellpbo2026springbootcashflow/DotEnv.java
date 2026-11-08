package org.delcom.maxwellpbo2026springbootcashflow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Pemuat sederhana file .env ke System properties.
 * Nilai yang sudah ada di System properties atau environment variable tidak ditimpa,
 * sehingga {@code set APP_PORT=9090} di terminal tetap menang atas isi .env.
 */
public final class DotEnv {
    private static final Path DEFAULT_FILE = Path.of(".env");

    private DotEnv() {
    }

    /** Memuat file .env di direktori kerja (diabaikan bila tidak ada). */
    public static void load() throws IOException {
        load(DEFAULT_FILE);
    }

    /** Memuat file .env dari path tertentu (diabaikan bila tidak ada). */
    public static void load(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            return;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = stripQuotes(line.substring(separator + 1).trim());
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
