package server.getcommands;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "GET")
public class Get_Enable_Send_Pog_Fix {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    public String id = "ENABLE_SEND_POG_FIX";
}
