package com.co.support.it.support_ticket_system.integration.asset;

import java.math.BigDecimal;

/**
 * Activo de TI tal como lo expone esta API, independiente del formato del proveedor.
 */
public record AssetResponseDto(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal referencePrice
) {
}
