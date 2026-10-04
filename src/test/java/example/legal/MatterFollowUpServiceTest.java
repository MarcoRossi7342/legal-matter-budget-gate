package example.legal;

import java.io.IOException;

public final class MatterFollowUpServiceTest {
    public static void main(String[] args) throws Exception {
        MatterFollowUpService service = new MatterFollowUpService(null, 1.0);
        MatterFollowUpService.Decision d = service.followUp(new MatterFollowUpService.Matter("Intake", "signed", "2026-10-01"), 9.0, 8.5);
        if (!"BLOCKED".equals(d.status())) throw new AssertionError("Expected BLOCKED");
        InfraiClient.validateResponse("/chat/completions", 200, "{\"choices\":[]}");
        try {
            InfraiClient.validateResponse("/chat/completions", 400, "{\"error\":\"bad request\"}");
            throw new AssertionError("Expected chat error to be rejected");
        } catch (IOException expected) {
            // Non-successful chat responses must still fail.
        }
        try {
            InfraiClient.validateResponse("/account/usage/timeseries", 200, "{\"data\":{}}");
            throw new AssertionError("Expected account response without ok to be rejected");
        } catch (IOException expected) {
            // Account endpoints retain their envelope validation.
        }
        System.out.println("business decision test passed");
    }
}
