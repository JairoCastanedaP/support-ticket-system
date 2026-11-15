package com.co.support.it.support_ticket_system.integration.asset;

import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;

public class AssetNotFoundException extends NotFoundException {

    public AssetNotFoundException(Long id) {
        super("No se encontró el activo de TI con id: " + id);
    }
}
