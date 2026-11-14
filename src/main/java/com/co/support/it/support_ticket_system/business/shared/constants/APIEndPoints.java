package com.co.support.it.support_ticket_system.business.shared.constants;

/**
 * Rutas versionadas de la API.
 */
public final class APIEndPoints {

    public static final String API_VERSION = "v1";
    public static final String API_BASE = "/api/" + API_VERSION;

    public static final String CATEGORIES = API_BASE + "/categories";
    public static final String REQUESTERS = API_BASE + "/requesters";
    public static final String SUPPORT_AGENTS = API_BASE + "/support-agents";
    public static final String TICKETS = API_BASE + "/tickets";
    public static final String TICKET_COMMENTS = TICKETS + "/{ticketId}/comments";

    public static final String BY_ID = "/{id}";
    public static final String TICKET_STATUS = "/{id}/status";
    public static final String TICKET_STATUS_HISTORY = "/{id}/status-history";

    private APIEndPoints() {
    }
}
