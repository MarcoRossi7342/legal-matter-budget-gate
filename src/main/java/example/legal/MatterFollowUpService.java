package example.legal;

import java.io.IOException;

public final class MatterFollowUpService {
    public record Matter(String summary, String signedDocumentUrl, String deadline) {}
    public record Decision(String status, String note) {}
    private final InfraiClient client;
    private final double estimateUsd;

    public MatterFollowUpService(InfraiClient client, double estimateUsd) { this.client = client; this.estimateUsd = estimateUsd; }

    public Decision followUp(Matter matter, double hardCapUsd, double usedUsd) throws IOException, InterruptedException {
        if (usedUsd + estimateUsd > hardCapUsd) return new Decision("BLOCKED", "Budget cap would be crossed");
        String body = "{\"model\":\"auto\",\"messages\":[{\"role\":\"user\",\"content\":\"Prepare a deadline follow-up for: " + matter.summary() + " due " + matter.deadline() + "\"}]}";
        String result = client.request("POST", "/chat/completions", body);
        return new Decision("SENT", result);
    }
}
