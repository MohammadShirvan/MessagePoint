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


import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.DeliveryReceiptException;
import com.cloudhopper.smpp.util.SmppUtil;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummySmppClientMessageService implements SmppClientMessageService {
    private static final Logger logger = LoggerFactory.getLogger(DummySmppClientMessageService.class);

    /**
     * delivery receipt, or MO
     */
    @Override
    public PduResponse received(OutboundClient client, DeliverSm deliverSm) {


        String sourceAddress = deliverSm.getSourceAddress().getAddress();
        String message = CharsetUtil.decode((deliverSm).getShortMessage(),
                mapDataCodingToCharset((deliverSm).getDataCoding()));
        logger.debug("SMS Message Received: {}, Source Address: {}", message.trim(), sourceAddress);

        boolean isDeliveryReceipt = false;
        if (false) {
            isDeliveryReceipt = deliverSm.getOptionalParameters() != null;
        } else {
            isDeliveryReceipt = SmppUtil.isMessageTypeAnyDeliveryReceipt((deliverSm).getEsmClass());
        }

        if (isDeliveryReceipt) {
            DeliveryReceipt dlr = null;
            try {
                dlr = DeliveryReceipt.parseShortMessage(message, DateTimeZone.UTC);
            } catch (DeliveryReceiptException e) {
                e.printStackTrace();
            }
            logger.info("Received delivery from {} at {} with message-id {} and status {}", sourceAddress,
                    dlr.getDoneDate(), dlr.getMessageId(), DeliveryReceipt.toStateText(dlr.getState()));
        }


        PduResponse response = deliverSm.createResponse();
        return  response;
    }

    private String mapDataCodingToCharset(byte dataCoding) {
        switch (dataCoding) {
            case SmppConstants.DATA_CODING_LATIN1:
                return CharsetUtil.NAME_ISO_8859_1;
            case SmppConstants.DATA_CODING_UCS2:
                return CharsetUtil.NAME_UCS_2;
            default:
                return CharsetUtil.NAME_GSM;
        }
    }
}
