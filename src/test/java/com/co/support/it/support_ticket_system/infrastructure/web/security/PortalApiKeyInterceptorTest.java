package com.co.support.it.support_ticket_system.infrastructure.web.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class PortalApiKeyInterceptorTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    private boolean call(String configuredKey, String headerValue, MockHttpServletResponse response) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/portal/tickets/TCK-2026-000001");
        if (headerValue != null) {
            request.addHeader(PortalApiKeyInterceptor.API_KEY_HEADER, headerValue);
        }
        return new PortalApiKeyInterceptor(configuredKey, jsonMapper).preHandle(request, response, new Object());
    }

    @Test
    void validKey_passes() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(call("clave-demo", "clave-demo", response)).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void missingKey_isUnauthorized() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(call("clave-demo", null, response)).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"status\":\"ERROR\"").contains("Falta el header");
    }

    @Test
    void wrongKey_isUnauthorized() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(call("clave-demo", "otra-clave", response)).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void withoutConfiguredKey_everythingIsRejected() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(call("", "", response)).isFalse();
        assertThat(call("", "cualquiera", new MockHttpServletResponse())).isFalse();
    }
}
