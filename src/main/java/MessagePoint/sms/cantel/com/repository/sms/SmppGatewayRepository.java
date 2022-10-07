package MessagePoint.sms.cantel.com.repository.sms;

import MessagePoint.sms.cantel.com.model.SmppGateway;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SmppGatewayRepository extends MongoRepository<SmppGateway, String> {
}
