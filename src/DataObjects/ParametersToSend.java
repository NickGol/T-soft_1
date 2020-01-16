package DataObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParametersToSend implements Serializable {

    private static final long serialVersionUID = 1L;
    private String paramName;
    private Double paramValue;

    public ParametersToSend(String paramName, Double paramValue) {
        super();
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public ParametersToSend() {
    }

    @Override
    public String toString() {
        //return paramName + "= " + paramValue + "; ";
        return String.format("%s = %.1f; ", paramName, paramValue);
    }

    public Double getParamValue() {
        return paramValue;
    }

    public void setParamValue(Double paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamName() {
        return paramName;
    }
}