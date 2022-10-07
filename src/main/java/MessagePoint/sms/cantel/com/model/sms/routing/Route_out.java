package MessagePoint.sms.cantel.com.model.sms.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Route_out {
    If_gateway If_gateway;
    If_source If_source;
    If_destination If_destination;
    private String then_gateway;
    private String then_source;
    private String then_destination;


    public Route_out() {
    }

    public Route_out.If_gateway getIf_gateway() {
        return If_gateway;
    }

    public void setIf_gateway(Route_out.If_gateway if_gateway) {
        If_gateway = if_gateway;
    }

    public Route_out.If_source getIf_source() {
        return If_source;
    }

    public void setIf_source(Route_out.If_source if_source) {
        If_source = if_source;
    }

    public Route_out.If_destination getIf_destination() {
        return If_destination;
    }

    public void setIf_destination(Route_out.If_destination if_destination) {
        If_destination = if_destination;
    }

    public String getThen_gateway() {
        return then_gateway;
    }

    public void setThen_gateway(String then_gateway) {
        this.then_gateway = then_gateway;
    }

    public String getThen_source() {
        return then_source;
    }

    public void setThen_source(String then_source) {
        this.then_source = then_source;
    }

    public String getThen_destination() {
        return then_destination;
    }

    public void setThen_destination(String then_destination) {
        this.then_destination = then_destination;
    }

    @Override
    public String toString() {
        return "Routing_out{" +
                "If_gateway=" + If_gateway +
                ", If_source=" + If_source +
                ", If_destination=" + If_destination +
                ", then_gateway='" + then_gateway + '\'' +
                ", then_source='" + then_source + '\'' +
                ", then_destination='" + then_destination + '\'' +
                '}';
    }

    public class If_destination {
        private boolean condition;
        ArrayList<String> destinations = new ArrayList<>();


        public If_destination() {
        }

        public boolean isCondition() {
            return condition;
        }

        public void setCondition(boolean condition) {
            this.condition = condition;
        }

        public ArrayList<String> getDestinations() {
            return destinations;
        }

        public void setDestinations(ArrayList<String> destinations) {
            this.destinations = destinations;
        }

        @Override
        public String toString() {
            return "If_destination{" +
                    "condition=" + condition +
                    ", destinations=" + Arrays.asList(destinations) +
                    '}';
        }
    }

    public class If_source implements Serializable {
        private boolean condition;
        ArrayList<String> sources = new ArrayList<>();


        public If_source() {
        }

        public boolean isCondition() {
            return condition;
        }

        public void setCondition(boolean condition) {
            this.condition = condition;
        }

        public ArrayList<String> getSources() {
            return sources;
        }

        public void setSources(ArrayList<String> sources) {
            this.sources = sources;
        }

        @Override
        public String toString() {
            return "If_source{" +
                    "condition=" + condition +
                    ", sources=" + Arrays.asList(sources) +
                    '}';
        }
    }

    public class If_gateway implements Serializable {
        private boolean condition;
        ArrayList<String> gateways = new ArrayList<>();


        public If_gateway() {
        }

        public boolean isCondition() {
            return condition;
        }

        public void setCondition(boolean condition) {
            this.condition = condition;
        }

        public ArrayList<String> getGateways() {
            return gateways;
        }

        public void setGateways(ArrayList<String> gateways) {
            this.gateways = gateways;
        }

        @Override
        public String toString() {
            return "If_gateway{" +
                    "condition=" + condition +
                    ", gateways=" + Arrays.asList(gateways) +
                    '}';
        }
    }
}
