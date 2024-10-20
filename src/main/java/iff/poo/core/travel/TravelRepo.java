package iff.poo.core.travel;

import iff.poo.core.BaseRepository;

public abstract class TravelRepo implements BaseRepository<TravelModel> {
    public TravelModel getTravelByOriginAndDestinyCities(Long originCityId, Long destinyCityId) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
