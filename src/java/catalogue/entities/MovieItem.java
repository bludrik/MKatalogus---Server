/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package catalogue.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Balazs
 */
@Entity
@Table(name = "MOVIES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Movies.findAll", query = "SELECT m FROM Movies m")})
public class MovieItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "TITLE_HU")
    private String titleHu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "TITLE_ORIG")
    private String titleOrig;
    @Basic(optional = false)
    @NotNull
    @Column(name = "YEAR")
    private Integer year;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TYPE")
    private String type;
    @Size(max = 300)
    @Column(name = "IMDB_URL")
    private String imdbUrl;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AMOUNT")
    private Integer amount;
    @JoinColumn(name = "RELEASEID", referencedColumnName = "ID")
    @ManyToOne
    private Integer releaseid;

    public MovieItem() {
    }

    public MovieItem(Integer id) {
        this.id = id;
    }

    public MovieItem(Integer id, String titleHu, String titleOrig, Integer year, String type, Integer amount) {
        this.id = id;
        this.titleHu = titleHu;
        this.titleOrig = titleOrig;
        this.year = year;
        this.type = type;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleHu() {
        return titleHu;
    }

    public void setTitleHu(String titleHu) {
        this.titleHu = titleHu;
    }

    public String getTitleOrig() {
        return titleOrig;
    }

    public void setTitleOrig(String titleOrig) {
        this.titleOrig = titleOrig;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImdbUrl() {
        return imdbUrl;
    }

    public void setImdbUrl(String imdbUrl) {
        this.imdbUrl = imdbUrl;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getReleaseid() {
        return releaseid;
    }

    public void setReleaseid(Integer releaseid) {
        this.releaseid = releaseid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieItem)) {
            return false;
        }
        MovieItem other = (MovieItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "catalogue.entities.Movies[ id=" + id + " ]";
    }
    
}
