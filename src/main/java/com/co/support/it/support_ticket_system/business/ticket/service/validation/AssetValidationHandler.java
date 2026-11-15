package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.integration.asset.AssetCatalogClient;
import com.co.support.it.support_ticket_system.integration.asset.AssetNotFoundException;
import org.springframework.stereotype.Component;

/**
 * El activo de TI es opcional; si viene, debe existir en el catálogo externo (404).
 * Si el catálogo no responde, la operación falla con 503.
 */
@Component
public class AssetValidationHandler extends TicketValidationHandler {

    private final AssetCatalogClient assetCatalogClient;

    public AssetValidationHandler(AssetCatalogClient assetCatalogClient) {
        this.assetCatalogClient = assetCatalogClient;
    }

    @Override
    protected void check(TicketValidationContext context) {
        Long assetId = context.getRequest().assetId();
        if (assetId == null) {
            return;
        }

        assetCatalogClient.findById(assetId)
                .orElseThrow(() -> new AssetNotFoundException(assetId));
    }
}
