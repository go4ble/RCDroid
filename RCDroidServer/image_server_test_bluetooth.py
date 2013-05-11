#!/usr/bin/python

import bluetooth
import subprocess
import time

UUID = "00001101-0000-1000-8000-00805F9B34FB"
IMAGE_FILE = "/tmp/capture.jpeg"
IMAGE_DEV = "/dev/video0"  # camera location
IMAGE_DELAY = 0.5  # time in seconds

server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
server_sock.bind(("", 3))
server_sock.listen(1)

bluetooth.advertise_service(server_sock, "image server",
    service_id=UUID,
    service_classes=[bluetooth.SERIAL_PORT_CLASS,],
    profiles=[bluetooth.SERIAL_PORT_PROFILE,])

sock, addr = server_sock.accept()
print "Connected:", addr
bluetooth.stop_advertising(server_sock)
server_sock.close()

subprocess.call(["touch", IMAGE_FILE])
image_file = open(IMAGE_FILE, "r")

while True:
    time.sleep(IMAGE_DELAY)
    subprocess.call(["streamer", "-q", "-c", IMAGE_DEV, "-o", IMAGE_FILE])
    image_file.seek(0)
    image = image_file.read()
    image = "{0}:{1}".format(len(image), image)
    sock.sendall(image)
