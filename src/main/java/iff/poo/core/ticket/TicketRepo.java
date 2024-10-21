package iff.poo.core.ticket;

import iff.poo.core.BaseRepository;

import java.util.List;

public abstract class TicketRepo implements BaseRepository<TicketModel> {
    public List<TicketModel> getTicketsByUserId(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<TicketModel> getTicketsByTravelId(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
