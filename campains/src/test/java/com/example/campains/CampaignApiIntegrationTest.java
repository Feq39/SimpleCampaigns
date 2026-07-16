package com.example.campains;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CampaignApiIntegrationTest {

    private static final String SELLER = "seller_one";
    private static final String PRODUCT = "Gaming Laptop";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndReturnCampaign() throws Exception {
        createCampaign("summer-sale", "100.00")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("summer-sale"))
                .andExpect(jsonPath("$.productDto.name").value(PRODUCT))
                .andExpect(jsonPath("$.fund").value(100.0));

        mockMvc.perform(get(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "summer-sale"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("summer-sale"))
                .andExpect(jsonPath("$.town.name").value("Warszawa"));

        expectSellerBalance(900.0);
    }

    @Test
    void shouldUpdateCampaign() throws Exception {
        createCampaign("summer-sale", "100.00")
                .andExpect(status().isCreated());

        mockMvc.perform(put(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "summer-sale"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(campaignRequest("updated-sale", "250.00")))
                .andExpect(status().isOk());

        mockMvc.perform(get(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "updated-sale"
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated-sale"))
                .andExpect(jsonPath("$.fund").value(250.0));

        expectSellerBalance(750.0);
    }

    @Test
    void shouldDeleteCampaignAndReturnFunds() throws Exception {
        createCampaign("summer-sale", "100.00")
                .andExpect(status().isCreated());

        mockMvc.perform(delete(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "summer-sale"
                ))
                .andExpect(status().isOk());

        mockMvc.perform(get(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "summer-sale"
                ))
                .andExpect(status().isNotFound());

        expectSellerBalance(1000.0);
    }

    @Test
    void shouldRejectCampaignWhenFundsAreInsufficient() throws Exception {
        createCampaign("too-expensive", "1000.01")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));

        expectSellerBalance(1000.0);
    }

    @Test
    void shouldReturnNotFoundForMissingCampaign() throws Exception {
        mockMvc.perform(get(
                        "/api/v1/campaigns/{sellerName}/{productName}/{campaignName}",
                        SELLER,
                        PRODUCT,
                        "missing-campaign"
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void shouldReturnReferenceData() throws Exception {
        mockMvc.perform(get("/api/keywords/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].value", hasItems("electronics", "sports")))
                .andExpect(jsonPath("$..id").doesNotExist());

        mockMvc.perform(get("/api/towns/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItems("Warszawa", "Kraków")))
                .andExpect(jsonPath("$..id").doesNotExist());

        mockMvc.perform(get("/api/v1/sellers/{sellerName}", SELLER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(SELLER))
                .andExpect(jsonPath("$.products[*].name", hasItems(PRODUCT)))
                .andExpect(jsonPath("$..id").doesNotExist());
    }

    @Test
    void shouldReturnKeywordTypeaheadSuggestions() throws Exception {
        mockMvc.perform(get("/api/keywords/v1/typeahead")
                        .param("prefix", "ELE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value")
                        .value("electronics"))
                .andExpect(jsonPath("$..id").doesNotExist());
    }

    @Test
    void shouldReturnEmptyListForBlankTypeaheadPrefix() throws Exception {
        mockMvc.perform(get("/api/keywords/v1/typeahead")
                        .param("prefix", " "))
                .andExpect(status().isBadRequest());
    }
    private ResultActions createCampaign(
            String name,
            String fund
    ) throws Exception {
        return mockMvc.perform(post(
                        "/api/v1/campaigns/{sellerName}/{productName}",
                        SELLER,
                        PRODUCT
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(campaignRequest(name, fund)));
    }

    private void expectSellerBalance(double expectedBalance) throws Exception {
        mockMvc.perform(get("/api/v1/sellers/{sellerName}", SELLER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountBalance").value(expectedBalance));
    }

    private String campaignRequest(String name, String fund) {
        return """
                {
                  "name": "%s",
                  "keywords": ["electronics", "sports"],
                  "bidAmount": 1.50,
                  "fund": %s,
                  "status": "ON",
                  "town": "Warszawa",
                  "radiusKm": 25
                }
                """.formatted(name, fund);
    }
}
