#!/usr/bin/env python

import os
import time
import RPi.GPIO as GPIO
from bluetooth import *

import json
import select

global server_sock
global port
global uuid
	
global client_sock
global client_info

global timeout

os.system('modprobe w1-gpio')

GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.OUT)				#RedLED
GPIO.setup(18, GPIO.IN, pull_up_down=GPIO.PUD_UP)	#YellowButton
GPIO.setup(22, GPIO.OUT)				#GreenLED
GPIO.setup(23, GPIO.IN, pull_up_down=GPIO.PUD_UP)	#BlueButton

GPIO.setup(19, GPIO.OUT)				#GreenLED2
GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_UP)	#Button2
GPIO.setup(26, GPIO.OUT)				#RedLED2
GPIO.setup(20, GPIO.IN, pull_up_down=GPIO.PUD_UP)	#Button

GPIO.setup(12, GPIO.IN, pull_up_down=GPIO.PUD_UP)	#Simulert disconnected
GPIO.setup(6,  GPIO.OUT) 				#Connection light

def setup():
	GPIO.setwarnings(False)

	global server_sock
	global port
	global uuid

	global client_sock
	global client_info

	global timeout

	server_sock=BluetoothSocket( RFCOMM )
	server_sock.bind(("",PORT_ANY))
	server_sock.listen(1)

	port = server_sock.getsockname()[1]

	uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

	advertise_service( server_sock, "AquaPiServer",
		   service_id = uuid,
		   service_classes = [ uuid, SERIAL_PORT_CLASS ],
		   profiles = [ SERIAL_PORT_PROFILE ], 
		   )

	GPIO.output(17, False)
	GPIO.output(22, False)
	GPIO.output(19, False)
	GPIO.output(26, False)
	GPIO.output(6,  False)

	client_sock = None
	client_info = None

	timeout = 10

def decr_timeout():
	global timeout
	timeout -= 0.5
	print("Timing out in " + str(timeout) + " s")


#True betyr at knappen er oppe
global input_state_red
input_state_red = True 

global input_state_green
input_state_green = True 

global input_state_red2
input_state_red2 = True 

global input_state_green2
input_state_green2 = True 

global conn_state
conn_state = True


#False betyr av
global redLightOn
redLightOn = False

global greenLightOn
greenLightOn = False

global redLight2On
redLight2On = False

global greenLight2On
greenLight2On = False

global yellowLightOn
yellowLightOn = False

def changeRedLight():
	global redLightOn
	redLightOn = not redLightOn
	GPIO.output(17, redLightOn)

def changeGreenLight():
	global greenLightOn
	greenLightOn = not greenLightOn
	GPIO.output(22, greenLightOn)

def changeRedLight2():
	global redLight2On
	redLight2On = not redLight2On
	GPIO.output(26, redLight2On)

def changeGreenLight2():
	global greenLight2On
	greenLight2On = not greenLight2On
	GPIO.output(19, greenLight2On)

def changeYellowLight():
	global yellowLightOn
	yellowLightOn = not yellowLightOn
	GPIO.output(6, yellowLightOn)

def data_message():
	global greenLightOn
	global redLightOn
	global greenLight2On
	global redLight2On
	global yellowLightOn
	
	data ={}
	data['driver'] = str(redLightOn).lower()
	data['passenger'] = str(greenLightOn).lower()
	data['backright'] = str(redLight2On).lower()
	data['backleft'] = str(greenLight2On).lower()
	data['connect'] = str(yellowLightOn).lower()
	
    #The ! is necessary because the Android app treats it as its EOL symbol
	return str(data) + "!"

global prev_data
prev_data = data_message()

global input_data

global client_sock
global client_info

client_sock = None
client_info = None

while True:          

	global client_sock
	global client_info

	while (client_sock == None and client_info == None):
	
		setup()

		print "Waiting for connection on RFCOMM channel %d" % port

		try:

			client_sock, client_info = server_sock.accept()
			print "Accepted connection from ", client_info
		
			#Set socket to non-blocking mode
			client_sock.setblocking(0)

			#When connection is accepted, start sending signals

		except BluetoothError:
			pass

		time.sleep(3)

	input_state_red = GPIO.input(18)
	input_state_green = GPIO.input(23)
	input_state_red2 = GPIO.input(20)
	input_state_green2 = GPIO.input(16)
	conn_state = GPIO.input(12)

	try:

		if input_state_red == False:
			time.sleep(0.1)
			changeRedLight()

		if input_state_green == False:
			time.sleep(0.1)
			changeGreenLight()

		if input_state_red2 == False:
			time.sleep(0.1)
			changeRedLight2()

		if input_state_green2 == False:
			time.sleep(0.1)
			changeGreenLight2()
	
		if conn_state == False:
			time.sleep(0.1);
			changeYellowLight()

		data = data_message()

		client_sock.send(data)
	
		if (data != prev_data and prev_data != None):
        #Only show message sent when it is a new one, to reduce outputted text

			print "sending [%s]" % data

	except IOError:
		pass

	except KeyboardInterrupt:

		print "disconnected"

		client_sock.close()
		server_sock.close()
		print "all done"

		GPIO.output(17, False)
		GPIO.output(22, False)
		GPIO.output(19, False)
		GPIO.output(26, False)
		GPIO.output(6,  False)

		break

	try:

		global prev_data
		prev_data = data

		global input_data

		global client_sock
		global client_info

		print("Receiving data")

		input_data = None

		#Waiting for signal for 1 second
		ready = select.select([client_sock], [], [], 1)
		
		try:
			if ready[0]:
				global input_data
				input_data = client_sock.recv(1024)
		except IOError:
			#Did not receive anything. Assuming None
			global input_data
			input_data = None
		
		global input_data
		print("Received " + str(input_data))

		if input_data == None:
			#Didn't receive anything, decrease timeout
			decr_timeout()
		else:
			#Everything OK
			pass
			
	except IOError:
		print("IOError occured.")

	if timeout <= 0:
		#Go back to waiting state
		print("Disconnected due to timeout")
		setup()
		

	time.sleep(0.5)

