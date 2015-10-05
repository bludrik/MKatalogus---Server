/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package catalogue.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Balazs
 */
@Embeddable
public class MovieSubItem implements Serializable {
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "TITLE_HU")
    private String titleHU;
    @Basic(optional = false)
    @NotNull
    @Column(name = "YEAR")
    private short year;
//    @Basic(optional = false)
//    @NotNull
//    @Size(min = 1, max = 50)
//    @Column(name = "RELEASE")
//    private Integer release;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "TYPE")
    private String type;

    public MovieSubItem() {
    }

    public MovieSubItem(Integer id, String titleHU, short year, String type) {
        this.id = id;
        this.titleHU = titleHU;
        this.year = year;
        this.type = type;
    }

    
    
//    public MovieSubItem(String titleEng, short year, /*Integer release,*/ String type) {
//        this.titleHU = titleEng;
//        this.year = year;
////        this.release = release;
//        this.type = type;
//    }

    public String getTitleHU() {
        return titleHU;
    }

    public void setTitleHU(String titleEng) {
        this.titleHU = titleEng;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    
//    public Integer getRelease() {
//        return release;
//    }
//
//    public void setRelease(Integer release) {
//        this.release = release;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (titleHU != null ? titleHU.hashCode() : 0);
//        hash += (release != null ? release.hashCode() : 0);
        hash += (type != null ? type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieSubItem)) {
            return false;
        }
        MovieSubItem other = (MovieSubItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if ((this.titleHU == null && other.titleHU != null) || (this.titleHU != null && !this.titleHU.equals(other.titleHU))) {
            return false;
        }
        if ( !(this.year == other.year)) {
            return false;
        }
//        if ((this.release == null && other.release != null) || (this.release != null && !this.release.equals(other.release))) {
//            return false;
//        }
        if ((this.type == null && other.type != null) || (this.type != null && !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "catalogue.entities.MoviesPK[ titleEng=" + titleHU + ", year=" + year + /*", release=" + release +*/ ", type=" + type + " ]";
    }
    
}
