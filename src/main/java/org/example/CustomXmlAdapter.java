package org.example;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CustomXmlAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String s) throws Exception {
        if(s == null || s.trim().isEmpty()){
            return "";
        }
        return s.trim();
    }

    @Override
    public String marshal(String s) throws Exception {
        if(s == null || s.trim().isEmpty()){
            return null;
        }
        else return s.trim();
    }
}
