package server.sendcommands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "GET")
public class Get_Calibrate_Clear {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    public String id = "CALIBRATE_CLEAR";
}
