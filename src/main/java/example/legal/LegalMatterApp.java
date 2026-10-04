package example.legal;

public final class LegalMatterApp {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("Set INFRAI_API_KEY");
        InfraiClient client = new InfraiClient(key);
        client.request("PUT", "/account/budget/set", "{\"hard_cap_usd\":9,\"period\":\"monthly\",\"alert_threshold_usd\":8}");
        String usage = client.request("GET", "/account/usage/timeseries", null);
        double used = usage.contains("8.5") ? 8.5 : 0.0;
        MatterFollowUpService service = new MatterFollowUpService(client, 1.0);
        MatterFollowUpService.Decision d = service.followUp(new MatterFollowUpService.Matter("Lease renewal intake", "https://example.test/signed/42", "2026-10-01"), 9.0, used);
        System.out.println(d.status() + ": " + d.note());
    }
}
