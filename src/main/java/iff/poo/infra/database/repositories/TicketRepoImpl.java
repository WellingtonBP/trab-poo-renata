package iff.poo.infra.database.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iff.poo.core.ticket.TicketModel;
import iff.poo.core.ticket.TicketRepo;
import io.agroal.api.AgroalDataSource;
import org.jboss.logging.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketRepoImpl extends TicketRepo {
    private static final Logger LOG = Logger.getLogger(TicketRepoImpl.class);

    private final AgroalDataSource mysqlDataSource;

    public TicketRepoImpl(AgroalDataSource dataSource) {
        this.mysqlDataSource = dataSource;
    }

    private static final String QUERY_GET_TICKET = "SELECT t.id, t.travel_id, t.origin_route_stop_id, t.destiny_route_stop_id, " +
            "t.user_id, t.seat_number, t.payment_id, p.type, p.status, p.meta FROM ticket t LEFT JOIN payment p ON t.payment_id = p.id";
    private static final String QUERY_INSERT_TICKET = "INSERT INTO ticket (travel_id, origin_route_stop_id, destiny_route_stop_id, user_id, " +
            "seat_number, payment_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String QUERY_INSERT_PAYMENT = "INSERT INTO payment (type, status, meta, user_id) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_TICKET_BY_ID = "UPDATE ticket SET travel_id = ?, origin_route_stop_id = ?, destiny_route_stop_id = ? " +
            "seat_number = ? WHERE id = ?";
    private static final String QUERY_UPDATE_PAYMENT_BY_ID = "UPDATE payment SET type = ?, status = ?, meta = ? WHERE id = ?";

    @Override
    public TicketModel getById(Long id) {
        String query = QUERY_GET_TICKET.concat(" WHERE t.id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            TicketModel ticket = null;
            if(rs.next()) {
                ticket = ticketModelFromResultSet(rs);
            }
            rs.close();
            return ticket;
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public List<TicketModel> getTicketsByUserId(Long id) {
        String query = QUERY_GET_TICKET.concat(" WHERE t.user_id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            List<TicketModel> tickets = new ArrayList<>();
            while (rs.next()) {
                tickets.add(ticketModelFromResultSet(rs));
            }
            rs.close();
            return tickets;
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public List<TicketModel> getTicketsByTravelId(Long id) {
        String query = QUERY_GET_TICKET.concat(" WHERE t.travel_id = ?");
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            var rs = ps.executeQuery();
            List<TicketModel> tickets = new ArrayList<>();
            while (rs.next()) {
                tickets.add(ticketModelFromResultSet(rs));
            }
            rs.close();
            return tickets;
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public Long create(TicketModel ticket) {
        try {
            Long paymentId = createPayment(ticket.getPayment(), ticket.getUserId());
            try(var conn = mysqlDataSource.getConnection();
                var ps = conn.prepareStatement(QUERY_INSERT_TICKET, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, ticket.getTravelId());
                ps.setObject(2, ticket.getOriginRouteStopId());
                ps.setObject(3, ticket.getDestinyRouteStopId());
                ps.setLong(4, ticket.getUserId());
                ps.setInt(5, ticket.getSeatNumber());
                ps.setLong(6, paymentId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                Long generatedId = rs.getLong(1);
                rs.close();
                return generatedId;
            }
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public List<TicketModel> getAll() {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_GET_TICKET)) {
            var rs = ps.executeQuery();
            List<TicketModel> tickets = new ArrayList<>();
            while (rs.next()) {
                tickets.add(ticketModelFromResultSet(rs));
            }
            rs.close();
            return tickets;
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return null;
    }

    @Override
    public boolean updateById(TicketModel ticket, Long id) {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_TICKET_BY_ID)) {
            ps.setLong(1, ticket.getTravelId());
            ps.setObject(2, ticket.getOriginRouteStopId());
            ps.setObject(3, ticket.getDestinyRouteStopId());
            ps.setInt(4, ticket.getSeatNumber());
            ps.setLong(5, ticket.getId());
            updatePayment(ticket.getPayment());
            return ps.execute();
        } catch (SQLException | JsonProcessingException ex) {
            LOG.error(ex);
        }
        return false;
    }

    private Long createPayment(TicketModel.PaymentModel payment, Long userId) throws SQLException, JsonProcessingException {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_INSERT_PAYMENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getType());
            ps.setString(2, payment.getStatus());
            ps.setString(3, deserializePaymentMeta(payment.getMeta()));
            ps.setLong(4, userId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            Long generatedId = rs.getLong(1);
            rs.close();
            return generatedId;
        }
    }

    private boolean updatePayment(TicketModel.PaymentModel payment) throws SQLException, JsonProcessingException {
        try(var conn = mysqlDataSource.getConnection();
            var ps = conn.prepareStatement(QUERY_UPDATE_PAYMENT_BY_ID)) {
            ps.setString(1, payment.getType());
            ps.setString(2, payment.getStatus());
            ps.setString(3, deserializePaymentMeta(payment.getMeta()));
            ps.setLong(4, payment.getId());
            return ps.execute();
        }
    }

    private TicketModel ticketModelFromResultSet(ResultSet rs) throws SQLException, JsonProcessingException {
        var ticket = new TicketModel();
        ticket.setId(rs.getLong(1));
        ticket.setTravelId(rs.getLong(2));
        ticket.setOriginRouteStopId(rs.getLong(3));
        ticket.setDestinyRouteStopId(rs.getLong(4));
        ticket.setUserId(rs.getLong(5));
        ticket.setSeatNumber(rs.getInt(6));
        var payment = new TicketModel.PaymentModel();
        payment.setId(rs.getLong(7));
        payment.setType(rs.getString(8));
        payment.setStatus(rs.getString(10));
        payment.setMeta(serializePaymentMeta(rs.getString(10)));
        ticket.setPayment(payment);
        return ticket;
    }

    private Map<String, Object> serializePaymentMeta(String meta) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(meta, new TypeReference<Map<String, Object>>() {});
    }

    private String deserializePaymentMeta(Map<String, Object> meta) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(meta);
    }
}
