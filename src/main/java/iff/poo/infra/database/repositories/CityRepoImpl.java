package iff.poo.infra.database.repositories;

import iff.poo.core.city.CityModel;
import iff.poo.core.city.CityRepo;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CityRepoImpl extends CityRepo {
    private static final Logger LOG = Logger.getLogger(CityRepoImpl.class);

    private final AgroalDataSource mysqlDataSource;

    public CityRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_CITY = "SELECT id, name, uf FROM city";
    private static final String QUERY_INSERT_CITY = "INSERT INTO city (name, uf) VALUES (?, ?)";
    private static final String QUERY_UPDATE_CITY_BY_ID = "UPDATE city SET name = ?, uf = ? WHERE id = ?";

    @Override
    public CityModel getById(Long id) {
        var query = QUERY_GET_CITY.concat(" WHERE id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            CityModel city = null;
            if(rs.next()) {
                city =  cityModelFromResultSet(rs);
            }
            rs.close();
            return city;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public Long create(CityModel city) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_CITY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, city.getName());
            ps.setString(2, city.getUf());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            Long generatedId = null;
            if(rs.next()) {
                generatedId = rs.getLong(1);
            }
            rs.close();
            return generatedId;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public List<CityModel> getAll() {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_GET_CITY)) {
            var rs = ps.executeQuery();
            List<CityModel> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(cityModelFromResultSet(rs));
            }
            rs.close();
            return cities;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public boolean updateById(CityModel city, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_CITY_BY_ID)) {
            ps.setString(1, city.getName());
            ps.setString(2, city.getUf());
            ps.setLong(3, id);
            return ps.execute();
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return false;
    }

    private CityModel cityModelFromResultSet(ResultSet rs) throws SQLException {
        var city = new CityModel();
        city.setId(rs.getLong(1));
        city.setName(rs.getString(2));
        city.setUf(rs.getString(3));
        return city;
    }
}
