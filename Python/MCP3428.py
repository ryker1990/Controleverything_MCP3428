'''**************************************************************************/
Controleverything.com- MCP3428

https://www.controleverything.com/content/Analog-Digital-Converters?sku=MCP3428_I2CADC

Analog-to-Digital Converter with I2C Interface and On-Board Reference using Raspberry Pi

Firmware v1.0 - Python
Author - Amanpal Singh

4-Channel ADC with 16-Bit Resolution
4 Differential Analog Inputs
Programmable x1-x8 Gain Amplifier
Up to 8 Devices per I²C Port
Up to 3.4MHz Communication Speed
0x68 Start Address

Harware Version - Rev A.
Platform - Raspberry Pi

**************************************************************************'''

MCP3428_DEFAULT_ADDRESS	0x68

# MCP3422, MCP3423, MCP3424, MCP3427 & MCP3428 addresses are controlled by address lines A0 and A1
# each address line can be low (GND), high (VCC) or floating (FLT)
MCP3428_A0GND_A1GND		0x68
MCP3428_A0GND_A1FLT		0x69
MCP3428_A0GND_A1VCC		0x6A
MCP3428_A0FLT_A1GND		0x6B
MCP3428_A0VCC_A1GND		0x6C
MCP3428_A0VCC_A1FLT		0x6D
MCP3428_A0VCC_A1VCC		0x6E
MCP3428_A0FLT_A1VCC		0x6F

# Conversion mode definitions
MCP3428_MODE_ONESHOT	0x00
MCP3428_MODE_CONTINUOUS	0x10

# Channel definitions
# MCP3421 & MCP3425 have only the one channel and ignore this param
# MCP3422, MCP3423, MCP3426 & MCP3427 have two channels and treat 3 & 4 as repeats of 1 & 2 respectively
# MCP3424 & MCP3428 have all four channels
MCP3428_CHANNEL_1	0x00
MCP3428_CHANNEL_2	0x20
MCP3428_CHANNEL_3	0x40
MCP3428_CHANNEL_4	0x60
MCP3428_CHANNEL_MASK	0x60

# Sample size definitions - these also affect the sampling rate
# 12-bit has a max sample rate of 240sps
# 14-bit has a max sample rate of  60sps
# 16-bit has a max sample rate of  15sps
MCP3428_SIZE_12BIT	0x00
MCP3428_SIZE_14BIT	0x04
MCP3428_SIZE_16BIT	0x08
MCP3428_SIZE_18BIT	0x0C
MCP3428_SIZE_MASK	0x0C

# Programmable Gain definitions
MCP3428_GAIN_1X	0x00
MCP3428_GAIN_2X	0x01
MCP3428_GAIN_4X	0x02
MCP3428_GAIN_8X	0x03
MCP3428_GAIN_MASK 0x03

# /RDY bit definition
MCP3428_RDY	0x80

#IMporting the lib. and getting talk with I2C Bus.
import smbus
import time
bus = smbus.SMBus(1)


# Default :Channel 1,Sample Rate 240SPS(12- bit),Gain x1 Selected
def intialiseadc():
	bus.write_byte(0x68,0x80|0x10|0x00|0x00)

#Getting raw value from the device
def getadcreading() :
	data = bus.read_i2c_block_data(0x68,0x00,2)
	t = ((data[0] << 8) | data[1])
	print "Rawdata  : ",t
	if (t >= 32768):
		t = 65536 -t
	return t

#Conversion of the raw volatge accoring to the datasheet into volts
def getvoltage():
	
	v = (getadcreading() * 2.048)/2048	
	print "Voltage of the Source is : ", v,"volts"
		
	time.sleep(0.5)

#Calling the intialisation of the device
intialiseadc()

while True:
	
	#Getting the voltage of the source reading from the device
	getvoltage()