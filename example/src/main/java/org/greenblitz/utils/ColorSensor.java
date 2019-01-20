package org.greenblitz.utils;

import edu.wpi.first.wpilibj.I2C;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ColorSensor {
    protected final static int CMD = 0x80;
    protected final static int MULTI_BYTE_BIT = 0x20;
    protected final static int ENABLE_REGISTER  = 0x00;
    protected final static int ATIME_REGISTER   = 0x01;
    protected final static int PPULSE_REGISTER  = 0x0E;

    protected final static int ID_REGISTER     = 0x12;
    protected final static int CDATA_REGISTER  = 0x14;
    protected final static int RDATA_REGISTER  = 0x16;
    protected final static int GDATA_REGISTER  = 0x18;
    protected final static int BDATA_REGISTER  = 0x1A;
    protected final static int PDATA_REGISTER  = 0x1C;

    protected final static int PON   = 0b00000001;
    protected final static int AEN   = 0b00000010;
    protected final static int PEN   = 0b00000100;
    protected final static int WEN   = 0b00001000;
    protected final static int AIEN  = 0b00010000;
    protected final static int PIEN  = 0b00100000;

    private final double integrationTime = 10;


    private I2C sensor;

    private ByteBuffer buffy = ByteBuffer.allocate(8);

    //public short red = 0, green = 0, blue = 0, prox = 0;

    public ColorSensor(I2C.Port port) {
        buffy.order(ByteOrder.LITTLE_ENDIAN);
        sensor = new I2C(port, 0x39); //0x39 is the address of the Vex ColorSensor V2

        sensor.write(CMD | 0x00, PON | AEN | PEN);

        sensor.write(CMD | 0x01, (int) (256-integrationTime/2.38)); //configures the integration time (time for updating color data)
        sensor.write(CMD | 0x0E, 0b1111);

    }



    public ColorSensorData read() {
        short red = 0, green = 0, blue = 0, prox = 0;
        buffy.clear();
        sensor.read(CMD | MULTI_BYTE_BIT | RDATA_REGISTER, 8, buffy);

        red = buffy.getShort(0);
        if(red < 0) { red += 0b10000000000000000; }

        green = buffy.getShort(2);
        if(green < 0) { green += 0b10000000000000000; }

        blue = buffy.getShort(4);
        if(blue < 0) { blue += 0b10000000000000000; }

        prox = buffy.getShort(6);
        if(prox < 0) { prox += 0b10000000000000000; }

        return new ColorSensorData(red, green, blue, prox);
    }

    public int status() {
        buffy.clear();
        sensor.read(CMD | 0x13, 1, buffy);
        return buffy.get(0);
    }

    public void free() {
        sensor.free();
    }

    public class ColorSensorData{
        private short red, green, blue, prox;
        private ColorSensorData(short red, short green, short blue, short prox ){
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.prox = prox;
        }

        public short getBlue() {
            return blue;
        }

        public short getRed() {
            return red;
        }

        public short getGreen() {
            return green;
        }

        public short getProx() {
            return prox;
        }
    }
}
