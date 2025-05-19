package guichafy.sample_api.modules.users.infrastructure.adapters.in;

// import guichafy.sample_api.modules.users.application.port.in.UserUseCase;
// import guichafy.sample_api.modules.users.domain.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import java.io.IOException;
// import java.util.Arrays;
// import java.util.Optional;

// import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // @MockBean
    // private UserUseCase userUseCase;

    private static ToxiproxyClient toxiproxyClient;
    public static Proxy jsonPlaceholderProxy;
    private static final String JSONPLACEHOLDER_SERVICE_NAME = "jsonplaceholder_service";
    private static final String TOXIPROXY_HOST = "localhost";
    private static final int TOXIPROXY_CONTROL_PORT = 8474;
    private static final String UPSTREAM_JSONPLACEHOLDER = "jsonplaceholder.typicode.com:443";

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        System.out.println("INFO: UserControllerIntegrationTest - Starting ToxiProxy dynamic property registration for service: " + JSONPLACEHOLDER_SERVICE_NAME + ", Upstream: " + UPSTREAM_JSONPLACEHOLDER);
        try {
            toxiproxyClient = new ToxiproxyClient(TOXIPROXY_HOST, TOXIPROXY_CONTROL_PORT);

            // Initial, more rigorous connectivity check using toxiproxyClient.version()
            try {
                String serverVersion = toxiproxyClient.version();
                System.out.println("INFO: Successfully connected to ToxiProxy server (version: " + serverVersion +
                                   ") at " + TOXIPROXY_HOST + ":" + TOXIPROXY_CONTROL_PORT);
            } catch (IOException e) {
                String errorMessage = "CRITICAL ERROR: Initial health check/version retrieval from ToxiProxy server (at " +
                                      TOXIPROXY_HOST + ":" + TOXIPROXY_CONTROL_PORT + ") failed. " +
                                      "This strongly suggests the ToxiProxy server is not running, is inaccessible, " +
                                      "or is not a valid ToxiProxy instance. Please verify the ToxiProxy server. Details: " + e.getMessage();
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage, e); // Fail fast, Spring context loading will halt
            }

            // Attempt to clean up existing proxy from previous runs, if any.
            try {
                Proxy existingProxy = toxiproxyClient.getProxy(JSONPLACEHOLDER_SERVICE_NAME);
                if (existingProxy != null) {
                    System.out.println("INFO: Found existing ToxiProxy proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                                       "'. Attempting to delete it before creating a new one.");
                    existingProxy.delete();
                    System.out.println("INFO: Successfully deleted existing ToxiProxy proxy '" +
                                       JSONPLACEHOLDER_SERVICE_NAME + "'.");
                }
            } catch (IOException e) { // IOException during cleanup is logged but not fatal for this specific step
                System.err.println("WARNING: IOException during pre-test cleanup of ToxiProxy proxy '" +
                                   JSONPLACEHOLDER_SERVICE_NAME + "'. This might be due to the proxy not existing or a transient issue. " +
                                   "Continuing with proxy creation. Details: " + e.getMessage());
            } catch (Exception e) { // Other exceptions during cleanup
                System.err.println("WARNING: Non-critical issue during pre-test proxy cleanup for '" +
                                   JSONPLACEHOLDER_SERVICE_NAME + "'. Details: " + e.getMessage());
            }

            // Create the new proxy
            System.out.println("INFO: Attempting to create ToxiProxy proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                               "' listening on 0.0.0.0:0 for upstream " + UPSTREAM_JSONPLACEHOLDER);
            try {
                jsonPlaceholderProxy = toxiproxyClient.createProxy(
                        JSONPLACEHOLDER_SERVICE_NAME,
                        "0.0.0.0:0", // Listen on any available port on all interfaces
                        UPSTREAM_JSONPLACEHOLDER
                );
            } catch (IOException e) {
                String errorMessage = "CRITICAL ERROR: toxiproxyClient.createProxy() for service '" + JSONPLACEHOLDER_SERVICE_NAME +
                                      "' (upstream: " + UPSTREAM_JSONPLACEHOLDER + ") failed directly with an IOException. " +
                                      "ToxiProxy server: " + TOXIPROXY_HOST + ":" + TOXIPROXY_CONTROL_PORT + ". " +
                                      "This indicates the server was likely reachable but unable to fulfill the proxy creation request. " +
                                      "Check ToxiProxy server logs. Client exception: " + e.getMessage();
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage, e);
            }

            // Validate proxy creation
            if (jsonPlaceholderProxy == null) {
                String errorMessage = "CRITICAL ERROR: ToxiProxy client's createProxy method returned null for service '" +
                                      JSONPLACEHOLDER_SERVICE_NAME + "' without throwing an IOException. This is highly unexpected. " +
                                      "Upstream: " + UPSTREAM_JSONPLACEHOLDER;
                System.err.println(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            System.out.println("INFO: Successfully created ToxiProxy proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                               "' with listen address: " + jsonPlaceholderProxy.getListen());

            // Additional verification: try to re-fetch the proxy to ensure it's properly registered
            try {
                Proxy retrievedProxy = toxiproxyClient.getProxy(JSONPLACEHOLDER_SERVICE_NAME);
                if (retrievedProxy == null) {
                    String errorMessage = "CRITICAL CONSISTENCY ERROR: Proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                                          "' was reportedly created by createProxy(), but toxiproxyClient.getProxy() returned null immediately after. " +
                                          "This suggests an internal issue with the ToxiProxy server or client library state. Upstream was intended to be: " + UPSTREAM_JSONPLACEHOLDER;
                    System.err.println(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                // Basic sanity check on key properties
                if (!jsonPlaceholderProxy.getName().equals(retrievedProxy.getName()) ||
                    !jsonPlaceholderProxy.getListen().equals(retrievedProxy.getListen()) ||
                    !jsonPlaceholderProxy.getUpstream().equals(retrievedProxy.getUpstream())) {
                    String errorMessage = "CRITICAL CONSISTENCY ERROR: Proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                                          "' retrieved via getProxy() immediately after creation differs in key properties from the instance returned by createProxy(). " +
                                          "Instance from createProxy(): Name=" + jsonPlaceholderProxy.getName() + ", Listen=" + jsonPlaceholderProxy.getListen() + ", Upstream=" + jsonPlaceholderProxy.getUpstream() + ". " +
                                          "Instance from getProxy(): Name=" + retrievedProxy.getName() + ", Listen=" + retrievedProxy.getListen() + ", Upstream=" + retrievedProxy.getUpstream() + ". " +
                                          "Expected Upstream for this proxy was: " + UPSTREAM_JSONPLACEHOLDER + ".";
                    System.err.println(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                System.out.println("INFO: Successfully re-fetched and verified proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                                   "' from ToxiProxy server. Consistency check passed.");
            } catch (IOException e) {
                String errorMessage = "CRITICAL ERROR: An IOException occurred while attempting to re-fetch proxy '" + JSONPLACEHOLDER_SERVICE_NAME +
                                      "' (intended for upstream: " + UPSTREAM_JSONPLACEHOLDER + ") using toxiproxyClient.getProxy() immediately after its reported creation. " +
                                      "This indicates a potential problem with the ToxiProxy server's stability or ongoing communication issues. Details: " + e.getMessage();
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage, e); // Fail fast
            }


            // Validate listen address
            String listenAddress = jsonPlaceholderProxy.getListen();
            if (listenAddress == null || listenAddress.trim().isEmpty()) {
                String errorMessage = "CRITICAL ERROR: ToxiProxy proxy for '" + JSONPLACEHOLDER_SERVICE_NAME +
                                      "' (upstream: " + UPSTREAM_JSONPLACEHOLDER + ") returned a null or empty listen address after creation. This prevents configuring the application. " +
                                      "Proxy details: " + jsonPlaceholderProxy.toString();
                System.err.println(errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            String[] parts = listenAddress.split(":");
            if (parts.length < 2 || parts[parts.length - 1].trim().isEmpty()) {
                 String errorMessage = "CRITICAL ERROR: ToxiProxy proxy for '" + JSONPLACEHOLDER_SERVICE_NAME +
                                       "' (upstream: " + UPSTREAM_JSONPLACEHOLDER + ") returned an invalid listen address format: '" + listenAddress +
                                       "'. Expected 'host:port' or '[ipv6_host]:port'. Cannot extract port.";
                 System.err.println(errorMessage);
                 throw new IllegalStateException(errorMessage);
            }
            String port = parts[parts.length - 1];

            // Register the dynamic property for the base URL
            String proxyUrl = "http://" + TOXIPROXY_HOST + ":" + port;
            registry.add("external.api.jsonplaceholder.base-url", () -> proxyUrl);
            System.out.println("INFO: ToxiProxy dynamic property configured: 'external.api.jsonplaceholder.base-url' set to '" + proxyUrl +
                               "' (proxy for " + JSONPLACEHOLDER_SERVICE_NAME + " upstream: " + UPSTREAM_JSONPLACEHOLDER + ")");

        } catch (IOException e) { // Catches unexpected IOExceptions not handled by more specific blocks above.
            String errorMessage = "CRITICAL ERROR: An unexpected IOException occurred during ToxiProxy setup operations for service '" +
                                  JSONPLACEHOLDER_SERVICE_NAME + "' with server at " +
                                  TOXIPROXY_HOST + ":" + TOXIPROXY_CONTROL_PORT + ". " +
                                  "This occurred outside of specific handled operations like initial connection, proxy creation, or re-fetch. " +
                                  "Details: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage, e); // Ensures Spring context loading fails clearly
        } catch (IllegalStateException e) { // Catch our own validation exceptions
             System.err.println("CRITICAL VALIDATION ERROR during ToxiProxy setup: " + e.getMessage());
             throw e; // Re-throw to ensure Spring context loading fails
        } catch (Exception e) { // Catch other unexpected setup issues
            String errorMessage = "CRITICAL UNEXPECTED ERROR: An unforeseen issue occurred during ToxiProxy setup for service '" +
                                  JSONPLACEHOLDER_SERVICE_NAME + "'. Problem details: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage, e); // Ensures Spring context loading fails clearly
        }
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        if (jsonPlaceholderProxy != null) {
            jsonPlaceholderProxy.delete();
        }
    }

    @BeforeEach
    void setUpEach() throws IOException {
        if (jsonPlaceholderProxy != null) {
            jsonPlaceholderProxy.toxics().getAll().forEach(toxic -> {
                try {
                    toxic.remove();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to remove toxic", e);
                }
            });
        }
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        // User user1 = new User();
        // user1.setId(1L);
        // user1.setName("Leanne Graham");
        // user1.setUsername("Bret");
        // user1.setEmail("Sincere@april.biz");

        // User user2 = new User();
        // user2.setId(2L);
        // user2.setName("Ervin Howell");
        // user2.setUsername("Antonette");
        // user2.setEmail("Shanna@melissa.tv");

        // when(userUseCase.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data._embedded.userResponseList", hasSize(10))) // Adjusted from 2 to 10
                .andExpect(jsonPath("$.data._embedded.userResponseList[0].id", is(1)))
                .andExpect(jsonPath("$.data._embedded.userResponseList[0].name", is("Leanne Graham")))
                .andExpect(jsonPath("$.data._embedded.userResponseList[1].id", is(2)))
                .andExpect(jsonPath("$.data._embedded.userResponseList[1].name", is("Ervin Howell")));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() throws Exception {
        // User user = new User();
        // user.setId(1L);
        // user.setName("Leanne Graham");
        // user.setUsername("Bret");
        // user.setEmail("Sincere@april.biz");

        // when(userUseCase.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Leanne Graham")));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        // when(userUseCase.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }
}