package MessagePoint.sms.cantel.com.repository.sms;

import MessagePoint.sms.cantel.com.model.sms.routing.Route;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface routeRepository extends MongoRepository<Route, String> {
}
