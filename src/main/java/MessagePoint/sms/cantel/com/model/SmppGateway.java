package MessagePoint.sms.cantel.com.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SmppGateway {

    @Id
    private String id;

    @Indexed
    private String name;
    private String ip;
    private String systemId;
    private String password;
    private int port;
    private int windowSize;
    private int poolSize;
    private int instances;
    private boolean status;

    public SmppGateway(String name, String ip, String systemId, String password, int port, int windowSize, int poolSize, int instances, boolean status) {
        this.name = name;
        this.ip = ip;
        this.systemId = systemId;
        this.password = password;
        this.port = port;
        this.windowSize = windowSize;
        this.poolSize = poolSize;
        this.instances = instances;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
