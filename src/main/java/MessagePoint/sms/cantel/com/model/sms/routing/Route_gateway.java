package MessagePoint.sms.cantel.com.model.sms.routing;

import java.util.ArrayList;
import java.util.Arrays;

public class Route_gateway {
    ArrayList<String> if_source = new ArrayList<>();
    private boolean condition;
    ArrayList<String> then_if_real_source = new ArrayList<>();
    private String then_if_gateway;
    private String then_gateway;

    public Route_gateway() {
    }

    public ArrayList<String> getIf_source() {
        return if_source;
    }

    public void setIf_source(ArrayList<String> if_source) {
        this.if_source = if_source;
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public ArrayList<String> getThen_if_real_source() {
        return then_if_real_source;
    }

    public void setThen_if_real_source(ArrayList<String> then_if_real_source) {
        this.then_if_real_source = then_if_real_source;
    }

    public String getThen_if_gateway() {
        return then_if_gateway;
    }

    public void setThen_if_gateway(String then_if_gateway) {
        this.then_if_gateway = then_if_gateway;
    }

    public String getThen_gateway() {
        return then_gateway;
    }

    public void setThen_gateway(String then_gateway) {
        this.then_gateway = then_gateway;
    }

    @Override
    public String toString() {
        return "Routing_gateway{" +
                "if_source=" + Arrays.asList(if_source) +
                ", condition=" + condition +
                ", then_if_real_source=" + Arrays.asList(then_if_real_source) +
                ", then_if_gateway='" + then_if_gateway + '\'' +
                ", then_gateway='" + then_gateway + '\'' +
                '}';
    }
}
