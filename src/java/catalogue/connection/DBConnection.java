package catalogue.connection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;

/**
 * Adatbázis kapcsolatot létesítő osztály
 * @author Ludrik Balázs
 */
public class DBConnection {
    
    /**
     * Kapcsolat objektum
     */
    private Connection con = null;
    /**
     * Prepared Statement objektum
     */
    private PreparedStatement stmt = null;
    
    public Connection getCon() 
    {
        return con;
    }

    public PreparedStatement getStmt() {
        return stmt;
    }
    
    
    
    /**
     * Konstruktor
     */
    public DBConnection() 
    {
        con = openConnection();
        try {
            con.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Kapcsolatot létesít a használandó adatbázissal.
     * @return  Egy Connection objektumot ad vissza.
     */
    public Connection openConnection() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("jdbc/movieDB");

            if ( ds.isWrapperFor(OracleDataSource.class) ) {
                OracleDataSource ods = ds.unwrap(OracleDataSource.class);
                return ods.getConnection();
            }            
            
            return ds.getConnection();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;  
    }
    
    /**
     * Létrehozza és visszaadja a prepared statement objektumot.
     * @param stmt  szöveg melyből statementet kell készíteni
     * @return 
     */
    public PreparedStatement createStatement( String stmt ) {
        try {
            this.stmt = con.prepareStatement(stmt);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.stmt;
    }
    
    /**
     * Véglegesíti az adatbázison végrehajtott módosításokat.
     * @throws SQLException 
     */
    public void commit() throws SQLException {
        con.commit();
    }
    
    /**
     * Lezárja a nyitott adatbázis kapcsolatot.
     */
    public void closeConn() {
        try {
            if( con != null ) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}