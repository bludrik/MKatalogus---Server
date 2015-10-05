/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package catalogue.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A film kiadási fajtáját reprezentáló osztály.
 * @author Ludrik Balázs
 */
@Entity
@Table(name = "RELEASES")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "Extras.findAll", query = "SELECT e FROM Extras e"),
//    @NamedQuery(name = "Extras.findById", query = "SELECT e FROM Extras e WHERE e.id = :id"),
//    @NamedQuery(name = "Extras.findByName", query = "SELECT e FROM Extras e WHERE e.name = :name")})
public class ReleaseItem implements Serializable {
    @OneToMany(mappedBy = "releaseid")
    private Collection<MovieItem> moviesCollection;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "NAME")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "extrasData")
    private Collection<MovieItem> movieItemCollection;
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation


    public ReleaseItem() {
    }

    public ReleaseItem(Integer id) {
        this.id = id;
    }

    public ReleaseItem(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof ReleaseItem)) {
            return false;
        }
        ReleaseItem other = (ReleaseItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "catalogue.entities.Extras[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<MovieItem> getMovieItemCollection() {
        return movieItemCollection;
    }

    public void setMovieItemCollection(Collection<MovieItem> movieItemCollection) {
        this.movieItemCollection = movieItemCollection;
    }

    @XmlTransient
    public Collection<MovieItem> getMoviesCollection() {
        return moviesCollection;
    }

    public void setMoviesCollection(Collection<MovieItem> moviesCollection) {
        this.moviesCollection = moviesCollection;
    }
    
}
