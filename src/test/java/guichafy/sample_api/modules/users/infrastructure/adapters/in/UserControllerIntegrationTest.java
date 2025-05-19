package guichafy.sample_api.modules.users.infrastructure.adapters.in;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockRuntimeInfo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

@SpringBootTest(properties = {
        "external.api.jsonplaceholder.base-url=http://localhost:${wiremock.server.port}"
})
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static WireMockRuntimeInfo wireMockInfo;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("external.api.jsonplaceholder.base-url", () -> wireMockInfo.getHttpBaseUrl());
    }

    private String readResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp(WireMockRuntimeInfo info) throws Exception {
        wireMockInfo = info;
        WireMock wireMock = info.getWireMock();
        wireMock.resetAll();

        wireMock.stubFor(get(urlEqualTo("/users"))
                .willReturn(okJson(readResource("json/users.json"))));

        wireMock.stubFor(get(urlEqualTo("/users/1"))
                .willReturn(okJson(readResource("json/user_1.json"))));

        wireMock.stubFor(get(urlEqualTo("/users/99"))
                .willReturn(notFound()));
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data._embedded.userResponseList", hasSize(10)))
                .andExpect(jsonPath("$.data._embedded.userResponseList[0].id", is(1)))
                .andExpect(jsonPath("$.data._embedded.userResponseList[0].name", is("Leanne Graham")))
                .andExpect(jsonPath("$.data._embedded.userResponseList[1].id", is(2)))
                .andExpect(jsonPath("$.data._embedded.userResponseList[1].name", is("Ervin Howell")));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Leanne Graham")));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }
}
