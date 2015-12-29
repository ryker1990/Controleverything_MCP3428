/**************************************************************************
 Controleverything.com- MCP3428
 Analog-to-Digital Converter with I2C Interface and On-Board Reference using Arduino Nano
 
 Firmware v1.0 - Arduino Nano
 Author - Anjali Rajan
 
 4-Channel ADC with 16-Bit Resolution
 4 Differential Analog Inputs
 Programmable x1-x8 Gain Amplifier
 Up to 8 Devices per I²C Port
 Up to 3.4MHz Communication Speed
 0x68 Start Address
 
 Harware Version - Rev A.
 Platform - Arduino Nano
 
 ****************************************************************************/


#include  <Wire.h>

// I2C Address of device
// MCP3421, MCP3425 & MCP3426 are factory programed for any of 0x68 thru 0x6F
#define MCP342X_DEFAULT_ADDRESS  0x68

// MCP3422, MCP3423, MCP3424, MCP3427 & MCP3428 addresses are controlled by address lines A0 and A1
// each address line can be low (GND), high (VCC) or floating (FLT)
#define MCP342X_A0GND_A1GND   0x68
#define MCP342X_A0GND_A1FLT   0x69
#define MCP342X_A0GND_A1VCC   0x6A
#define MCP342X_A0FLT_A1GND   0x6B
#define MCP342X_A0VCC_A1GND   0x6C
#define MCP342X_A0VCC_A1FLT   0x6D
#define MCP342X_A0VCC_A1VCC   0x6E
#define MCP342X_A0FLT_A1VCC   0x6F


// Conversion mode definitions
#define MCP342X_MODE_ONESHOT  0x00
#define MCP342X_MODE_CONTINUOUS 0x10


// Channel definitions
// MCP3421 & MCP3425 have only the one channel and ignore this param
// MCP3422, MCP3423, MCP3426 & MCP3427 have two channels and treat 3 & 4 as repeats of 1 & 2 respectively
// MCP3424 & MCP3428 have all four channels
#define MCP342X_CHANNEL_1 0x00
#define MCP342X_CHANNEL_2 0x20
#define MCP342X_CHANNEL_3 0x40
#define MCP342X_CHANNEL_4 0x60
#define MCP342X_CHANNEL_MASK  0x60


// Sample size definitions - these also affect the sampling rate
// 12-bit has a max sample rate of 240sps
// 14-bit has a max sample rate of  60sps
// 16-bit has a max sample rate of  15sps
// 18-bit has a max sample rate of   3.75sps (MCP3421, MCP3422, MCP3423, MCP3424 only)
#define MCP342X_SIZE_12BIT  0x00
#define MCP342X_SIZE_14BIT  0x04
#define MCP342X_SIZE_16BIT  0x08
#define MCP342X_SIZE_18BIT  0x0C
#define MCP342X_SIZE_MASK 0x0C


// Programmable Gain definitions
#define MCP342X_GAIN_1X 0x00
#define MCP342X_GAIN_2X 0x01
#define MCP342X_GAIN_4X 0x02
#define MCP342X_GAIN_8X 0x03
#define MCP342X_GAIN_MASK 0x03


// /RDY bit definition
#define MCP342X_RDY 0x80

float volt= 0.0;
int result = 0 ;

int b1,b2;

void setup() {
    
    Wire.begin();  // join I2C bus
    
    Serial.begin(9600); // Open serial connection to send info to the host
    while (!Serial) {}  // wait for Serial comms to become ready
    Serial.println("Starting up");
    
    //Prepare the confugration byte. Change the parameters as per your requirements.
    
    byte config = ( MCP342X_MODE_CONTINUOUS |
                    MCP342X_CHANNEL_1 |
                    MCP342X_SIZE_16BIT |
                    MCP342X_GAIN_1X
                    );            

    Wire.beginTransmission(MCP342X_DEFAULT_ADDRESS);
    Wire.write(config);
    Wire.endTransmission();
    
    Serial.println("Started");

    
}  // End of setup()

void loop() {
    
    Wire.beginTransmission(MCP342X_DEFAULT_ADDRESS);

     Wire.requestFrom(MCP342X_DEFAULT_ADDRESS, 2);  // Rquest 2 bytes from Device
    
    if(Wire.available() == 2) {                   
    
     b1 = Wire.read();
     b2 = Wire.read();

     result = b1 << 8 | b2 ;
     
     volt = result * 2.048/32768.0;   // Calculate the voltage 
    
    Serial.println(volt);
    }

    Wire.endTransmission();
    
    delay(500);      
    
}  // End of loop()
