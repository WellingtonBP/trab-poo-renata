package iff.poo.infra.database.repositories;

import iff.poo.core.vehicle.VehicleModel;
import iff.poo.core.vehicle.VehicleRepo;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepoImpl extends VehicleRepo {
    private static final Logger LOG = Logger.getLogger(VehicleRepoImpl.class);

    private final AgroalDataSource mysqlDataSource;

    public VehicleRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_VEHICLE = "SELECT id, license_plate, model, capacity, last_maintenance FROM vehicle";
    private static final String QUERY_INSERT_VEHICLE = "INSERT INTO vehicle (license_plate, model, capacity, last_maintenance) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_VEHICLE = "UPDATE vehicle SET license_plate = ?, model = ?, capacity = ?, last_maintenance = ? WHERE id = ?";

    @Override
    public VehicleModel getById(Long id) {
        var query = QUERY_GET_VEHICLE.concat(" WHERE id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            VehicleModel vehicle = null;
            if(rs.next()) {
                vehicle = vehicleFromResultSet(rs);
            }
            rs.close();
            return vehicle;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public Long create(VehicleModel vehicle) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_VEHICLE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getModel());
            ps.setInt(3, vehicle.getCapacity());
            ps.setDate(4, Date.valueOf(vehicle.getLastMaintenance()));
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
    public List<VehicleModel> getAll() {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_GET_VEHICLE)) {
            var rs = ps.executeQuery();
            List<VehicleModel> vehicles = new ArrayList<>();
            while (rs.next()) {
                vehicles.add(vehicleFromResultSet(rs));
            }
            rs.close();
            return vehicles;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public boolean updateById(VehicleModel vehicle, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_VEHICLE)) {
            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getModel());
            ps.setInt(3, vehicle.getCapacity());
            ps.setDate(4, Date.valueOf(vehicle.getLastMaintenance()));
            ps.setLong(5, vehicle.getId());
            return ps.execute();
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return false;
    }

    private VehicleModel vehicleFromResultSet(ResultSet rs) throws SQLException {
        var vehicle = new VehicleModel();
        vehicle.setId(rs.getLong(1));
        vehicle.setLicensePlate(rs.getString(2));
        vehicle.setModel(rs.getString(3));
        vehicle.setCapacity(rs.getInt(4));
        vehicle.setLastMaintenance(rs.getDate(5).toLocalDate());
        return vehicle;
    }
}
