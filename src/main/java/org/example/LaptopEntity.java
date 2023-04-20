package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LaptopEntity {

    String manufacturer;
    String screenDiagonal;
    String screenResolution;
    String screenFinish;
    String touchScreenPresent;
    String CPU;
    String coreCount;
    String CPUFrequency;
    String RAM;
    String diskCapacity;
    String diskType;
    String GPU;
    String GPUMemory;
    String OS;
    String opticalDrive;

    public String[] toArrayOfStrings(){
        return new String[]{manufacturer, screenDiagonal, screenResolution, screenFinish, touchScreenPresent, CPU,
        coreCount, CPUFrequency, RAM, diskCapacity, diskType, GPU, GPUMemory, OS, opticalDrive};
    }
}
