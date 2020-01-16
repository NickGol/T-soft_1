package Task_1_Server;

import DataObjects.ParametersToSend;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class XMLSerializer {

    ListWrapperForParameters wrapper;

    public XMLSerializer() {
        wrapper = new ListWrapperForParameters();
    }

    private void setParametersList(List<ParametersToSend> parametersList) {
        wrapper.setParametersList( parametersList );
    }

    public String serialize(List<ParametersToSend> parametersList) {
        setParametersList(parametersList);
        String str = null;
        try {
            str = makeMarshaling();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return str;
    }

    public List<ParametersToSend> deSerialize(String str) {
        List<ParametersToSend> list = null;
        try {
            list = unMarshaling(str);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String makeMarshaling() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ListWrapperForParameters.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter str = new StringWriter();
        jaxbMarshaller.marshal(wrapper, str);

        return str.toString();
    }

    private List<ParametersToSend> unMarshaling(String xmlString) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(ListWrapperForParameters.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //We had written this file in marshalling example
        StringReader str = new StringReader(xmlString);
        ListWrapperForParameters wrapper = (ListWrapperForParameters) jaxbUnmarshaller.unmarshal(str);
        return wrapper.getParametersList();
//        for(Employee emp : emps.getEmployees())
//        {
//            System.out.println(emp.getId());
//            System.out.println(emp.getFirstName());
//        }
    }
}

@XmlRootElement(name = "ParamList")
@XmlAccessorType(XmlAccessType.FIELD)
class ListWrapperForParameters {

    @XmlElement(name = "Parameter")
    private List<ParametersToSend> parametersList = null;

    public List<ParametersToSend> getParametersList() {
        return parametersList;
    }

    public void setParametersList(List<ParametersToSend> parametersList) {
        this.parametersList = parametersList;
    }

}