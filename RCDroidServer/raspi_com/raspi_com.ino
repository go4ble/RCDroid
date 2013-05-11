#include <cstring>
#include <AFMotor.h>
// #include <Servo.h>

#define STX 0x02
#define ETX 0x03

AF_DCMotor leftMotor(2, MOTOR34_1KHZ);
AF_DCMotor rightMotor(3, MOTOR12_1KHZ);
// Servo servo;

void setup() {
  Serial.begin(9600);
  // servo.attach(9);
  // servo.write(0);
}

void loop() {
  if (getChar() == STX) {
    int offset = 0;
    char buffer[16];
    char c = getChar();
    while (c != ETX && offset < 16) {
      buffer[offset] = c;
      offset++;
      c = getChar();
    }
    analyzeMessage(buffer, offset);
  }
}

char getChar() {
  while (true) {
    if (Serial.available() > 0) {
      return Serial.read();
    }
  }
}

void analyzeMessage(char *buf, int len) {
  buf[len] = '\0';
  // Serial.println(buf);
  if (strncmp(buf, "LMSxxx", 3) == 0) {
    // left motor
    int spd = atoi(buf + 3) - 255;
    if (spd == 0) {
      leftMotor.run(RELEASE);
    } 
    else if (spd > 0) {
      leftMotor.run(BACKWARD);
      leftMotor.setSpeed(spd);
    } 
    else {  // spd < 0
      leftMotor.run(FORWARD);
      leftMotor.setSpeed(abs(spd));
    }
  } 
  else if (strncmp(buf, "RMSxxx", 3) == 0) {
    // right motor
    int spd = atoi(buf + 3) - 255;
    if (spd == 0) {
      rightMotor.run(RELEASE);
    } 
    else if (spd > 0) {
      rightMotor.run(BACKWARD);
      rightMotor.setSpeed(spd);
    } 
    else {  // spd < 0
      rightMotor.run(FORWARD);
      rightMotor.setSpeed(abs(spd));
    }
  } 
  else if (strncmp(buf, "SASxxx", 3) == 0) {
    // servo angle
    // int angle = atoi(buf + 3);
    // servo.write(angle);
  }
}
//

