package MessagePoint.sms.cantel.com.repository.sms;

import MessagePoint.sms.cantel.com.model.sms.outgoingSMS;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface outgoingSMSRepository extends MongoRepository<outgoingSMS, String> {

    List<outgoingSMS> findAllByStatus(String status, Pageable page);
}
