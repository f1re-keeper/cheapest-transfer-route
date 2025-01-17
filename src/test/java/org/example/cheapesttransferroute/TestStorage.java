package org.example.cheapesttransferroute;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.cheapesttransferroute.Model.Route;
import org.example.cheapesttransferroute.Model.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.example.cheapesttransferroute.Repository.*;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestStorage{
    private Storage storage;
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        storage = new Storage("Initializing Empty Storage");
    }

    @Test
    public void testDefaultConstructor() throws IOException {
        storage = new Storage();

        //We need to get the value of maxWeight from the default file,
        //otherwise we would have to change the data.json file every time before we ran this test
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src/main/resources/data.json"));
        String maxWeightStr = Optional.ofNullable(rootNode.get("maxWeight"))
                .map(JsonNode::asText)
                .orElse(null);

        int maxWeight = 0;
        if(maxWeightStr != null){
            maxWeight = Integer.parseInt(maxWeightStr);
        }

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(maxWeight));
    }

    @Test
    public void testInitWithException() throws IOException {
        storage.setJsonPath("src/test/java/jsonFiles/InvalidFile.json");
        storage.init();
        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(0));
        assertTrue(storage.getData().get(0).isEmpty());
    }

    @Test
    public void testInitWithEmptyFile() throws IOException {
        Path tempFile = tempDir.resolve("emptyFile.json");
        Files.createFile(tempFile);

        storage.setJsonPath(tempFile.toString());
        storage.init();

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(0));
        assertTrue(storage.getData().get(0).isEmpty());
    }

    @Test
    public void testInitWithFullFile() throws IOException {
        String validJson = "src/test/java/jsonFiles/ValidCase.json";

        storage.setJsonPath(validJson);
        storage.init();

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(24));
        assertEquals(4, storage.getData().get(24).size());
    }

    @Test
    public void testInitWithEmptyTransfers() throws IOException {
        String emptyTransfers = "src/test/java/jsonFiles/EmptyTransfers.json";

        storage.setJsonPath(emptyTransfers);
        storage.init();

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().get(24).isEmpty());
    }

    @Test
    public void testClearData() throws IOException {
        String clearFile = "src/test/java/jsonFiles/ClearAndUpdate.json";

        storage.setJsonPath(clearFile);
        storage.init();
        storage.clearData();

        assertTrue(storage.getData().isEmpty());
        String fileContent = new String(Files.readAllBytes(Paths.get(clearFile)));
        assertEquals("{}", fileContent);
    }

    @Test
    public void testUpdateData() throws IOException {
        String updateFile = "src/test/java/jsonFiles/ClearAndUpdate.json";

        storage.setJsonPath(updateFile);

        Route route = new Route();
        route.setMaxWeight(200);
        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = new Transfer();
        transfer.setWeight(20);
        transfer.setCost(75);
        transfers.add(transfer);
        route.setAvailableTransfers(transfers);

        storage.updateData(route);

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(200));
        assertEquals(1, storage.getData().get(200).size());

        storage = new Storage("Initializing Empty Storage");
        storage.setJsonPath(updateFile);
        storage.init();

        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(200));
        assertEquals(1, storage.getData().get(200).size());
    }

    @Test
    void testSaveToFileIOException() throws IOException {
        Path readOnlyFile = tempDir.resolve("readonly.json");
        Files.writeString(readOnlyFile, "{}");
        File testFile = readOnlyFile.toFile();
        testFile.setReadOnly();

        try {
            storage.setJsonPath(testFile.getAbsolutePath());
            List<Transfer> transfers = new ArrayList<>();
            transfers.add(new Transfer(1, 1));
            storage.getData().put(10, transfers);

            Route route = new Route();
            storage.setJsonPath(testFile.getAbsolutePath());
            storage.updateData(route);

            assertFalse(storage.getData().isEmpty());

        } finally {
            testFile.setWritable(true);
            Files.deleteIfExists(testFile.toPath());
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testInitWithNullTransfers() throws IOException {
        storage.setJsonPath("src/test/java/jsonFiles/NullAvailableTransfers.json");
        storage.init();

        assertFalse(storage.getData().isEmpty());
        assertTrue(storage.getData().get(24).isEmpty());
    }

    @Test
    void testInitWithNullMaxWeight() throws IOException {
        storage.setJsonPath("src/test/java/jsonFiles/NullMaxWeight.json");
        storage.init();

        assertFalse(storage.getData().isEmpty());
        assertTrue(storage.getData().containsKey(0));
    }

    @Test
    void testClearDataWithIOException() throws IOException {
        Path readOnlyFile = tempDir.resolve("readonly.json");
        Files.writeString(readOnlyFile, "{}");
        File file = readOnlyFile.toFile();
        file.setReadOnly();

        storage.setJsonPath(file.getAbsolutePath());
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(1, 1));
        storage.getData().put(10, transfers);

        storage.clearData();

        assertTrue(storage.getData().isEmpty());
        file.setWritable(true);
        Files.deleteIfExists(file.toPath());
        Files.deleteIfExists(tempDir);
    }

    @Test
    void testInitHandlesIOException() throws IOException {
        Storage storage = new Storage("test");
        File tempFile = Files.createTempDirectory("unreadable").toFile();
        storage.setJsonPath(tempFile.getAbsolutePath());

        storage.init();

        assertTrue(storage.getData().containsKey(0));
        assertEquals(0, storage.getData().get(0).size());

        tempFile.delete();
    }

}
