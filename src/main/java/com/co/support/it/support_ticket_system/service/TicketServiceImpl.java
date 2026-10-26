package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService{

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository){
        this.ticketRepository= ticketRepository;
    }

    @Override
    public List<TicketEntity> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<TicketEntity> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public TicketEntity save(TicketEntity ticketEntity) {
        ticketEntity.setId(null);
        return ticketRepository.save(ticketEntity);
    }

    @Override
    public Optional<TicketEntity> update(Long id, TicketEntity updatedTicket) {

        Optional<TicketEntity> optionalTicket= ticketRepository.findById(id);
        if(optionalTicket.isPresent()){
            TicketEntity existingTicket= optionalTicket.get();
            existingTicket.setTitle(updatedTicket.getTitle());
            existingTicket.setDescription(updatedTicket.getDescription());
            existingTicket.setRequesterName(updatedTicket.getRequesterName());
            existingTicket.setRequesterEmail(updatedTicket.getRequesterEmail());
            existingTicket.setPriority(updatedTicket.getPriority());
            existingTicket.setStatus(updatedTicket.getStatus());
            return Optional.of(ticketRepository.save(existingTicket));
        }
        else{
            return Optional.empty();
        }
    }
}
