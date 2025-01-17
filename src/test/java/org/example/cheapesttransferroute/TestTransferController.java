package org.example.cheapesttransferroute;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.example.cheapesttransferroute.Controller.TransferController;
import org.example.cheapesttransferroute.ErrorHandlers.*;
import org.example.cheapesttransferroute.Model.CheapestRoute;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @MockBean
    private TransferService transferService;

    @Test
    public void testGetBestRoute() throws Exception {
        CheapestRoute expectedRoute = new CheapestRoute();
        when(transferService.findCheapestRoute()).thenReturn(expectedRoute);
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
    public void testCalculatedRouteInvalidJson() throws Exception {
        String malformedJson = "{\"maxWeight\": 15, \"availableTransfers\": [{}]}";

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
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
                new Transfer(-5, 10),
                new Transfer(10, 20)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['availableTransfers[0].weight']")
                        .value("Weight must be at least 1"));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['availableTransfers[0].cost']")
                        .value("Cost must be at least 1"));
    }

    @Test
    public void testInvalidTransfer() throws Exception {
        Route invalidRoute = new Route(10, Arrays.asList(
                new Transfer(-5, 10),
                new Transfer(10, -20)
        ));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoute)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['availableTransfers[0].weight']")
                        .value("Weight must be at least 1"))
                .andExpect(jsonPath("$['availableTransfers[1].cost']")
                        .value("Cost must be at least 1"));
    }

    @Test
    public void testCalculatedRouteWithServiceException() throws Exception {
        Route request = new Route(15, Arrays.asList(
                new Transfer(5, 10),
                new Transfer(10, 20)
        ));

        doThrow(new RuntimeException("Error processing request"))
                .when(transferService)
                .processRequest(any(Route.class));

        // Perform the request and verify the response
        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())  // Still expect 500
                .andExpect(content().string("Error processing request"))  // Match the message from your handler
                .andDo(result -> {
                    // Print response for debugging
                    System.out.println("Response Status: " + result.getResponse().getStatus());
                    System.out.println("Response Body: " + result.getResponse().getContentAsString());
                });

        verify(transferService).clearData();
    }

    @Test
    public void testHandleJsonFormatException() throws Exception {
        //This Json is not written with correct format
        String malformedJson = "{\"maxWeight\": 15, \"availableTransfers\": [{\"weight\": 5, \"cost\": 10}";

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("Invalid JSON format")))
                .andDo(result -> {
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    @Test
    public void testHandleInvalidJsonFormatException() throws Exception {
        String validJson = "{\"maxWeight\": 15, \"availableTransfers\": " +
                "[{\"weight\": 5, \"cost\": 10}]}";

        doThrow(new InvalidJsonFormatException("Invalid transfer format: weight cannot exceed maxWeight"))
                .when(transferService)
                .processRequest(any(Route.class));

        mockMVC.perform(post("/api/requestInput")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value("Invalid transfer format: weight cannot exceed maxWeight"));
    }

}
