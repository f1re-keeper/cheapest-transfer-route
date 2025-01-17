package org.example.cheapesttransferroute;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cheapesttransferroute.Controller.TransferController;
import org.example.cheapesttransferroute.Model.Route;
import org.example.cheapesttransferroute.Model.Transfer;
import org.example.cheapesttransferroute.Repository.Storage;
import org.example.cheapesttransferroute.Repository.TransferRep;
import org.example.cheapesttransferroute.Service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TestTransferController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMVC;

    @Test
    public void testGetBestRoute() throws Exception {
        mockMVC.perform(get("/api/getBestRoute"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalCost").exists())
                .andExpect(jsonPath("$.totalWeight").exists());
    }

    @Test
    public void testCalculatedRoute() throws Exception {
        Route request = new Route(15, Arrays.asList
                (
                        new Transfer(5, 10),
                        new Transfer(10, 20),
                        new Transfer(3, 5),
                        new Transfer(8, 15)
                )
        );
        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCalculatedRouteEmptyTransfers() throws Exception {
        Route route = new Route(15, Arrays.asList());

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(route)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.availableTransfers").value("Available transfers must not be empty"));
    }

    @Test
    public void testCalculatedRouteNullTransfers() throws Exception {
        Route route = new Route(15, null);

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(route)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.availableTransfers").value("Available transfers must not be null"));
    }

    @Test
    public void testInvalidMaxWeight() throws Exception {
        Route invalidRoute = new Route(-1, Arrays.asList(
                new Transfer(12, 15),
                new Transfer(5, 23)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maxWeight").value("Max weight has to be at least 1"));
    }

    @Test
    public void testNullMaxWeight() throws Exception {
        Route invalidRoute = new Route(null, Arrays.asList(
                new Transfer(12, 15),
                new Transfer(5, 23)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.maxWeight").value("Max weight must not be null"));
    }

    @Test
    public void testInvalidTransferWeightInput() throws Exception {
        Route invalidRoute = new Route(10, Arrays.asList(
                new Transfer(-5, 10), // Invalid weight
                new Transfer(10, 20)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidTransferCostInput() throws Exception {
        Route invalidRoute = new Route(10, Arrays.asList(
                new Transfer(5, -10),
                new Transfer(10, 20)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest());
    }

}
