package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(CustomXmlAdapter.class)
@XmlRootElement(name = "laptop")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laptop {

    //private String id;
    private String manufacturer;
    private Screen screen;
    private Processor processor;
    private String ram;
    private Disc disc;
    private Graphic_card graphic_card;
    private String os;
    private String disc_reader;

    public LaptopEntity parseToLaptopEntity(){
        return new LaptopEntity(this.manufacturer, this.screen.getSize(), this.screen.getResolution(),
                this.screen.getType(), this.screen.getTouch(), this.processor.getName(), this.processor.getPhysical_cores(),
                this.processor.getClock_speed(), this.getRam(), this.disc.getStorage(), this.disc.getType(),
                this.graphic_card.getName(), this.graphic_card.getMemory(), this.getOs(), this.getDisc_reader());
    }
}
