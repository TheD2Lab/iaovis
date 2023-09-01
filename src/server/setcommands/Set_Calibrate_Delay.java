package server.setcommands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SET")
public class Set_Calibrate_Delay {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    public String id = "CALIBRATE_DELAY";

    @JacksonXmlProperty(isAttribute = true, localName = "VALUE")
    public double value;
}
