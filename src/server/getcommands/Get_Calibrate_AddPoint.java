package server.getcommands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "GET")
public class Get_Calibrate_AddPoint {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    public String id = "CALIBRATE_ADDPOINT";
}
