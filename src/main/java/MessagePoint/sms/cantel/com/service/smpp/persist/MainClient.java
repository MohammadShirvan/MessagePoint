package MessagePoint.sms.cantel.com.service.smpp.persist;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2014 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import MessagePoint.sms.cantel.com.model.SmppGateway;
import MessagePoint.sms.cantel.com.model.sms.STATUS;
import MessagePoint.sms.cantel.com.model.sms.outboundSMS;
import MessagePoint.sms.cantel.com.model.sms.outgoingSMS;
import MessagePoint.sms.cantel.com.repository.sms.outboundSMSRepository;
import MessagePoint.sms.cantel.com.repository.sms.outgoingSMSRepository;
import com.cloudhopper.commons.charset.GSMCharset;
import com.cloudhopper.commons.util.LoadBalancedList;
import com.cloudhopper.commons.util.LoadBalancedLists;
import com.cloudhopper.commons.util.RoundRobinLoadBalancedList;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Runtime.getRuntime;

@Service
public class MainClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    outgoingSMSRepository outgoingSMSRepo;

    @Autowired
    outboundSMSRepository outboundSMSRepo;

    public void runClient(SmppGateway gateway) throws IOException, RecoverablePduException, InterruptedException,
            SmppChannelException, UnrecoverablePduException, SmppTimeoutException {
        DummySmppClientMessageService smppClientMessageService = new DummySmppClientMessageService();
        int i = 0;
        final LoadBalancedList<OutboundClient> balancedList = LoadBalancedLists.synchronizedList(new RoundRobinLoadBalancedList<OutboundClient>());
        balancedList.set(createClient(gateway, smppClientMessageService, ++i), 1);
        balancedList.set(createClient(gateway, smppClientMessageService, ++i), 1);
        balancedList.set(createClient(gateway, smppClientMessageService, ++i), 1);

        final ExecutorService executorService = Executors.newFixedThreadPool(1000);


        getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdownNow();
            ReconnectionDaemon.getInstance().shutdown();
            for (LoadBalancedList.Node<OutboundClient> node : balancedList.getValues()) {
                node.getValue().shutdown();
            }
        }));
        Pageable topHundred = PageRequest.of(0, gateway.getPoolSize());

        while (true) {
            //    System.gc();
            final AtomicLong alreadySent = new AtomicLong();
            List<outgoingSMS> outgoingSMSList = outgoingSMSRepo.findAllByStatus(STATUS.CREATED.name(), topHundred);
            //System.out.println(Thread.currentThread().getName() + " count read:" + outgoingSMSList.size());
            outgoingSMSList.forEach(sms -> {
                sms.setStatus(STATUS.PROCESSING.name());
            });
            outgoingSMSRepo.saveAll(outgoingSMSList);
            BlockingQueue<outgoingSMS> queue = new LinkedBlockingQueue<>(outgoingSMSList);


            // outgoingSMSList.forEach(sms -> {
            while (!queue.isEmpty()) {
                outgoingSMS sms = queue.take();
                System.out.println(Thread.currentThread().getName() + "===============================" + outgoingSMSList.size() + "----->" + sms.getId());
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            double start = System.currentTimeMillis();
                            System.out.println("here is running --- " + Thread.currentThread().getName());
                            System.out.println(Thread.currentThread().getName() + "-->>read from db:" + (System.currentTimeMillis() - start) / 1000);
                            start = System.currentTimeMillis();

                            //outgoingSMSRepo.saveAll(outgoingSMSList);
                            double _start = System.currentTimeMillis();
                            long sent = alreadySent.incrementAndGet();
                            //        System.out.println("here is running --- " + Thread.currentThread().getName() + " --- sent:" + sent);

                            //     List<outboundSMS> outboundSMSList = new ArrayList<>();
                            //       outgoingSMSList.forEach(sms -> {
                            try {
                                int maximumSingleMessageSize = 0;
                                int maximumMultipartMessageSegmentSize = 0;
                                byte[] byteSingleMessage = null;
                                byte alphabet;
                                if (GSMCharset.canRepresent(sms.getBody())) {
                                    byteSingleMessage = sms.getBody().getBytes();
                                    alphabet = SmppConstants.DATA_CODING_DEFAULT;
                                    maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT;
                                    maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT;
                                } else {
                                    byteSingleMessage = sms.getBody().getBytes("UTF-16BE");
                                    alphabet = SmppConstants.DATA_CODING_UCS2;
                                    maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2;
                                    maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2;
                                }
                                byte[][] byteMessagesArray = null;
                                byte esmClass;
                                if (sms.getBody().length() > maximumSingleMessageSize) {
                                    byteMessagesArray = splitUnicodeMessage(byteSingleMessage, maximumMultipartMessageSegmentSize);
                                    esmClass = SmppConstants.ESM_CLASS_UDHI_MASK;
                                } else {
                                    byteMessagesArray = new byte[][]{byteSingleMessage};
                                    esmClass = SmppConstants.ESM_CLASS_MM_DEFAULT;
                                }

                                for (int i = 0; i < byteMessagesArray.length; i++) {
                                    System.out.println("Areee");
                                    SubmitSm submit0 = new SubmitSm();
                                    submit0.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
                                    submit0.setSourceAddress(new Address((byte) 0x01, (byte) 0x01, sms.getSource()));
                                    submit0.setDestAddress(new Address((byte) 0x01, (byte) 0x01, sms.getDestination()));
                                    submit0.setEsmClass(esmClass);
                                    submit0.setDataCoding(alphabet);
                                    submit0.setShortMessage(byteMessagesArray[i]);
                                    final OutboundClient next = balancedList.getNext();
                                    final SmppSession session = next.getSession();
                                    if (session != null && session.isBound()) {
                                        //  final SubmitSmResp submitSmResp = session.submit(submit0, 10000);
                                        // Assert.assertNotNull(submit0);
                                        if (true) {
                                            outboundSMS outbound = new outboundSMS();
                                            outbound.setAllPart(byteMessagesArray.length);
                                            outbound.setBatchId(sms.getBatchId());
                                            outbound.setAlphabet(alphabet + "");
                                            outbound.setCheckingId(sms.getCheckingId());
                                            outbound.setOutgoingId(sms.getId());
                                            outbound.setSource(sms.getSource());
                                            outbound.setDestination(sms.getDestination());
                                            outbound.setCreateAt(new Date());
                                            outbound.setMessageId(UUID.randomUUID().toString());
                                            outbound.setPart(i + 1);
                                            outbound.setStatus("SUBMITTED");
                                            String body;
                                            if (alphabet == SmppConstants.DATA_CODING_UCS2) {
                                                body = new String(byteMessagesArray[i], "UTF-16BE");
                                            } else {
                                                body = new String(byteMessagesArray[i]);
                                            }
                                            outbound.setBody(body);
                                            // outboundSMSList.add(outbound);
                                            System.out.println("saved to outbound by-- " + Thread.currentThread().getName());
                                            outboundSMSRepo.save(outbound);
                                            outgoingSMSRepo.delete(sms);
                                        }
                                    }
                                    //    System.out.println("going sleep -- " + Thread.currentThread().getName());
                                    //   Thread.sleep(5000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error("Exception occurred {}", e.getMessage());
                            }
                            //     });
//                            if (outboundSMSList.size() > 0) {
//                                outboundSMSRepo.saveAll(outboundSMSList);
//                                outgoingSMSRepo.deleteAll(outgoingSMSList);
//                                System.out.println(Thread.currentThread().getName() + "---->saving to db:" + outboundSMSList.size() + "  ,  " + (System.currentTimeMillis() - _start) / 1000);
//                                outboundSMSList.forEach(item -> {
//                                    System.out.print(item.getOutgoingId() + ",");
//                                });
//                                System.out.println();
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //    System.err.println(e.toString());
                            return;
                        }
                    }
                });
            }

        }

    }


    private static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2 = 134;
    private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2 = 70;
    private static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT = 153;
    private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT = 160;

    private static byte[][] splitUnicodeMessage(byte[] aMessage, Integer maximumMultipartMessageSegmentSize) {

        final byte UDHIE_HEADER_LENGTH = 0x05;
        final byte UDHIE_IDENTIFIER_SAR = 0x00;
        final byte UDHIE_SAR_LENGTH = 0x03;

        // determine how many messages have to be sent
        int numberOfSegments = aMessage.length / maximumMultipartMessageSegmentSize;
        int messageLength = aMessage.length;
        if (numberOfSegments > 255) {
            numberOfSegments = 255;
            messageLength = numberOfSegments * maximumMultipartMessageSegmentSize;
        }
        if ((messageLength % maximumMultipartMessageSegmentSize) > 0) {
            numberOfSegments++;
        }

        // prepare array for all of the msg segments
        byte[][] segments = new byte[numberOfSegments][];

        int lengthOfData;

        // generate new reference number
        byte[] referenceNumber = new byte[1];
        new Random().nextBytes(referenceNumber);

        // split the message adding required headers
        for (int i = 0; i < numberOfSegments; i++) {
            if (numberOfSegments - i == 1) {
                lengthOfData = messageLength - i * maximumMultipartMessageSegmentSize;
            } else {
                lengthOfData = maximumMultipartMessageSegmentSize;
            }

            // new array to store the header
            segments[i] = new byte[6 + lengthOfData];

            // UDH header
            // doesn't include itself, its header length
            segments[i][0] = UDHIE_HEADER_LENGTH;
            // SAR identifier
            segments[i][1] = UDHIE_IDENTIFIER_SAR;
            // SAR length
            segments[i][2] = UDHIE_SAR_LENGTH;
            // reference number (same for all messages)
            segments[i][3] = referenceNumber[0];
            // total number of segments
            segments[i][4] = (byte) numberOfSegments;
            // segment number
            segments[i][5] = (byte) (i + 1);

            // copy the data into the array
            System.arraycopy(aMessage, (i * maximumMultipartMessageSegmentSize), segments[i], 6, lengthOfData);

        }
        return segments;
    }


    private static OutboundClient createClient(SmppGateway gateway, DummySmppClientMessageService smppClientMessageService, int i) {
        OutboundClient client = new OutboundClient();
        client.initialize(getSmppSessionConfiguration(gateway, i), smppClientMessageService);
        client.scheduleReconnect();
        return client;
    }

    private static SmppSessionConfiguration getSmppSessionConfiguration(SmppGateway gateway, int i) {
        SmppSessionConfiguration config = new SmppSessionConfiguration();
        config.setWindowSize(5);
        config.setName("Tester.Session." + i);
        config.setType(SmppBindType.TRANSCEIVER);
        config.setHost("91.245.228.107");
        config.setPort(2775);
        config.setConnectTimeout(10000);
        config.setSystemId("ofoghk1");
        config.setPassword("0f0gh!");
        config.getLoggingOptions().setLogBytes(false);
        // to enable monitoring (request expiration)
        config.setRequestExpiryTimeout(30000);
        config.setWindowMonitorInterval(15000);

        config.setCountersEnabled(false);
        return config;
    }


}
