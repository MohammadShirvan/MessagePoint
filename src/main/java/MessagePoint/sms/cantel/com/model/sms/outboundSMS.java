package MessagePoint.sms.cantel.com.model.sms;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("outbound_sms")
public class outboundSMS {
    @Id
    private String id;
    @Indexed
    private String outgoingId;
    @CreatedDate
    private Date createAt;
    @Indexed
    private String batchId;
    @Indexed
    private String userId;
    @Indexed
    private String gateway;
    @Indexed
    private long checkingId;
    private String source;
    private String destination;
    private String body;
    private String alphabet;
    private String messageId;
    private int part;
    private int allPart;
    private String status;
    private int updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutgoingId() {
        return outgoingId;
    }

    public void setOutgoingId(String outgoingId) {
        this.outgoingId = outgoingId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(long checkingId) {
        this.checkingId = checkingId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public int getAllPart() {
        return allPart;
    }

    public void setAllPart(int allPart) {
        this.allPart = allPart;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(int updateAt) {
        this.updateAt = updateAt;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
