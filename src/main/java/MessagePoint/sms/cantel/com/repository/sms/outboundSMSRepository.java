package MessagePoint.sms.cantel.com.repository.sms;

import MessagePoint.sms.cantel.com.model.sms.outboundSMS;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface outboundSMSRepository extends MongoRepository<outboundSMS, String> {
}
