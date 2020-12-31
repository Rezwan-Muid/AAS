
int give_attendance(Adafruit_Fingerprint finger)
{
  int id;

  lcd.begin();

  lcd.backlight();

  lcd.setCursor(0, 0);

  Serial.println("\n\nAUTOMATED ATTENDANCE SYSTEM");

  lcd.print("AUTOMATED");
  lcd.setCursor(0, 1);
  lcd.print("ATTENDANCE");

  delay(3000);
  // set the data rate for the sensor serial port
  finger.begin(57600);

  if (finger.verifyPassword()) {
    Serial.println("Fingerprint sensor ACTIVATED!");
  } else {
    Serial.println("Did not find fingerprint sensor :(");
    while (1) {
      delay(1);
    }
  }

  finger.getTemplateCount();
  Serial.print("Sensor contains "); Serial.print(finger.templateCount); Serial.println(" fingerprints");



  while (true)
  {
    lcd.clear();
    lcd.home();
    lcd.print("Press 1 to start");
    lcd.setCursor(0, 1);
    lcd.print("or 0 to abort");

    int id = readnumber();
    if (!id) {
      Serial.println("Aborting Attendance Giving\n");
      lcd.clear();
      lcd.home();
      lcd.print("Aborting...");
      return 0;
    }

    Serial.println("put your finger on the scanner");

    lcd.clear();
    lcd.home();
    lcd.print("Put your finger");

    int final_id = getFingerprintIDez();

    // if no error occurs then return the id
    if (final_id != -1)
      return final_id;
  }
}


/*
  int readnumber(void) {
  String in = "";
  char key;
  do {
    key = keypad.getKey();
    if (isDigit(key))
    {
      in += key;
      delay(500);
      lcd.print(key);
    }
    if (key != NO_KEY) {
      Serial.println(key);
    }
  } while (key != '*');
  int num = in.toInt();
  Serial.println(num);
  return num;
  }
*/

// returns -1 if failed, otherwise returns ID #
int getFingerprintIDez(void) {
  uint8_t p = -1;
  while (p != FINGERPRINT_OK)
  {
    p = finger.getImage();
    delay(50); //don't need to run this at full speed.
  }

  p = finger.image2Tz();
  if (p != FINGERPRINT_OK)
  {
    Serial.println("Image Conversion Error\n");
    return -1;
  }

  p = finger.fingerFastSearch();
  if (p != FINGERPRINT_OK)
  {
    Serial.println("Fingerprint doesn't found in the database\n");
    Serial.print(" with confidence of "); Serial.println(finger.confidence);
    lcd.clear();
    lcd.home();
    lcd.print("doesn't match ");
    delay(1500);

    return -1;
  }

  // found a match!
  else
  {
    Serial.print("Found ID #"); Serial.println(finger.fingerID);

    lcd.clear();
    lcd.home();
    lcd.print("Found id ");
    lcd.print(finger.fingerID);

    return finger.fingerID;
  }

  Serial.print(" with confidence of "); Serial.println(finger.confidence);

}
