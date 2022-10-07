package MessagePoint.sms.cantel.com;

import MessagePoint.sms.cantel.com.model.SmppGateway;
import MessagePoint.sms.cantel.com.repository.sms.SmppGatewayRepository;
import MessagePoint.sms.cantel.com.service.smpp.persist.MainClient;
import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MessagePointApplication implements CommandLineRunner {

    @Autowired
    MainClient mainClient;

    @Autowired
    SmppGatewayRepository smppGatewayRepo;

    public static void main(String[] args) {
        SpringApplication.run(MessagePointApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (smppGatewayRepo.count() == 0) {
            SmppGateway gateway = new SmppGateway("SMPP", "ServerIP", "smpp_user",
                    "smpp_pass", 2775, 100, 1000, 2, true);
            smppGatewayRepo.save(gateway);
        }

        List<SmppGateway> smppGateways = smppGatewayRepo.findAll();

        smppGateways.forEach(gateway -> {
            try {
                mainClient.runClient(gateway);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RecoverablePduException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (SmppChannelException e) {
                throw new RuntimeException(e);
            } catch (UnrecoverablePduException e) {
                throw new RuntimeException(e);
            } catch (SmppTimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
