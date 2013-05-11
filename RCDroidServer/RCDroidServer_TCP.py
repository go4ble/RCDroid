#!/usr/bin/python

import serial
import signal
import socket
import subprocess
import threading
import time

STX = "\x02"
ETX = "\x03"

HOST = ""
SOCK_TIMEOUT = 15
ARDUINO_PORT = 31415
ARDUINO_FILE = "/dev/ttyACM0"
IMAGE_PORT = 31416
IMAGE_FILE = "/tmp/capture.jpeg"
IMAGE_DEV = "/dev/video0"  # camera location
IMAGE_DELAY = 0.25  # time in seconds

stop_exec = threading.Event()
stop_exec.clear()

def ArduinoThread():
    # get client socket
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_sock.bind((HOST, ARDUINO_PORT))
    server_sock.listen(1)
    server_sock.settimeout(SOCK_TIMEOUT)
    try:
        arduino_sock, addr = server_sock.accept()
    except socket.timeout:
        print "Arduino server socket timed out"
        return
    print "Arduino thread connected to ", addr
    server_sock.close()
    arduino_sock.settimeout(SOCK_TIMEOUT)

    # get file object for writing to
    #arduino_file = open(ARDUINO_FILE, "w", 0)
    arduino_file = serial.Serial(ARDUINO_FILE)

    while not stop_exec.is_set():
        # get message <size>:<message>
        try:
            buf = arduino_sock.recv(1)
        except socket.timeout:
            continue
        while buf[-1] != ":":
            buf += arduino_sock.recv(1)
        size = int(buf[0:-1])
        message = arduino_sock.recv(size)

        # pass message onto arduino
        print message
        arduino_file.write("{0}{1}{2}".format(STX, message, ETX))

    # clean up
    print "Stopping arduino thread"
    arduino_sock.close()
    arduino_file.close()

def ImageThread():
    # get client socket
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_sock.bind((HOST, IMAGE_PORT))
    server_sock.listen(1)
    server_sock.settimeout(SOCK_TIMEOUT)
    try:
        image_sock, addr = server_sock.accept()
    except socket.timeout:
        print "Image server socket timed out"
        return
    print "Image thread connected to ", addr
    server_sock.close()
    image_sock.settimeout(SOCK_TIMEOUT)

    # get file object for reading image
    subprocess.call(["touch", IMAGE_FILE])
    image_file = open(IMAGE_FILE, "r")

    while not stop_exec.is_set():
        time.sleep(IMAGE_DELAY)
        print image_sock.recv(1024)
        subprocess.call(["streamer", "-q", "-c", IMAGE_DEV, "-o", IMAGE_FILE])
        image_file.seek(0)
        image = image_file.read()
        image = "{0}:{1}".format(len(image), image)
        try:
            image_sock.sendall(image)
        except:
            continue

    # clean up
    print "Stopping image thread"
    image_sock.close()
    image_file.close()

arduino_thread = threading.Thread(target=ArduinoThread)
arduino_thread.start()

image_thread = threading.Thread(target=ImageThread)
image_thread.start()

# handle ctrl+c
def sigint_handler(signum, frame):
    print "Stopping threads"
    stop_exec.set()
    exit()

signal.signal(signal.SIGINT, sigint_handler)

while True:
    pass
