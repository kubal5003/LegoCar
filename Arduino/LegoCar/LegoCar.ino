
#include <SoftwareSerial.h>
#include <PWM.h>

#define DELAY 20

#define ENGINE_DIRECTION_PIN_1 7
#define ENGINE_DIRECTION_PIN_2 8
#define ENGINE_SPEED_PIN 9

#define STEERING_WHEEL_PWM_PIN 3
#define STEERING_WHEEL_DIRECTION_PIN_1 4
#define STEERING_WHEEL_DIRECTION_PIN_2 5
#define STEERING_WHEEL_PWM_FREQUENCY 1150

#define COMMAND_LENGTH 20


 
SoftwareSerial BLE(10,11);


void setup() {
   Serial.begin(9600); 
   setupBleConnection();
   setupEngine();
   setupSteeringWheel();
}


String command;
char recvChar;

void loop(){
    if(Serial.available()){
      char ch = Serial.read();
      BLE.write(ch);
    }
    
    if(BLE.available()){
      recvChar = BLE.read();
      
      Serial.print(recvChar);
      if(recvChar == ';'){
        executeCommand(command);
        command = "";
      }
      else{
        command += recvChar;
      }
    }
}

void executeCommand(String cmd){
    Serial.print(cmd);
    if(cmd == "FORWARD") {
      forward();
    }
    else if(cmd == "STOP") {
      stopEngine();
    }
    else if(cmd == "BACKWARD") {
      reverse();
    }
    else {
      int value = cmd.toInt();
      steeringWheel(value);
    }
}

void setupBleConnection(){
  pinMode(10, INPUT);
  pinMode(11, OUTPUT);
  BLE.begin(9600); //Set BLE BaudRate to default baud rate 9600
  BLE.print("AT+CLEAR"); //clear all previous setting
  BLE.print("AT+ROLE1"); //set the bluetooth name as a master
  BLE.print("AT+SAVE1");  //don't save the connect information
}

void setupEngine(){
  pinMode(ENGINE_DIRECTION_PIN_1,OUTPUT);
  pinMode(ENGINE_DIRECTION_PIN_2,OUTPUT);
  pinMode(ENGINE_SPEED_PIN,OUTPUT);
}

void setupSteeringWheel(){
  InitTimersSafe(); 
  bool success = SetPinFrequencySafe(STEERING_WHEEL_PWM_PIN, STEERING_WHEEL_PWM_FREQUENCY);
  pinMode(STEERING_WHEEL_PWM_PIN, OUTPUT);
  pinMode(STEERING_WHEEL_DIRECTION_PIN_1, OUTPUT);
  pinMode(STEERING_WHEEL_DIRECTION_PIN_2, OUTPUT);
}

void forward(){
  analogWrite(ENGINE_SPEED_PIN, 255);//Sets speed variable via PWM 
  digitalWrite(ENGINE_DIRECTION_PIN_1, HIGH);
  digitalWrite(ENGINE_DIRECTION_PIN_2, LOW);
}

void reverse(){
  analogWrite(ENGINE_SPEED_PIN, 255);//Sets speed variable via PWM 
  digitalWrite(ENGINE_DIRECTION_PIN_1, LOW);
  digitalWrite(ENGINE_DIRECTION_PIN_2, HIGH);
}

void stopEngine(){
  analogWrite(ENGINE_SPEED_PIN, 0); 
}

void steeringWheel(int value){

 int pwm_value = min(abs(value) * 37, 255);

 if(value == 0){
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_1,LOW);
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_2,LOW);
 }
 else if(value >0){
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_1,LOW);
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_2,HIGH);
 }
 else{
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_1,HIGH);
   digitalWrite(STEERING_WHEEL_DIRECTION_PIN_2,LOW);
 }
 pwmWrite(STEERING_WHEEL_PWM_PIN, pwm_value);
}





