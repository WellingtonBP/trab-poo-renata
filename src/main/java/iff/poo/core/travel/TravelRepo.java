package iff.poo.core.travel;

import iff.poo.core.BaseRepository;

import java.util.List;

public abstract class TravelRepo implements BaseRepository<TravelModel> {
    public List<TravelModel> getTravelByOriginAndDestinyCities(Long originCityId, Long destinyCityId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
