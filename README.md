#RCDroid

Android application and platform setup for controlling a simple exploration robot

##Main Components

###Raspberry Pi [[link](http://www.raspberrypi.org/faqs)]
Small Linux computer used for handling most processing. Runs the server script (name & link), written in Python. Provides two USB ports, one of which is used for the webcam and the other for either a Bluetooth or WiFi dongle. I chose to utilize the Raspberry Pi because of its relatively cheap price, small size, and ease of use.

###Arduino Uno w/ motor shield [[link](http://arduino.cc)]
Prototyping board useful for its analog/digital IO. Receives message packets from the Raspberry Pi that request it to perform various actions. So far, with the help of this [motor shield](http://www.adafruit.com/products/81), the Arduino only controls two DC motors that drive and steer the robot. In the future, I intend on adding a servo for tilting the on board camera and interfacing with a [distance sensor](http://amzn.com/B004U8TOE6), both of which will also be handled by the Arduino. Here(LINK) is the code that is ran on it.

###Tamiya tank-style chassis [[link](http://www.tamiyausa.com/items/geniuseries-educational-kits-50/educational-construction-38000/tracked-vehicle-chassis-kit-70108)]
Tracked model kit that works well for the intended size. The kit comes with a gearbox and motor; however, in order to be able to steer, I replaced it a [dual gearbox](http://www.tamiyausa.com/items/geniuseries-educational-kits-50/educational-construction-38000/twin-motor-gearbox-70097) from the same manufacture. I also replaced the included motors with two [higher voltage DC motors](http://www.adafruit.com/products/711) since the included ones operated at a lower voltage than the motor shield (3V instead of 5-9V).

###PlayStation EyeToy camera
In order to stream images from the robot to the android device, all I needed was a USB webcam. Instead of purchasing one, I opted to use an EyeToy camera that I had for a PS2. Conveniently, the model I have (with the OV519 chip) is already supported with drivers on the Raspberry Pi. On the Raspberry Pi, the server script periodically calls *streamer*, a utility from the [xawtv package](https://www.kraxel.org/blog/linux/xawtv/), which saves an image snapshot from the camera, and then sends it to the android device. The EyeToy also has a built in microphone, which I am considering on utilizing for this robot project in the future.

###Power Supply
There are two 7.2V NiMH rechargeable batteries to provide power for the various components on the robot. The reason I chose to use two separate batteries is because I've read that motors tend to produce a lot of noise on the wires, which can be detrimental to the operation of other devices using the same source of power. Therefore, one battery provides power for just the motors, while the other supplies the remaining components. However, having two batteries has added a significant amount of weight and has taken up a lot of precious space; there probably is a more efficient method out there for supplying power to this size of robot.

Additionally, I added this [5V switching voltage regulator](http://www.dimensionengineering.com/products/de-sw050) for the Raspberry Pi to ensure it was supplied with the proper voltage. Although I could have used a less expensive basic voltage regulator, they tend to be a lot less efficient and produce more heat.

##Communication Schemes

###Android - Raspberry Pi
Bluetooth communication is handled using [PyBluez](https://code.google.com/p/pybluez/). WiFi communication is handled using Python's built in [socket library](http://docs.python.org/2/library/socket.html).

###Raspberry Pi - Arduino
