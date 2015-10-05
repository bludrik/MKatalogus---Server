package catalogue.entities;

import catalogue.entities.MovieItem;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-05-13T09:59:49")
@StaticMetamodel(ReleaseItem.class)
public class ReleaseItem_ { 

    public static volatile SingularAttribute<ReleaseItem, Integer> id;
    public static volatile CollectionAttribute<ReleaseItem, MovieItem> moviesCollection;
    public static volatile CollectionAttribute<ReleaseItem, MovieItem> movieItemCollection;
    public static volatile SingularAttribute<ReleaseItem, String> name;

}