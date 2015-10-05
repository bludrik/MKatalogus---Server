package catalogue.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.2.v20111125-r10461", date="2013-05-13T09:59:49")
@StaticMetamodel(MovieItem.class)
public class MovieItem_ { 

    public static volatile SingularAttribute<MovieItem, Integer> id;
    public static volatile SingularAttribute<MovieItem, Integer> amount;
    public static volatile SingularAttribute<MovieItem, String> titleOrig;
    public static volatile SingularAttribute<MovieItem, String> imdbUrl;
    public static volatile SingularAttribute<MovieItem, Integer> releaseid;
    public static volatile SingularAttribute<MovieItem, String> titleHu;
    public static volatile SingularAttribute<MovieItem, Integer> year;
    public static volatile SingularAttribute<MovieItem, String> type;

}