package catalogue.endpoint;

import catalogue.connection.DBConnection;
import catalogue.entities.DirectorItem;
import catalogue.entities.GenreItem;
import catalogue.entities.MovieItem;
import catalogue.entities.MovieSubItem;
import catalogue.entities.ReleaseItem;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * A webszolgáltatásokat megvalósító osztály.
 * @author Ludrik Balázs
 */
@WebService(serviceName = "CatalogueService")
public class CatalogueService {

    /**
     * Összes film lekérdezése a katalógusból.
     * @return  MovieItem-ek listája
     */
    @WebMethod(operationName = "getMoviesService")
    public List<catalogue.entities.MovieSubItem> getMoviesService() {
        
        List<catalogue.entities.MovieSubItem> movieList = new LinkedList<catalogue.entities.MovieSubItem>();
        
        try {
            
            DBConnection db = new DBConnection();
            PreparedStatement pstmt = db.createStatement("SELECT id, title_hu, year, type FROM LUDRIKB.MOVIES ORDER BY title_hu ASC");
            ResultSet rs = pstmt.executeQuery();
            
            
            while ( rs.next() ) {
             
                //
                // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA 
                //
                MovieSubItem subItem = new MovieSubItem();
                subItem.setId(rs.getInt("ID"));
                subItem.setTitleHU(rs.getString("TITLE_HU"));
                subItem.setYear( rs.getShort("YEAR") );
                subItem.setType(rs.getString("TYPE"));
                
                //Movie bejegyzés hozzáadása listához
                movieList.add(subItem);
                                
            }
            
            db.commit();
            db.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);

        }
        
        return movieList;
    }

    /**
     * Új bejegyzést vesz fel a katalógusba
     * @param movie MovieItem objektum
     * @return      sikerességet igazoló válasz üzenet
     */
    @WebMethod(operationName = "insertMovieService")
    public String insertMovieService(@WebParam(name = "movie") catalogue.entities.MovieItem movie, @WebParam(name = "genreIdx") ArrayList<Integer> genreIdx, @WebParam(name = "directorIdx") ArrayList<Integer> directorIdx) {
        
        DBConnection dB = new DBConnection();
        int res = 0;
        int movieID = 1;
        try {
            PreparedStatement pstmt = dB.createStatement("SELECT  * FROM "
                    + "(SELECT  id + 1 AS gap FROM LUDRIKB.MOVIES d1 WHERE NOT EXISTS( "
                    + "SELECT  NULL FROM ludrikb.MOVIES d2 WHERE d2.id = d1.id + 1 ) ORDER BY id) "
                    + "WHERE rownum = 1");
            //visszaadja a first gap-et az id sorozatban
            ResultSet rs = pstmt.executeQuery();
            if( rs.next() ) {
                movieID = rs.getInt("gap");
                movie.setId(movieID);
            } else {
                //movie.setId(1);
            }
            pstmt = dB.createStatement("INSERT INTO ludrikb.movies(id, title_hu, title_orig, year, releaseid, type, imdb_url, amount) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, movieID);
            pstmt.setString(2, movie.getTitleHu());
            pstmt.setString(3, movie.getTitleOrig());
            pstmt.setInt(4, movie.getYear());
            pstmt.setInt(5, movie.getReleaseid());
            pstmt.setString(6, movie.getType());
            pstmt.setString(7, movie.getImdbUrl());
            pstmt.setInt(8, movie.getAmount());
            
            res = pstmt.executeUpdate();
//            dB.commit();
            
            //Műfajok hozzárendelése filmhez
            pstmt = dB.createStatement("INSERT INTO LUDRIKB.GENREOFMOVIE(movie_id, genre_id) VALUES(?, ?) ");
            pstmt.setInt(1, movieID);
            for( int i=0; i<genreIdx.size(); i++) {    
                pstmt.setInt(2, genreIdx.get(i));
                pstmt.executeUpdate();
            }
//            dB.commit();
            
            //Rendező(k) hozzárendelése filmhez
            pstmt = dB.createStatement("INSERT INTO LUDRIKB.DIRECTOROFMOVIE(movie_id, director_id) VALUES(?, ?) ");
            pstmt.setInt(1, movieID);
            for( int i=0; i<directorIdx.size(); i++) {    
                pstmt.setInt(2, directorIdx.get(i));
                pstmt.executeUpdate();
            }
            dB.commit();
            dB.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        }
        catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
        return ""+res;

    }

    /**
     * Új rendezőt vesz fel az katalógusba.
     * @param director  rendező objektum
     * @return          sikeresség visszaigazoló string
     */
    @WebMethod(operationName = "insertDirectorService")
    public String insertDirectorService(@WebParam(name = "director") DirectorItem director) {
        
        DBConnection db = null;
        PreparedStatement pstmt = null;
        int res = 0; 
        try {
            //Kapcsolat létrehozása
            db = new DBConnection();
            pstmt = db.createStatement("SELECT  * FROM "
                    + "(SELECT  id + 1 AS gap FROM LUDRIKB.DIRECTORS d1 WHERE NOT EXISTS( "
                    + "SELECT  NULL FROM ludrikb.DIRECTORS d2 WHERE d2.id = d1.id + 1 ) ORDER BY id) "
                    + "WHERE rownum = 1");
            //visszaadja a first gap-et az id sorozatban
            ResultSet rs = pstmt.executeQuery();
            if( rs.next() ) {
                director.setId(rs.getInt("gap"));
            } else {
                //director.setId(1));
            }
            pstmt = db.createStatement("INSERT INTO LUDRIKB.DIRECTORS(id,firstname,lastname,birth, birthplace) VALUES(?, ?, ?, ?, ?)");
            pstmt.setInt(1, director.getId());
            pstmt.setString(2, director.getFirstname());
            pstmt.setString(3, director.getLastname());
            pstmt.setDate(4, new Date(director.getBirth().getTime()) );
            pstmt.setString(5, director.getBirthplace());
            res = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        }
        catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
       
        return ""+res;
        
    }

    /**
     * Lekérdezi a meglévő rendező bejegyzéseket.
     * @return  rendező bejegyzések listája
     */
    @WebMethod(operationName = "getDirectorsService")
    public List<DirectorItem> getDirectorsService() {
        
        List<catalogue.entities.DirectorItem> directorList = new LinkedList<catalogue.entities.DirectorItem>();
        
        try {
            DBConnection db = new DBConnection();
            PreparedStatement pstmt = db.createStatement("SELECT * FROM LUDRIKB.DIRECTORS ORDER BY CONCAT(firstname,lastname) ASC ");
            ResultSet rs = pstmt.executeQuery();
//            db.commit();

            while ( rs.next() ) {
                
                //
                // DIRECTOR OBJEKTUM OSSZEÁLLÍTÁSA 
                //
                catalogue.entities.DirectorItem director = new catalogue.entities.DirectorItem();
                director.setId(rs.getInt("ID"));
                director.setFirstname( rs.getString("FIRSTNAME") );
                director.setLastname(rs.getString("LASTNAME"));
                director.setBirth( rs.getDate("BIRTH"));
                director.setBirthplace(rs.getString("BIRTHPLACE"));
                
                //DIRECTOR bejegyzés hozzáadása listához
                directorList.add(director);
                                
            }
            db.commit();
            db.closeConn();       
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return directorList;
       
    }
    
    
    /**
     * Visszaadja az összes katalógus által ismert kiadási típust.
     * @return  kiadási típusok listája
     */
    @WebMethod(operationName = "getReleasesService")
    public List<ReleaseItem> getReleasesService() {
        
        List<catalogue.entities.ReleaseItem> extrasList = new LinkedList<catalogue.entities.ReleaseItem>();
        
        try {
            DBConnection db = new DBConnection();
            PreparedStatement pstmt = db.createStatement("SELECT * FROM LUDRIKB.RELEASES ORDER BY name ASC");
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {

                //
                // RELEASEITEM OBJEKTUM ÖSSZEÁLLÍTÁSA
                //
                catalogue.entities.ReleaseItem release = new catalogue.entities.ReleaseItem();
                release.setId(rs.getInt("ID"));
                release.setName( rs.getString("NAME") );
                
                //Release bejegyzés hozzáadása listához
                extrasList.add(release);
                                
            }
            db.commit();
            db.closeConn();        
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);

        }
        
        return extrasList;
        
    }

    /**
     * Frissít egy adott film bejegyzést.
     * @param condition     a módosítandó bejegyzéshez tartozó kulcs objektum
     * @param modifiedItem    a friss bejegyzés objektum
     * @return 
     */
    @WebMethod(operationName = "updateMovieService")
    public String updateMovieService( @WebParam( name = "id" ) int id, @WebParam( name = "modifiedItem" ) MovieItem modifiedItem , @WebParam( name = "modifiedGenreIdx" ) ArrayList<Integer> modifiedGenreIdx, @WebParam( name = "modifiedDirectorIdx" ) ArrayList<Integer> modifiedDirectorIdx ) {
        
        DBConnection db;
        PreparedStatement pstmt;
        
        try {
            db = new DBConnection();
            pstmt = db.createStatement("UPDATE LUDRIKB.MOVIES SET title_hu=?, title_orig=?, year=?, releaseid=?, type=?, imdb_url=?, amount=? WHERE id = ? ");
            pstmt.setString(1, modifiedItem.getTitleHu());
            pstmt.setString(2, modifiedItem.getTitleOrig());
            pstmt.setInt(3, modifiedItem.getYear());
            pstmt.setInt(4, modifiedItem.getReleaseid());
            pstmt.setString(5, modifiedItem.getType());
            pstmt.setString(6, modifiedItem.getImdbUrl());
            pstmt.setInt(7, modifiedItem.getAmount());
            pstmt.setInt(8, id);
            pstmt.executeUpdate();
            
            //
            //Műfaj hozzárendelés a filmhez
            //
            pstmt = db.createStatement(" DELETE FROM LUDRIKB.GENREOFMOVIE WHERE movie_id=?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            pstmt = db.createStatement(" INSERT INTO LUDRIKB.GENREOFMOVIE(movie_id, genre_id) VALUES(?,?)");
            pstmt.setInt(1, id);
            for( int i=0; i<modifiedGenreIdx.size(); i++) {
                pstmt.setInt(2, modifiedGenreIdx.get(i));
                pstmt.executeUpdate();
            }
            
            //
            //Rendező hozzárendelése a filmhez
            //
            pstmt = db.createStatement(" DELETE FROM LUDRIKB.DIRECTOROFMOVIE WHERE movie_id=?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            
            pstmt = db.createStatement(" INSERT INTO LUDRIKB.DIRECTOROFMOVIE(movie_id, director_id) VALUES(?,?)");
            pstmt.setInt(1, id);
            for( int i=0; i<modifiedDirectorIdx.size(); i++) {
                pstmt.setInt(2, modifiedDirectorIdx.get(i));
                pstmt.executeUpdate();
            }
            db.commit();
            db.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
     
        return "1";
        
    }
   
    /**
     * Frissít egy rendező bejegyzést.
     * @param id        frissítendő rendező sorszáma
     * @param director  friss rendező objektum
     * @return          
     */
    @WebMethod(operationName = "updateDirectorService")
    public String updateDirectorService(@WebParam(name = "id") Integer id, @WebParam(name = "director") DirectorItem director) {
        
        DBConnection db = null;
        PreparedStatement pstmt = null;
        int res = 0;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("UPDATE LUDRIKB.DIRECTORS SET id=?, firstname=?, lastname=?, birth=?, birthplace=? WHERE id=?");
            pstmt.setInt(1, director.getId());
            pstmt.setString(2, director.getFirstname());
            pstmt.setString(3, director.getLastname());
            pstmt.setDate(4, new Date(director.getBirth().getTime()));
            pstmt.setString(5, director.getBirthplace());
            pstmt.setInt(6, id);
            
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch(NullPointerException ex) {
            return "0";
        }
                      
        try { 
            res = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
            return ""+res;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        }
            
    }

    /**
     * A paraméternek megfelelő című filmekkel tér vissza. Minden olyan film bejegyzést visszaad, amelyben megtalálható a paraméter részstringként.
     * @param title     az a részstring aminek szerepelnie kell a film címében
     * @return          MovieItem objektumok listája
     */
    @WebMethod(operationName = "getMoviesFilteredByTitleService")
    public List<catalogue.entities.MovieSubItem> getMoviesFilteredByTitleService(@WebParam(name = "title") String title) {
        
        List<catalogue.entities.MovieSubItem> movieList = new LinkedList<catalogue.entities.MovieSubItem>();
        
        DBConnection db;
        PreparedStatement pstmt;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id, title_hu, year, type FROM LUDRIKB.MOVIES WHERE LOWER(title_hu) LIKE CONCAT(CONCAT('%', ?),'%') OR LOWER(title_orig) LIKE CONCAT(CONCAT('%', ?),'%') ");
            pstmt.setString(1, title.toLowerCase());
            pstmt.setString(2, title.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
     
                //
                // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA
                //
                MovieSubItem subItem = new MovieSubItem();
                subItem.setId(rs.getInt("id"));
                subItem.setTitleHU(rs.getString("title_HU"));
                subItem.setYear( rs.getShort("year") );
                subItem.setType(rs.getString("type"));
                
                //Movie bejegyzés hozzáadása listához
                movieList.add(subItem);
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return movieList;
    }

    /**
     * A paraméterként kapott rendező filmjeivel tér vissza. Minden olyan film bejegyzést visszaad, amelynek rendezője nevében megtalálható a paraméter részstringként.
     * @param directorName      az a részstring aminek szerepelnie kell a rendező teljes nevében
     * @return                  MovieItem objektumok listája
     */
    @WebMethod(operationName = "getMoviesFilteredByDirectorService")
    public List<catalogue.entities.MovieSubItem> getMoviesFilteredByDirectorService(@WebParam(name = "directorName") String directorName) {
        
        DBConnection db;
        PreparedStatement pstmt;
        List<Integer> directorID = new ArrayList<Integer>();
        List<catalogue.entities.MovieSubItem> movieList = new LinkedList<catalogue.entities.MovieSubItem>();
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id FROM LUDRIKB.DIRECTORS WHERE CONCAT(CONCAT(LOWER(firstname),' '), LOWER(lastname)) LIKE CONCAT(CONCAT('%', ?),'%') OR CONCAT(CONCAT(LOWER(lastname),' '), LOWER(firstname)) LIKE CONCAT(CONCAT('%', ?),'%')");
            pstmt.setString(1, directorName.toLowerCase() );
            pstmt.setString(2, directorName.toLowerCase() );
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                directorID.add(rs.getInt("id"));                    
            }
            
            int count = directorID.size();
            
            if( count > 0 ) {
                //Ha van olyan rendező amire illeszkedik a felhasználó által keresett szöveg
                
                //
                // Rendezőkhöz tartozó filmek ID-jának meghatározása
                //
                
                StringBuilder query =  new StringBuilder("SELECT movie_id FROM LUDRIKB.DIRECTOROFMOVIE WHERE director_id IN ( ?");
                for( int i=1 ; i<count ; i++) {
                    query.append(", ?");
                }
                query.append(" )");
                
                //
                // STATEMENT ÖSSZEÁLLÍTÁSA ÉS KITÖLTÉSE
                //
                pstmt = db.createStatement(query.toString());
                for( int i= 1; i<= count ; i++) {
                    pstmt.setInt(i, directorID.get(i-1));
                }

                rs = pstmt.executeQuery();
//                db.commit();

                ArrayList<Integer> movieIdx = new ArrayList<Integer>();
                while ( rs.next() ) {
                    // MOVIE_ID elemek listához való hozzáadása
                    movieIdx.add(rs.getInt("movie_id"));
                                
                }//end of while
                
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
                
                //
                // QUERY ÖSSZEÁLLÍTÁSA
                //
                query =  new StringBuilder("SELECT id, title_hu, year, type FROM LUDRIKB.MOVIES WHERE id IN ( ?");
                for( int i=1 ; i<movieIdx.size() ; i++) {
                    query.append(", ?");
                }
                query.append(" )");
                
                //
                // STATEMENT ÖSSZEÁLLÍTÁSA ÉS KITÖLTÉSE
                //
                pstmt = db.createStatement(query.toString());
                for( int i= 1; i<= movieIdx.size() ; i++) {
                    pstmt.setInt(i, movieIdx.get(i-1));
                }

               
                rs = pstmt.executeQuery();
//                db.commit();

                while ( rs.next() ) {
                   
                    //
                    // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA
                    //
                    MovieSubItem subItem = new MovieSubItem();
                    subItem.setId(rs.getInt("id"));
                    subItem.setTitleHU(rs.getString("title_HU"));
                    subItem.setYear( rs.getShort("year") );
                    subItem.setType(rs.getString("type"));

                    //Movie bejegyzés hozzáadása listához
                     movieList.add(subItem);
                                
                }//end of while
            }//end of if
            db.commit();
            db.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally{  
            return movieList;
        }
    }

    /**
     * A paraméterként kapott műfajú filmekkel tér vissza. Minden olyan film bejegyzést visszaad, amelyben műfajai között megtalálható a paraméter részstringként.
     * @param genre     részstring, aminek szerepelnie kell a műfajok között
     * @return          MovieItem objektumok listája 
     */
    @WebMethod(operationName = "getMoviesFilteredByGenreService")
    public List<catalogue.entities.MovieSubItem> getMoviesFilteredByGenreService(@WebParam(name = "genre") String genre) {
        
        DBConnection db;
        PreparedStatement pstmt;
        List<Integer> genreID = new ArrayList<Integer>();
        List<catalogue.entities.MovieSubItem> movieList = new LinkedList<catalogue.entities.MovieSubItem>();
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id FROM LUDRIKB.GENRES WHERE LOWER(name) LIKE CONCAT(CONCAT('%', ?),'%')");
            pstmt.setString(1, genre );
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                genreID.add(rs.getInt("id"));                    
            }
            
            int count = genreID.size();
            if( count > 0 ) {
                //Ha van olyan műfaj amire illeszkedik a felhasználó által keresett szöveg
                
                //
                // Műfajokhoz tartozó filmek ID-jának meghatározása
                //
                
                StringBuilder query =  new StringBuilder("SELECT movie_id FROM LUDRIKB.GENREOFMOVIE WHERE genre_id IN ( ?");
                for( int i=1 ; i<count ; i++) {
                    query.append(", ?");
                }
                query.append(" )");
                
                //
                // STATEMENT ÖSSZEÁLLÍTÁSA ÉS KITÖLTÉSE
                //
                pstmt = db.createStatement(query.toString());
                for( int i= 1; i<= count ; i++) {
                    pstmt.setInt(i, genreID.get(i-1));
                }

                rs = pstmt.executeQuery();
//                db.commit();

                ArrayList<Integer> movieIdx = new ArrayList<Integer>();
                while ( rs.next() ) {
                    // MOVIE_ID elemek listához való hozzáadása
                    movieIdx.add(rs.getInt("movie_id"));
                                
                }//end of while
                
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
                
                //
                // QUERY ÖSSZEÁLLÍTÁSA
                //
                query =  new StringBuilder("SELECT id, title_hu, year, type FROM LUDRIKB.MOVIES WHERE id IN ( ?");
                for( int i=1 ; i<movieIdx.size() ; i++) {
                    query.append(", ?");
                }
                query.append(" )");
                
                //
                // STATEMENT ÖSSZEÁLLÍTÁSA ÉS KITÖLTÉSE
                //
                pstmt = db.createStatement(query.toString());
                for( int i= 1; i<= movieIdx.size() ; i++) {
                    pstmt.setInt(i, movieIdx.get(i-1));
                }

               
                rs = pstmt.executeQuery();
//                db.commit();

                while ( rs.next() ) {
                   
                    //
                    // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA
                    //
                    MovieSubItem subItem = new MovieSubItem();
                    subItem.setId(rs.getInt("id"));
                    subItem.setTitleHU(rs.getString("title_HU"));
                    subItem.setYear( rs.getShort("year") );
                    subItem.setType(rs.getString("type"));

                    //Movie bejegyzés hozzáadása listához
                     movieList.add(subItem);
                                
                }//end of while
            }//end of if
            db.commit();
            db.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally{  
            return movieList;
        }
        
        
    }

    /**
     * A paraméterként kapott típusú filmekkel tér vissza. Minden olyan film bejegyzést visszaad, amelyben típusában megtalálható a paraméter részstringként.
     * @param type  részstring, aminek benne kell lennie a típus nevében
     * @return      MovieItem objektumok listája
     */
    @WebMethod(operationName = "getMoviesFilteredByTypeService")
    public List<catalogue.entities.MovieSubItem> getMoviesFilteredByTypeService(@WebParam(name = "type") String type) {
        
        List<catalogue.entities.MovieSubItem> movieList = new LinkedList<catalogue.entities.MovieSubItem>();
        
        DBConnection db;
        PreparedStatement pstmt;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id, title_hu, year, type FROM LUDRIKB.MOVIES WHERE LOWER(type) LIKE CONCAT(CONCAT('%', ?),'%')");
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                
                //
                // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA
                //
                MovieSubItem subItem = new MovieSubItem();
                subItem.setId(rs.getInt("id"));
                subItem.setTitleHU(rs.getString("title_HU"));
                subItem.setYear( rs.getShort("year") );
                subItem.setType(rs.getString("type"));
                
                movieList.add(subItem);
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            return movieList;
        }
        
    }

    /**
     * Törli az adott kulccsal rendelkező film bejegyzéseket a katalógusból. Több bejegyzés törlésére is sor kerülhet egyszerre.
     * @param keys     kulcsok listája, amelyek alapján töröl
     * @return          sikeresen töröl bejegyzések száma
     */
    @WebMethod(operationName = "deleteMoviesService")
    public String deleteMoviesService(@WebParam(name = "pKeys") List<Integer> keys) {
        
        DBConnection db;
        PreparedStatement pstmt;
        Integer succesCount = 0;
        try {
            db = new DBConnection();
            //Filmhez kapcsolódó műfajok törlése
            pstmt = db.createStatement("DELETE FROM LUDRIKB.GENREOFMOVIE WHERE movie_id = ?");
            for( int key : keys ) {
                pstmt.setInt(1, key );
                pstmt.executeUpdate();
            }
            
            //Filmhez kapcsolódó rendezők törlése
            pstmt = db.createStatement("DELETE FROM LUDRIKB.DIRECTOROFMOVIE WHERE movie_id = ?");
            for( int key : keys ) {
                pstmt.setInt(1, key );
                pstmt.executeUpdate();
            }
            
            //Filmek törlése
            pstmt = db.createStatement("DELETE FROM LUDRIKB.MOVIES WHERE id = ?");
            for( int key : keys ) {
                pstmt.setInt(1, key ); 
                succesCount += pstmt.executeUpdate();
                
            }
            db.commit();
            db.closeConn();
            return ""+succesCount;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
    }

    /**
     * Visszaadja azon rendezők adatait akik neve tartalmazza paraméterben kapott stringet.
     * @param name      string, amit a rendező nevének tartalmaznia kell
     * @return          rendezők listája
     */
    @WebMethod(operationName = "getDirectorsFilteredByNameService")
    public java.util.List<DirectorItem> getDirectorsFilteredByNameService(@WebParam(name = "name") String name) {
        
        StringBuilder sb = new StringBuilder("");
        DBConnection db;
        PreparedStatement pstmt;
        List<catalogue.entities.DirectorItem> directorList = new LinkedList<catalogue.entities.DirectorItem>();
        
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT * FROM LUDRIKB.DIRECTORS WHERE CONCAT(CONCAT(LOWER(firstname),' '),LOWER(lastname)) LIKE CONCAT(CONCAT('%', ?),'%') ORDER BY CONCAT(firstname,lastname) ASC");
            pstmt.setString(1, name.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
        
            while ( rs.next() ) {
             
                //
                // DIRECTOR OBJEKTUM ÖSSZEÁLLÍTÁSA
                //
                DirectorItem director = new DirectorItem();
                director.setId(rs.getInt("ID"));
                director.setFirstname(rs.getString("firstname"));
                director.setLastname(rs.getString("lastname"));
                director.setBirth(rs.getDate("birth"));
                director.setBirthplace(rs.getString("birthplace"));
                
                
                //Director bejegyzés hozzáadása listához
                directorList.add(director);
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);

        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return directorList;
        }
    }

    /**
     * Törli a paraméterben kapott rendezőket a katalógusból.
     * @param keys  törlendő rendezők azonosítói
     * @return      sikeresen törölt elemek száma vagy hiba
     */
    @WebMethod(operationName = "deleteDirectorsService")
    public String deleteDirectorsService(@WebParam(name = "keys") List<Integer> keys) {
        
        DBConnection db;
        PreparedStatement pstmt;
        Integer succesCount = 0;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("DELETE FROM LUDRIKB.DIRECTORS WHERE id=?");
            for(int i=0; i<keys.size() ; i++) {
                pstmt.setInt(1, keys.get(i));
                int r = pstmt.executeUpdate();
//                db.commit();
                succesCount += r;
                pstmt.clearParameters();
            }
            db.commit();
            db.closeConn();
            return ""+succesCount;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
        
    }

    /**
     * Visszaadja a katalógus által ismert műfajokat.
     * @return  műfajok listája
     */
    @WebMethod(operationName = "getGenresService")
    public List<GenreItem> getGenresService() {
        
        List<GenreItem> genresList = new LinkedList<GenreItem>();
        
        try {
            
            DBConnection db = new DBConnection();
            PreparedStatement pstmt = db.createStatement("SELECT * FROM LUDRIKB.GENRES ORDER BY name ASC");
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                //Genre bejegyzés hozzáadása listához
                GenreItem item = new GenreItem(rs.getInt("id"),rs.getString("name"));
                genresList.add(item);
            }
            db.commit();
            db.closeConn();       
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);

        }
        
        return genresList;
        
    }

    /**
     * Új műfajt vesz fel az katalógusba.
     * @param name  új műfaj megnevezése
     * @return      sikerességet igazoló string
     */
    @WebMethod(operationName = "insertGenreService")
    public String insertGenreService(@WebParam(name = "name") String name) {
        
        DBConnection dB = new DBConnection();
        int res = 0;
        int id = 1;
        try {
            PreparedStatement pstmt = dB.createStatement("SELECT count(*) AS sum FROM LUDRIKB.GENRES WHERE LOWER(name) = ?");
            pstmt.setString(1, name.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            
            if( rs.getInt("sum")==1 ) {
                return "ERR: integrity error"; 
            }
            pstmt = dB.createStatement("SELECT  * FROM "
                    + "(SELECT  id + 1 AS gap FROM LUDRIKB.GENRES d1 WHERE NOT EXISTS( "
                    + "SELECT  NULL FROM ludrikb.GENRES d2 WHERE d2.id = d1.id + 1 ) ORDER BY id) "
                    + "WHERE rownum = 1");
            //visszaadja a first gap-et az id sorozatban
            rs = pstmt.executeQuery();
            if( rs.next() ) {
                id = rs.getInt("gap");
            }
            pstmt = dB.createStatement("INSERT INTO LUDRIKB.GENRES(id, name) VALUES(?,?)");
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            res = pstmt.executeUpdate();
            dB.commit();
            dB.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        }
        catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
        return ""+res;
        
    }

    /**
     * Törli a paraméterben kapott műfajt a katalógusból.
     * @param id      törlendő műfaj azonosítója
     * @return        sikerességet igazoló string
     */
    @WebMethod(operationName = "deleteGenreService")
    public String deleteGenreService(@WebParam(name = "id") Integer id) {
        
        DBConnection db;
        PreparedStatement pstmt;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("DELETE FROM LUDRIKB.GENRES WHERE id = ? ");
            pstmt.setInt(1, id);
            int r = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
            return ""+r;
            
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
            
        
        
    }

    /**
     * Új kiadási típust vesz fel az katalógusba.
     * @param name      kiadási típus neve
     * @return          sikerreséget igazoló string
     */
    @WebMethod(operationName = "insertReleaseService")
    public String insertReleaseService(@WebParam(name = "name") String name) {
        
        DBConnection dB = new DBConnection();
        int res = 0;
        int id = 1;
        try {
            PreparedStatement pstmt = dB.createStatement("SELECT count(*) AS sum FROM LUDRIKB.RELEASES WHERE LOWER(name) = ?");
            pstmt.setString(1, name.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if( rs.getInt("sum")==1 ) {
                return "ERR: integrity error"; 
            }
            pstmt = dB.createStatement("SELECT  * FROM "
                    + "(SELECT  id + 1 AS gap FROM LUDRIKB.RELEASES d1 WHERE NOT EXISTS( "
                    + "SELECT  NULL FROM ludrikb.RELEASES d2 WHERE d2.id = d1.id + 1 ) ORDER BY id) "
                    + "WHERE rownum = 1");
            //visszaadja a first gap-et az id sorozatban
            rs = pstmt.executeQuery();
            if( rs.next() ) {
                id = rs.getInt("gap");
            }
            pstmt = dB.createStatement("INSERT INTO LUDRIKB.RELEASES(id, name) VALUES(?,?)");
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            res = pstmt.executeUpdate();
            dB.commit();
            dB.closeConn();
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        }
        catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
        return ""+res;
        
        
    }

    /**
     * Törli a paraméterként kapott kiadási típust.
     * @param id        kiadási típus azonosítója
     * @return          sikerességet igazoló string
     */
    @WebMethod(operationName = "deleteReleaseService")
    public String deleteReleaseService(@WebParam(name = "id") Integer id) {
        
        DBConnection db;
        PreparedStatement pstmt;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("DELETE FROM LUDRIKB.RELEASES WHERE id=?");
            pstmt.setInt(1, id);
            int r = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
            return ""+r;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch( NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
       
        
    }

    /**
     * Frissít egy kiadási típus bejegyzést.
     * @param id        kiadás azonosítója
     * @param newName   kiadás módosított neve
     * @return          sikerességet vagy hibát igazoló string
     */
    @WebMethod(operationName = "updateReleaseService")
    public String updateReleaseService(@WebParam(name = "id") Integer id, @WebParam(name = "newName") String newName) {
        
        DBConnection db;
        PreparedStatement pstmt;
        int res = 0;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT count(*) AS sum FROM LUDRIKB.RELEASES WHERE LOWER(name) = ?");
            pstmt.setString(1, newName.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if( rs.getInt("sum")==1 ) {
                return "ERR: integrity error"; 
            }
            pstmt = db.createStatement("UPDATE LUDRIKB.RELEASES SET name=? WHERE id=?");
            pstmt.setString(1, newName);
            pstmt.setInt(2, id);
            res = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
            return ""+res;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch(NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
        
    }

    /**
     * Visszaadja az adott kulcsú movieitem elemet a katalógusból.
     * @param key       movieitem kulcs, film id-ja
     * @return          movieitem objektum
     */
    @WebMethod(operationName = "getMovieByKeyService")
    public MovieItem getMovieByKeyService(@WebParam(name = "key") int id) {
        
        DBConnection db;
        PreparedStatement pstmt;
        catalogue.entities.MovieItem movie = new catalogue.entities.MovieItem();
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT * FROM LUDRIKB.MOVIES WHERE id = ? ");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
//              
                //
                // MOVIE OBJEKTUM ÖSSZEÁLLÍTÁSA
                //
                
                movie.setId(id);
                movie.setTitleHu(rs.getString("title_hu"));
                movie.setTitleOrig( rs.getString("title_orig"));
                movie.setReleaseid(rs.getInt("releaseid"));
                movie.setImdbUrl(rs.getString("imdb_url"));
                movie.setYear(rs.getInt("year"));
                movie.setType(rs.getString("type"));
                movie.setAmount(rs.getInt("amount"));
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return movie;
    }

    /**
     * Frissíti egy műfaj megnevezését.
     * @param id        frissítendő műfaj azonosítója
     * @param newName   műfaj módosított neve
     * @return          sikerességet vagy hibát igazoló string
     */
    @WebMethod(operationName = "updateGenreService")
    public String updateGenreService(@WebParam(name = "id") int id, @WebParam(name = "newName") String newName) {
        
        DBConnection db;
        PreparedStatement pstmt;
        int res = 0;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT count(*) AS sum FROM LUDRIKB.GENRES WHERE LOWER(name) = ?");
            pstmt.setString(1, newName.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            if( rs.getInt("sum")==1 ) {
                return "ERR: integrity error"; 
            }
            pstmt = db.createStatement("UPDATE LUDRIKB.GENRES SET name=? WHERE id=?");
            pstmt.setString(1, newName);
            pstmt.setInt(2, id);
            res = pstmt.executeUpdate();
            db.commit();
            db.closeConn();
            return ""+res;
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "SQLEX: "+ex;
        } catch(NullPointerException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            return "0";
        }
    }

    /**
     * Visszaadja a paraméterben kapott azonosítóval rendelkező film műfaját. Egy filmhez több műfaj is tartozhat
     * @param movieId   a film azonosítója
     * @return          műfaj(ok) listája
     */
    @WebMethod(operationName = "getGenreOfMovieService")
    public ArrayList<Integer> getGenreOfMovieService(@WebParam(name = "movieId") int movieId) {
        
        DBConnection db;
        PreparedStatement pstmt;
        ArrayList<Integer> genresIdx = new ArrayList<Integer>();
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT genre_id FROM LUDRIKB.GENREOFMOVIE WHERE movie_id = ? ");
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                genresIdx.add(rs.getInt("genre_id"));
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return genresIdx;
        
    }

    /**
     * Visszaadja a paraméterben kapott azonosítóval rendelkező film rendezőit.
     * @param movieId   film azonosítója
     * @return          rendezők listája
     */
    @WebMethod(operationName = "getDirectorOfMovieService")
    public ArrayList<Integer> getDirectorOfMovieService(@WebParam(name = "movieId") int movieId) {
        
        DBConnection db;
        PreparedStatement pstmt;
        ArrayList<Integer> directorIdx = new ArrayList<Integer>();
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT director_id FROM LUDRIKB.DIRECTOROFMOVIE WHERE movie_id = ? ");
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            while ( rs.next() ) {
                directorIdx.add(rs.getInt("director_id"));
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return directorIdx;
        
    }

    /**
     * Visszaadja a paraméterben kapott névvel rendelkező kiadvány azonosítóját.
     * @param name      kiadvány neve
     * @return          azonosító vagy null érték
     */
    @WebMethod(operationName = "getReleaseIdByNameService")
    public Integer getReleaseIdByNameService(@WebParam(name = "name") String name) {
        
        DBConnection db;
        PreparedStatement pstmt;
        Integer releaseId = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id FROM LUDRIKB.RELEASES WHERE name = ? ");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                releaseId = rs.getInt("id");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return releaseId;
        
    }

    /**
     * Visszaadja a paraméterben kapott névvel rendelkező műfaj azonosítóját.
     * @param name  műfaj neve
     * @return      műfaj azonosítója vagy null
     */
    @WebMethod(operationName = "getGenreIdByNameService")
    public Integer getGenreIdByNameService(@WebParam(name = "name") String name) {
        
        DBConnection db;
        PreparedStatement pstmt;
        Integer genreId = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id FROM LUDRIKB.GENRES WHERE name = ? ");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                genreId = rs.getInt("id");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return genreId;
    }

    /**
     * Visszaadja a paraméterben kapott névvel rendelkező rendező azonosítóját.
     * @param name      rendező neve
     * @return          rendező azonosítója vagy null
     */
    @WebMethod(operationName = "getDirectorIdByNameService")
    public Integer getDirectorIdByNameService(@WebParam(name = "name") String name) {
        DBConnection db;
        PreparedStatement pstmt;
        Integer directorId = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT id FROM LUDRIKB.DIRECTORS WHERE CONCAT(CONCAT(LOWER(firstname),' '),LOWER(lastname)) =? ");
            pstmt.setString(1, name.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                directorId = rs.getInt("id");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return directorId;

    }

    /**
     * Visszaadja a paraméterben kapott azonosítóval rendelkező műfaj nevét.
     * @param id    műfaj azonosító
     * @return      név vagy null
     */
    @WebMethod(operationName = "getGenreNameByIdService")
    public String getGenreNameByIdService(@WebParam(name = "id") Integer id) {
        
        DBConnection db;
        PreparedStatement pstmt;
        String genreName = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT name FROM LUDRIKB.GENRES WHERE id = ? ");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                genreName = rs.getString("name");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return genreName;
        
    }

    /**
     * Visszaadja a paraméterben kapott azonosítóval rendelkező kiadvány nevét.
     * @param id    azonosító
     * @return      név vagy null
     */
    @WebMethod(operationName = "getReleaseNameByIdService")
    public String getReleaseNameByIdService(@WebParam(name = "id") Integer id) {
        
        DBConnection db;
        PreparedStatement pstmt;
        String releaseName = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT name FROM LUDRIKB.RELEASES WHERE id = ? ");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                releaseName = rs.getString("name");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return releaseName;
        
    }

    /**
     * Visszaadja a paraméterben kapott azonosítóval rendelkező rendező nevét.
     * @param id    azonosító   
     * @return      név vagy null
     */
    @WebMethod(operationName = "getDirectorNameByIdService")
    public String getDirectorNameByIdService(@WebParam(name = "id") Integer id) {
        DBConnection db;
        PreparedStatement pstmt;
        String directorName = null;
        try {
            db = new DBConnection();
            pstmt = db.createStatement("SELECT CONCAT(CONCAT(firstname,' '),lastname) AS name FROM LUDRIKB.DIRECTORS WHERE id =? ");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
//            db.commit();
            
            if ( rs.next() ) {
                directorName = rs.getString("name");
                                
            }
            db.commit();
            db.closeConn();          
        } catch (SQLException ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( Exception ex) {
            Logger.getLogger(CatalogueService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return directorName;

        
    }

    
    
}
