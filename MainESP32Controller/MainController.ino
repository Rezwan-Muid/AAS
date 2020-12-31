#include <Adafruit_Fingerprint.h>
#include <WiFi.h>
#include <Wire.h>

#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x3F, 16, 2);

const char* ssid     = "iut";
const char* password = "11111111";

Adafruit_Fingerprint finger = Adafruit_Fingerprint(&Serial2);
#include <Keypad.h>

const byte ROWS = 4; //four rows
const byte COLS = 4; //three columns
char keys[ROWS][COLS] = {
  {'1', '2', '3', 'A'},
  {'4', '5', '6', 'B'},
  {'7', '8', '9', 'C'},
  {'*', '0', '#', 'D'}
};
byte rowPins[ROWS] = {12, 13, 14, 27}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {25, 26, 32, 33}; //connect to the column pinouts of the keypad

Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );


void enroll_fingerprint(Adafruit_Fingerprint finger);
int give_attendance(Adafruit_Fingerprint finger);

void setup() {
  Serial.begin(9600);
  delay(1000);

  lcd.begin();

  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");

  lcd.print("Connecting to");
  lcd.setCursor(0, 1);
  lcd.print(ssid);
  delay(1000);

  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(600);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");

  lcd.clear();
  lcd.home();
  lcd.print("WiFi Connected");
  delay(1000);
  

  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(500);
}

void loop() {
  const uint16_t port = 80;
  const char * host = "192.168.43.210"; // ip or dns
  lcd.clear();
  lcd.home();
  Serial.print("connecting to ");
  lcd.print("connecting to ");
  lcd.setCursor(0, 1);
  Serial.println(host);
  lcd.print(host);
  delay(500);

  // Use WiFiClient class to create TCP connections
  WiFiClient client;

  if (!client.connect(host, port)) {
    lcd.clear();
    lcd.home();
    Serial.println("connection fail");
    lcd.print("connection fail");

    lcd.setCursor(0, 1);
    Serial.println("wait 5 sec...");
    lcd.print("wait 5 sec...");
    delay(5000);
    return;
  }


  Serial.println("Connection Established");
  lcd.clear();
  lcd.home();
  lcd.print("Connection");
  lcd.setCursor(0, 1);
  lcd.print("Established");
  delay(1000);

  while (true)
  {
    String line = client.readStringUntil('\n');
    Serial.print("Received data: ");
    Serial.println(line);

    int control = line.toInt();
    int send_data = 100;
    Serial.flush();

    // give attendance
    if (control == 1)
    {
      while (send_data != 0) //secret code for aborting attendance
      {
        send_data = give_attendance(finger);
        if (send_data != 0)
        {
          client.println(String(send_data));
          delay(5000);

          line = client.readStringUntil('\n');
          Serial.print("Received data: ");
          Serial.println(line);

          if (line.toInt() == 69)
          {
            Serial.println("Attendance Successfully Given\n");

            lcd.clear();
            lcd.home();
            lcd.print("Attendance");
            lcd.setCursor(0, 1);
            lcd.print("Confirmed");
            delay(2000);
          }
        }
      }
      client.println("0");
      delay(5000);
      break;
    }
    else if (control == 2)
    {
      enroll_fingerprint(finger);
      client.println("0");
      delay(5000);
      break;
    }
    else if (control == 1569)
    {
      Serial.println("closing connection");
      client.flush();
      client.stop();
      Serial.println("Connection Closed");
      delay(5000);
      break;
    }
  }

}

int readnum(void) {
  int num;

  while (! Serial.available());
  num = Serial.parseInt();

  return num;
}
