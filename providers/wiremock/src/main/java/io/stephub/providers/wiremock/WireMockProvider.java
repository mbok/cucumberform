package io.stephub.providers.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import io.stephub.json.JsonString;
import io.stephub.provider.StepResponse;
import io.stephub.providers.util.LocalProviderAdapter;
import io.stephub.providers.util.spring.SpringBeanProvider;
import io.stephub.providers.util.spring.StepMethodAnnotationProcessor.StepArgument;
import io.stephub.providers.util.spring.StepMethodAnnotationProcessor.StepMethod;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
public class WireMockProvider extends SpringBeanProvider<WireMockState> {
    @Override
    protected WireMockState startState(final String sessionId, final ProviderOptions options) {
        final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        log.debug("Started for sessionId={} local WirMock server on port={}", sessionId, wireMockServer.port());
        return WireMockState.builder().wireMockServer(wireMockServer).build();
    }

    @Override
    protected void stopState(final WireMockState state) {
        state.getWireMockServer().stop();
        log.debug("Stopped for sessionId={} local WirMock server on port={}", state.getSessionId(), state.getWireMockServer().port());
    }

    @StepMethod(pattern = "I mock a GET request to {string:url}")
    public static StepResponse mockSomething(@StepArgument(name = "url") final JsonString url) {
        return null;
    }

    @Override
    public String getName() {
        return "wiremock";
    }
}

@SuperBuilder
@Data
class WireMockState extends LocalProviderAdapter.SessionState {
    private WireMockServer wireMockServer;
}