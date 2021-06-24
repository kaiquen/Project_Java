package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Agenda;
import model.Funcionario;


public class AgendaDAO {
    Connection connection;
    public AgendaDAO(Connection  connection){
        this.connection = connection;
    }
    
    public void createAgenda(Agenda agenda) throws SQLException{
        String sql = "insert into agenda (idmedico, idpaciente, datetime) values (?, ?, ?)"; 
        PreparedStatement ps = connection.prepareStatement(sql);
      
        ps.setInt(1, agenda.getIdMedico());
        ps.setInt(2, agenda.getIdPaciente());
        ps.setTimestamp(3, agenda.getDateTime());
        ps.execute();
    }

    public List<Agenda> selectAgendas(LocalDate newValue) throws SQLException{

        String sql = "select p.name, a.datetime from agenda as a inner join paciente as p on p.id=a.idpaciente where a.idmedico=? and (cast(a.datetime as date)=?)"; 
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, Funcionario.idLogin);
        Date dateSQL = Date.valueOf(newValue);
        ps.setDate(2, dateSQL);
        System.out.println(Funcionario.idLogin);
        ResultSet  rs = ps.executeQuery();

        List<Agenda> agendas = new ArrayList<>();
        while(rs.next()){
            Agenda agenda = new Agenda();
            agenda.setNamePaciente(rs.getString("name"));
            agenda.setDateTime(rs.getTimestamp("datetime"));
            agendas.add(agenda);
        }
        return agendas;
    }

    public Integer selectIdMedico(Agenda agenda) throws SQLException{
        String sql = "select id from funcionario where name=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, agenda.getNameMedico());
     
        ResultSet rs = ps.executeQuery();
        rs.next();

        return rs.getInt("id");
    }
    public ArrayList selectYear() throws SQLException{
        String sql = "select distinct extract(year from datetime) as ano from agenda";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        ArrayList agendas = new ArrayList<>();

        while(rs.next()){
            agendas.add(rs.getInt("ano"));
        }
        return agendas;
    }
    public Map<Integer, ArrayList> graphic(Integer date) throws SQLException{
        String sql = "select count(id), extract(year from datetime)as ano, extract(month from datetime) as mes from agenda where extract(year from datetime)=? group by ano,mes order by ano,mes";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,date);
        
        Map<Integer, ArrayList> graphic = new HashMap<>();
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            ArrayList rows = new ArrayList<>();
            if(!graphic.containsKey(rs.getInt("mes"))){
                rows.add(rs.getInt("mes"));
                rows.add(rs.getInt("count"));
                graphic.put(rs.getInt("mes"), rows);
            }else {
                ArrayList newRows = graphic.get(rs.getInt("ano"));
                newRows.add(rs.getInt("mes"));
                newRows.add(rs.getInt("count"));
            }
        }
        return graphic;
    }
    public List<Agenda> selectRelatorios() throws SQLException{
        String sql = "select a.id,  m.name as medico, p.name as paciente, a.datetime from agenda as a inner join funcionario as m on m.id=a.idMedico inner join paciente as p on p.id=a.idPaciente order by datetime"; 
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet  rs = ps.executeQuery();
        
        List<Agenda> agendas = new ArrayList<>();
        while(rs.next()){
            Agenda agenda = new Agenda();
            agenda.setId(rs.getInt("id"));
            agenda.setNameMedico(rs.getString("medico"));
            agenda.setNamePaciente(rs.getString("paciente"));
            agenda.setDateTime(rs.getTimestamp("datetime"));
            agendas.add(agenda);
        }
        return agendas;
    }
}
