package iff.poo.infra.database.repositories;

import iff.poo.core.city.CityModel;
import iff.poo.core.route.RouteModel;
import iff.poo.core.travel.TravelModel;
import iff.poo.core.travel.TravelRepo;
import iff.poo.core.vehicle.VehicleModel;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelRepoImpl extends TravelRepo {
    private static final Logger LOG = Logger.getLogger(TravelRepoImpl.class);

    private final AgroalDataSource mysqlDataSource;

    public TravelRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_TRAVEL = "SELECT t.id, t.start_date, t.end_date, t.status, v.id, v.license_plate, v.model, v.capacity, " +
            "v.last_maintenance, r.id, oc.id, oc.name, oc.uf, dc.id, dc.name, dc.uf, r.distance, r.base_price, rs.id, crs.id, crs.name, crs.uf, " +
            "rs.stop_order, rs.distance_from_origin FROM travel t LEFT JOIN vehicle v ON t.vehicle_id = v.id INNER JOIN route r ON t.route_id = r.id " +
            "INNER JOIN city oc ON r.origin_city_id = oc.id INNER JOIN city dc ON r.destiny_city_id = dc.id LEFT JOIN route_stop rs ON r.id = rs.route_id " +
            "LEFT JOIN city crs ON rs.city_id = crs.id {CONDITIONS} ORDER BY rs.stop_order";
    private static final String QUERY_INSERT_TRAVEL = "INSERT INTO travel (start_date, end_date, status, route_id, vehicle_id) VALUES (?, ?, ?, ?, ?)";
    private static final String QUERY_UPDATE_TRAVEL = "UPDATE travel SET start_date = ?, end_date = ?, status = ?, route_id = ?, vehicle_id = ? WHERE id = ?";
    private static final String SUB_QUERY_GET_TRAVEL_BY_CITIES = "SELECT DISTINCT t.id FROM travel t INNER JOIN route r ON t.route_id = r.id INNER JOIN city " +
            "c_origin ON r.origin_city_id = c_origin.id INNER JOIN city c_destiny ON r.destiny_city_id = c_destiny.id " +
            "LEFT JOIN route_stop rs_origin ON rs_origin.route_id = r.id LEFT JOIN route_stop rs_destiny ON rs_destiny.route_id = " +
            "r.id LEFT JOIN city cs_origin ON rs_origin.city_id = cs_origin.id LEFT JOIN city cs_destiny ON rs_destiny.city_id = " +
            "cs_destiny.id WHERE (c_origin.id = ? OR cs_origin.id = ?) AND (c_destiny.id = ? OR cs_destiny.id = ?) AND t.status = " +
            "'scheduled' AND t.start_date > NOW()";

    @Override
    public TravelModel getById(Long id) {
        var query = QUERY_GET_TRAVEL.replace("{CONDITIONS}", "WHERE t.id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            var travels = travelModelsFromResultSet(rs);
            rs.close();
            return !travels.isEmpty() ? travels.get(0) : null;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public List<TravelModel> getTravelByOriginAndDestinyCities(Long originCityId, Long destinyCityId) {
        var query = QUERY_GET_TRAVEL.replace("{CONDITIONS}", "WHERE t.id IN (".concat(SUB_QUERY_GET_TRAVEL_BY_CITIES).concat(")"));
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, originCityId);
            ps.setLong(2, originCityId);
            ps.setLong(3, destinyCityId);
            ps.setLong(4, destinyCityId);
            var rs = ps.executeQuery();
            var travels = travelModelsFromResultSet(rs);
            rs.close();
            return travels;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public Long create(TravelModel travel) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_TRAVEL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(travel.getStartDate()));
            ps.setTimestamp(2, Timestamp.valueOf(travel.getEndDate()));
            ps.setString(3, travel.getStatus());
            ps.setLong(4, travel.getRoute().getId());
            ps.setLong(5, travel.getVehicle().getId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            Long generatedId = rs.getLong(1);
            rs.close();
            return generatedId;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public List<TravelModel> getAll() {
        var query = QUERY_GET_TRAVEL.replace("{CONDITIONS}", "");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            var rs = ps.executeQuery();
            var travels = travelModelsFromResultSet(rs);
            rs.close();
            return travels;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public boolean updateById(TravelModel travel, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_TRAVEL)) {
            ps.setTimestamp(1, Timestamp.valueOf(travel.getStartDate()));
            ps.setTimestamp(2, Timestamp.valueOf(travel.getEndDate()));
            ps.setString(3, travel.getStatus());
            ps.setLong(4, travel.getRoute().getId());
            ps.setLong(5, travel.getVehicle().getId());
            ps.setLong(6, id);
            return ps.execute();
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return false;
    }

    private List<TravelModel> travelModelsFromResultSet(ResultSet rs) throws SQLException {
        List<TravelModel> travels = new ArrayList<>();
        Map<Long, Integer> obtainedTravels = new HashMap<>();
        while (rs.next()) {
            var id = rs.getLong(1);
            TravelModel travel = null;
            var obtainedTravelIdx = obtainedTravels.getOrDefault(id, null);
            if(obtainedTravelIdx != null) {
                travel = travels.get(obtainedTravelIdx);
            } else {
                travel = new TravelModel();
                travel.setId(id);
                travel.setStartDate(rs.getTimestamp(2).toLocalDateTime());
                travel.setEndDate(rs.getTimestamp(3).toLocalDateTime());
                travel.setStatus(rs.getString(4));

                VehicleModel vehicle = new VehicleModel();
                vehicle.setId(rs.getLong(5));
                vehicle.setLicensePlate(rs.getString(6));
                vehicle.setModel(rs.getString(7));
                vehicle.setCapacity(rs.getInt(8));
                vehicle.setLastMaintenance(rs.getDate(9).toLocalDate());
                travel.setVehicle(vehicle);

                var route = new RouteModel();
                route.setId(rs.getLong(10));
                var originCity = new CityModel();
                var destinyCity = new CityModel();
                originCity.setId(rs.getLong(11));
                originCity.setName(rs.getString(12));
                originCity.setUf(rs.getString(13));
                destinyCity.setId(rs.getLong(14));
                destinyCity.setName(rs.getString(15));
                destinyCity.setUf(rs.getString(16));
                route.setOriginCity(originCity);
                route.setDestinyCity(destinyCity);
                route.setDistance(rs.getDouble(17));
                route.setBasePrice(rs.getDouble(18));
                route.setRouteStops(new ArrayList<>());
                travel.setRoute(route);

                travels.add(travel);
                obtainedTravels.put(id, travels.size() - 1);
            }
            var routeStopId = rs.getLong(19);
            if(routeStopId != 0) {
                var routeStop = new RouteModel.RouteStop();
                routeStop.setId(routeStopId);
                var routeStopCity = new CityModel();
                routeStopCity.setId(rs.getLong(20));
                routeStopCity.setName(rs.getString(21));
                routeStopCity.setUf(rs.getString(22));
                routeStop.setCity(routeStopCity);
                routeStop.setStopOrder(rs.getInt(23));
                routeStop.setDistanceFromOrigin(rs.getDouble(24));
                travel.getRoute().getRouteStops().add(routeStop);
            }
        }
        return travels;
    }
}
