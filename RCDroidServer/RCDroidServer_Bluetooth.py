#!/usr/bin/python

import bluetooth
#import RPi.GPIO as gpio
import serial
import signal
import subprocess
import threading
import time

STX = "\x02"
ETX = "\x03"

ARDUINO_DEVICE = "/dev/ttyACM0"
IMAGE_DEVICE = "/dev/video0"

IMAGE_FILE = "/tmp/capture.jpeg"
LED_PIN = 12
SOCK_TIMEOUT = 15
UUID = "00001101-0000-1000-8000-00805F9B34FB"

def get_connected_socket(channel):
    server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    server_sock.bind(("", channel))
    server_sock.listen(1)
    # server_sock.settimeout(SOCK_TIMEOUT)
    bluetooth.advertise_service(server_sock, "RC Droid",
        service_id = UUID,
        service_classes = [bluetooth.SERIAL_PORT_CLASS,],
        profiles = [bluetooth.SERIAL_PORT_PROFILE,])
    sock, addr = server_sock.accept()
    print "Connection established:", addr
    bluetooth.stop_advertising(server_sock)
    server_sock.close()
    sock.settimeout(SOCK_TIMEOUT)
    return sock

def ArduinoThread(sock, stop_exec):
    arduino = serial.Serial(ARDUINO_DEVICE)
    while not stop_exec.is_set():
        try:
            c = sock.recv(1)
        except bluetooth.BluetoothError:
            # only stop execution if Event is set
            continue
        # get message size, then get size bytes
        size = ""
        while c != ':':
            size += c
            c = sock.recv(1)
        size = int(size)
        msg = sock.recv(size)
        msg = "{0}{1}{2}".format(STX, msg, ETX)
        arduino.write(msg)

    print "Stopping ArduinoThread"
    sock.close()
    arduino.close()

def ImageThread(sock, stop_exec):
    # call touch in case file doesn't already exist
    subprocess.call(["touch", IMAGE_FILE])
    image = open(IMAGE_FILE)

    while not stop_exec.is_set():
        time.sleep(1)
        # image capture
        subprocess.call(["streamer", "-q", "-c", IMAGE_DEVICE, "-o", IMAGE_FILE])
        image.seek(0)
        msg = image.read()
        msg = "{0}:{1}".format(len(msg), msg)
        sock.sendall(msg)

    print "Stopping ImageThread"
    sock.close()
    image.close()

# handle ctrl+c
def sigint_handler(signum, frame):
    print "Stopping threads"
    stop_exec.set()
    exit()

if __name__ == "__main__":
    # phone seems to connect to RPi only when scan is on
    # turn back off after starting threads
    #subprocess.call(["hciconfig", "hci0", "piscan"])

    # signals threads when to stop execution
    stop_exec = threading.Event()

    # turn on LED to indicate waiting for connection
    '''
    gpio.setmode(gpio.BOARD)
    gpio.setup(LED_PIN, gpio.OUT)
    gpio.output(LED_PIN, gpio.HIGH)
    '''

    # client will be expecting the sockets to be created in this order
    #  1. arduino
    #  2. image

    arduino_sock = get_connected_socket(2)
    arduino_thread = threading.Thread(target=ArduinoThread, args=(arduino_sock, stop_exec,))
    arduino_thread.start()

    '''
    image_sock = get_connected_socket(3)
    image_thread = threading.Thread(target=ImageThread, args=(image_sock, stop_exec,))
    image_thread.start()
    '''

    # turn off LED after connections are made
    '''
    gpio.output(LED_PIN, gpio.LOW)
    gpio.cleanup()
    '''

    # turn scanning back off
    #subprocess.call(["hciconfig", "hci0", "noscan"])

    # catch ctrl+c
    signal.signal(signal.SIGINT, sigint_handler)

    while True:
        pass
