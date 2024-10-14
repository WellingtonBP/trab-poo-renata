package iff.poo.infra.database.repositories;

import iff.poo.core.user.UserModel;
import iff.poo.core.user.UserRepo;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl extends UserRepo {
    private static final Logger LOG = Logger.getLogger(UserRepoImpl.class);

    private AgroalDataSource mysqlDataSource;

    public UserRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_USER = "SELECT id, name, email, password_hash, role FROM user";
    private static final String QUERY_INSERT_USER = "INSERT INTO user (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_USER_BY_ID = "UPDATE user SET name = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";

    @Override
    public UserModel getById(int id) {
        var query = QUERY_GET_USER.concat(" WHERE id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            UserModel user = null;
            if(rs.next()) {
                user =  userModelFromResultSet(rs);
            }
            rs.close();
            return user;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public Long create(UserModel user) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword_hash());
            ps.setString(4, user.getType());
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
    public List<UserModel> getAll() {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_GET_USER)) {
            var rs = ps.executeQuery();
            List<UserModel> users = new ArrayList<>();
            while (rs.next()) {
                users.add(userModelFromResultSet(rs));
            }
            rs.close();
            return users;
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return null;
    }

    @Override
    public boolean updateById(UserModel user, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_USER_BY_ID)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword_hash());
            ps.setString(4, user.getType());
            ps.setLong(5, id);
            return ps.execute();
        } catch (SQLException sqlex) {
            LOG.error(sqlex);
        }
        return false;
    }

    private UserModel userModelFromResultSet(ResultSet rs) throws SQLException {
        var user = new UserModel();
        user.setId(rs.getLong(1));
        user.setName(rs.getString(2));
        user.setEmail(rs.getString(3));
        user.setPassword_hash(rs.getString(4));
        user.setType(rs.getString(5));
        return user;
    }
}
