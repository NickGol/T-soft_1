package DataObjects;

import java.util.Random;

public class DataItem {
    private Integer parId;
    private Integer clientId;
    private String paramName;
    private Double paramValue;

    public DataItem(String parId, String clientId, String paramName) {
        this.parId = Integer.valueOf(parId);
        this.clientId = Integer.valueOf(clientId);
        this.paramName = paramName;
        Random random = new Random();
        this.paramValue = random.nextDouble() * 10;
    }

    public void setParamValue(Double paramValue) {
        this.paramValue = paramValue;
    }

    public Integer getParId() {
        return parId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getParamName() {
        return paramName;
    }

    public Double getParamValue() {
        return paramValue;
    }

    @Override
    public String toString() {
        return String.format("%s = %.1f; ", paramName, paramValue);
        //return paramName + "= " + paramValue +"; ";
    }
}
