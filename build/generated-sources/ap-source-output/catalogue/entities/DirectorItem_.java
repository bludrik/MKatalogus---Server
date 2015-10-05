package catalogue.entities;

import catalogue.entities.MovieItem;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-05-13T09:59:49")
@StaticMetamodel(DirectorItem.class)
public class DirectorItem_ { 

    public static volatile SingularAttribute<DirectorItem, Integer> id;
    public static volatile CollectionAttribute<DirectorItem, MovieItem> moviesCollection;
    public static volatile SingularAttribute<DirectorItem, Date> birth;
    public static volatile CollectionAttribute<DirectorItem, MovieItem> movieItemCollection;
    public static volatile SingularAttribute<DirectorItem, String> birthplace;
    public static volatile SingularAttribute<DirectorItem, String> lastname;
    public static volatile SingularAttribute<DirectorItem, String> firstname;

}