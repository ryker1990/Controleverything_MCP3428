/* Controleverything.com - MCP3428
 
 https://www.controleverything.com/content/Analog-Digital-Converters?sku=MCP3428_I2CADC
 
 Firmware v1.0 - java

 Author: Yadwinder Singh
 
 4-Channel ADC with 16-Bit Resolution
 4 Differential Analog Inputs
 Programmable x1-x8 Gain Amplifier
 Up to 8 Devices per I2C Port
 Up to 3.4MHz Communication Speed
 0x68 Start Address

 Hardware version - Rev A.
 
 Platform : Raspberry pi

 Project uses pi4j library. 
 Please follow a detailed tutorial to install pi4j here.
 
 http://pi4j.com/install.html
 
 Compile the java program with command ~\[Directory of code]\ pi4j Filename.java
 Run it with  ~\[Directory of code]\ pi4j Filename
 
 */


import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
 

public class MCP3428
{
    public final static int MCP3428_ADDRESS = 0x68;
    
    public final static byte L = 0;
    public final static byte H = 1;
    public final static byte F = 2;
     
	
    public final static int CHANNEL_0 = 0;
    public final static int CHANNEL_1 = 1;
    public final static int CHANNEL_2 = 2;
    public final static int CHANNEL_3 = 3;
   
    public final static int GAIN_1 = 0;
    public final static int GAIN_2 = 1;
    public final static int GAIN_4 = 2;
    public final static int GAIN_8 = 3;

    double VRef = 2.048;  // Reference Voltage
     
    //communication register
    public final static int BIT_RDY = 7; //data ready
    public final static int BIT_C1 = 6; //channel select
    public final static int BIT_C0 = 5; //channel select
    public final static int BIT_OC = 4; //conversion mode (one shot/continuous)
    public final static int BIT_S1 = 3; //sample rate
    public final static int BIT_S0 = 2; //sample rate
    public final static int BIT_G1 = 1; //gain
    public final static int BIT_G0 = 0; //gain

    byte[] b = new byte[3];
    int status = 0;		
     
    private static boolean verbose = "true".equals(System.getProperty("mcp3428.verbose", "false"));
    
    private I2CBus bus;
    private I2CDevice mcp3428;
    
    public MCP3428()
    {
        this(MCP3428_ADDRESS);
    }
    
    public MCP3428(int address)
    {
        try
        {
            // Get i2c bus
            bus = I2CFactory.getInstance(I2CBus.BUS_1); // Depends onthe RasPI version
            if (verbose)
                System.out.println("Connected to bus. OK.");
            
            // Get device itself
            mcp3428 = bus.getDevice(address);
            if (verbose)
                System.out.println("Connected to device. OK.");
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
    

   public void setConfig(int channel, int gain)             // Select Channel and Gain 
    {
        
        //configuration register, assuming 16 bit resolution
        byte reg =  (byte)((1 << BIT_RDY) | (channel << BIT_C0) | (1 << BIT_OC) | (1 << BIT_S1) | gain);
        
        try{
            
            mcp3428.write(reg);    // write configuration into config register.
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        
    }
    public double readADC()
    
    {
        
        try{

		mcp3428.read(b,0,3);    // read byte data into byte buffer b
            	Thread.sleep(300);
            
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        int h = b[0] & 0xff;
        int l = b[1] & 0xff;
        int r = b[2] & 0xff;
        
        long t = h << 8 |  l;
        
        
        if (t >= 32768)
            t = 65536l - t;
        

        System.out.println("ADC output :  " + t);  // print Raw ADC value
  
        double v = (double) t * VRef/32768.0;       // convert the ADC value voltage
        return v;
        
    }
    
    public static void main(String[] args)
    {
        
        MCP3428 sensor = new MCP3428();
	
        final NumberFormat NF = new DecimalFormat("##00.00");
        sensor.setConfig(CHANNEL_0,GAIN_1);     //selected Channel = 0 and Gain = 1x
        
	while(true)
        {
            
            System.out.println("OUTPUT FROM CHANNEL : " + sensor.readADC());
         
        }
        
        
    }
}

