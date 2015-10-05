package catalogue.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Rendező bejegyzést reprezentáló osztály.
 * @author Ludrik Balazs
 */
@Entity
@Table(name = "DIRECTORS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DirectorData.findAll", query = "SELECT d FROM DirectorData d")})
public class DirectorItem implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "direcid")
    private Collection<MovieItem> moviesCollection;
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "LASTNAME")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "BIRTH")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birth;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "BIRTHPLACE")
    private String birthplace;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "direcid")
    private Collection<MovieItem> movieItemCollection;

    public DirectorItem() {
    }

    public DirectorItem(Integer id) {
        this.id = id;
    }

    public DirectorItem(Integer id, String firstname, String lastname, Date birth, String birthplace) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birth = birth;
        this.birthplace = birthplace;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    @XmlTransient
    public Collection<MovieItem> getMovieItemCollection() {
        return movieItemCollection;
    }

    public void setMovieItemCollection(Collection<MovieItem> movieItemCollection) {
        this.movieItemCollection = movieItemCollection;
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
        if (!(object instanceof DirectorItem)) {
            return false;
        }
        DirectorItem other = (DirectorItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "catalogue.entities.DirectorData[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<MovieItem> getMoviesCollection() {
        return moviesCollection;
    }

    public void setMoviesCollection(Collection<MovieItem> moviesCollection) {
        this.moviesCollection = moviesCollection;
    }
    
}
