package iff.poo.infra.database.repositories;

import iff.poo.core.city.CityModel;
import iff.poo.core.route.RouteModel;
import iff.poo.core.route.RouteRepo;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteRepoImpl extends RouteRepo {
    private static final Logger LOG = Logger.getLogger(RouteRepoImpl.class);

    private final AgroalDataSource mysqlDataSource;

    public RouteRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_ROUTE = "SELECT r.id, oc.id, oc.name, oc.uf, dc.id, dc.name, dc.uf, r.distance, " +
        "r.base_price, rs.id, crs.id, crs.name, crs.uf, rs.stop_order, rs.distance_from_origin FROM route r INNER JOIN city oc " +
        "ON r.origin_city_id = oc.id INNER JOIN city dc ON r.destiny_city_id = dc.id LEFT JOIN route_stop rs ON r.id = rs.route_id " +
        "LEFT JOIN city crs ON rs.city_id = crs.id ORDER BY rs.stop_order";
    private static final String QUERY_INSERT_ROUTE = "INSERT INTO route (origin_city_id, destiny_city_id, distance, base_price) VALUES (?, ?, ?, ?)";
    private static final String QUERY_INSERT_ROUTE_STOP = "INSERT INTO route_stop (route_id, city_id, stop_order, distance_from_origin) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_ROUTE = "UPDATE route SET origin_city_id = ?, destiny_city_id = ?, distance = ?, base_price = ? WHERE id = ?";
    private static final String QUERY_DELETE_ROUTE_STOP = "DELETE FROM route_stop";

    @Override
    public RouteModel getById(Long id) {
        var query = QUERY_GET_ROUTE.concat(" WHERE r.id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            var routes = routeModelsFromResultSet(rs);
            rs.close();
            return !routes.isEmpty() ? routes.get(0) : null;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public Long create(RouteModel route) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_ROUTE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, route.getOriginCity().getId());
            ps.setLong(2, route.getDestinyCity().getId());
            ps.setDouble(3, route.getDistance());
            ps.setDouble(4, route.getBasePrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            Long generatedId = rs.getLong(1);
            rs.close();
            createRouteStops(route.getRouteStops(), generatedId);
            return generatedId;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public List<RouteModel> getAll() {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_GET_ROUTE)) {
            var rs = ps.executeQuery();
            var routes = routeModelsFromResultSet(rs);
            rs.close();
            return routes;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public boolean updateById(RouteModel route, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_ROUTE)) {
            ps.setLong(1, route.getOriginCity().getId());
            ps.setLong(2, route.getDestinyCity().getId());
            ps.setDouble(3, route.getDistance());
            ps.setDouble(4, route.getBasePrice());
            ps.setLong(5, id);
            deleteRouteStopsByRouteId(id);
            createRouteStops(route.getRouteStops(), id);
            return ps.execute();
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return false;
    }

    private void deleteRouteStopsByRouteId(Long routeId) throws SQLException {
        var query = QUERY_DELETE_ROUTE_STOP.concat(" WHERE route_id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, routeId);
            ps.executeUpdate();
        }
    }

    private void createRouteStops(List<RouteModel.RouteStop> routeStops, Long routeId) throws SQLException {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_ROUTE_STOP)) {
            for(var routeStop : routeStops) {
                ps.setLong(1, routeId);
                ps.setLong(2, routeStop.getCity().getId());
                ps.setInt(3, routeStop.getStopOrder());
                ps.setDouble(4, routeStop.getDistanceFromOrigin());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        }
    }

    private List<RouteModel> routeModelsFromResultSet(ResultSet rs) throws SQLException {
        List<RouteModel> routes = new ArrayList<>();
        Map<Long, Integer> obtainedRoutes = new HashMap<>();
        while (rs.next()) {
            var id = rs.getLong(1);
            RouteModel route = null;
            var obtainedRouteIdx = obtainedRoutes.getOrDefault(id, null);
            if(obtainedRouteIdx != null) {
                route = routes.get(obtainedRouteIdx);
            } else {
                route = new RouteModel();
                route.setId(id);
                routes.add(route);
                obtainedRoutes.put(id, routes.size() - 1);
                var originCity = new CityModel();
                var destinyCity = new CityModel();
                originCity.setId(rs.getLong(2));
                originCity.setName(rs.getString(3));
                originCity.setUf(rs.getString(4));
                destinyCity.setId(rs.getLong(5));
                destinyCity.setName(rs.getString(6));
                destinyCity.setUf(rs.getString(7));
                route.setOriginCity(originCity);
                route.setDestinyCity(destinyCity);
                route.setDistance(rs.getDouble(8));
                route.setBasePrice(rs.getDouble(9));
                route.setRouteStops(new ArrayList<>());
            }
            var routeStopId = rs.getLong(10);
            if(routeStopId != 0) {
                var routeStop = new RouteModel.RouteStop();
                routeStop.setId(routeStopId);
                var routeStopCity = new CityModel();
                routeStopCity.setId(rs.getLong(11));
                routeStopCity.setName(rs.getString(12));
                routeStopCity.setUf(rs.getString(13));
                routeStop.setCity(routeStopCity);
                routeStop.setStopOrder(rs.getInt(14));
                routeStop.setDistanceFromOrigin(rs.getDouble(15));
                route.getRouteStops().add(routeStop);
            }
        }
        return routes;
    }
}
