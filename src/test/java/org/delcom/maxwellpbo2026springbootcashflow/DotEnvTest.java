package org.delcom.maxwellpbo2026springbootcashflow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DotEnvTest {
    private static final String[] KEYS = {"DOTENV_TEST_A", "DOTENV_TEST_B", "DOTENV_TEST_C", "DOTENV_TEST_D"};

    @AfterEach
    void cleanUp() {
        for (String key : KEYS) {
            System.clearProperty(key);
        }
    }

    @Test
    void load_readsKeyValuePairs(@TempDir Path dir) throws Exception {
        Path file = dir.resolve(".env");
        Files.writeString(file, """
                # komentar
                DOTENV_TEST_A=8081

                DOTENV_TEST_B = "quoted"
                DOTENV_TEST_C='single'
                baris-tanpa-sama-dengan
                =tanpa-key
                """);

        DotEnv.load(file);

        assertEquals("8081", System.getProperty("DOTENV_TEST_A"));
        assertEquals("quoted", System.getProperty("DOTENV_TEST_B"));
        assertEquals("single", System.getProperty("DOTENV_TEST_C"));
    }

    @Test
    void load_doesNotOverrideExistingProperty(@TempDir Path dir) throws Exception {
        System.setProperty("DOTENV_TEST_A", "dari-terminal");
        Path file = dir.resolve(".env");
        Files.writeString(file, "DOTENV_TEST_A=dari-file\n");

        DotEnv.load(file);

        assertEquals("dari-terminal", System.getProperty("DOTENV_TEST_A"));
    }

    @Test
    void load_keepsShortValueWithoutQuotes(@TempDir Path dir) throws Exception {
        Path file = dir.resolve(".env");
        Files.writeString(file, "DOTENV_TEST_D=x\n");

        DotEnv.load(file);

        assertEquals("x", System.getProperty("DOTENV_TEST_D"));
    }

    @Test
    void load_ignoresMissingFile(@TempDir Path dir) {
        assertDoesNotThrow(() -> DotEnv.load(dir.resolve("tidak-ada.env")));
        assertNull(System.getProperty("DOTENV_TEST_A"));
    }

    @Test
    void load_defaultFileDoesNotThrow() {
        assertDoesNotThrow(() -> DotEnv.load());
    }
}
