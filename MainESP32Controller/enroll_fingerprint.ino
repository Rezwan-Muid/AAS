
void enroll_fingerprint(Adafruit_Fingerprint finger)
{
  delay(100);

  lcd.begin();

  lcd.backlight();

  lcd.setCursor(0, 0);

  Serial.println("\n\nFingerprint enrollment");
  lcd.print("Fingerprint");
  lcd.setCursor(0, 1);
  lcd.print("Enrollment");
  delay(1000);

  // set the data rate for the sensor serial port
  finger.begin(57600);

  if (finger.verifyPassword()) {
    Serial.println("fingerprint sensor ACTIVATED!");
    lcd.clear();
    lcd.home();
    lcd.print("sensor ACTIVATED");
    delay(600);
  } else {
    Serial.println("Did not find fingerprint sensor :(");

    lcd.clear();
    lcd.home();
    lcd.print("sensor not found");

    while (1) {
      delay(1);
    }
  }
  delay(5000);
  while (true)
  {
    Serial.println("Ready to enroll a fingerprint!\n");
    Serial.println("Please type in the ID # (from 1 to 127) or enter 0 to abort attendance enrollment process\n");

    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Enter ID");
    lcd.setCursor(0, 1);
    delay(500);

    int id = readnumber();
    if (!id) {
      Serial.println("Aborting Attendance Enrollment.....\n");
      return;
    }
    Serial.print("Enrolling ID #");
    Serial.println(id);

    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Enrolling ID #");
    lcd.print(id);
    delay(1000);
    lcd.clear();

    getFingerprintEnroll(id);
  }
}

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


uint8_t getFingerprintEnroll(int id) {

  int p = -1;
  Serial.print("Waiting for valid finger to enroll as #"); Serial.println(id);
  
    lcd.home();
    lcd.print("Put your finger");
  
  while (p != FINGERPRINT_OK) {
    p = finger.getImage();
    switch (p) {
      case FINGERPRINT_OK:
        Serial.println("Image taken");
        
          lcd.setCursor(0, 1);
          lcd.print("Image taken");
        
        break;
      case FINGERPRINT_NOFINGER:
        Serial.print(".");
        break;
      case FINGERPRINT_PACKETRECIEVEERR:
        Serial.println("Communication error");
        break;
      case FINGERPRINT_IMAGEFAIL:
        Serial.println("Imaging error");
        break;
      default:
        Serial.println("Unknown error");
        break;
    }
  }

  // OK success!

  p = finger.image2Tz(1);
  switch (p) {
    case FINGERPRINT_OK:
      Serial.println("Image converted");
      break;
    case FINGERPRINT_IMAGEMESS:
      Serial.println("Image too messy");
      return p;
    case FINGERPRINT_PACKETRECIEVEERR:
      Serial.println("Communication error");
      return p;
    case FINGERPRINT_FEATUREFAIL:
      Serial.println("Could not find fingerprint features");
      return p;
    case FINGERPRINT_INVALIDIMAGE:
      Serial.println("Could not find fingerprint features");
      return p;
    default:
      Serial.println("Unknown error");
      return p;
  }

  Serial.println("Remove finger");
  
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Remove finger");
  
  delay(2000);
  p = 0;
  while (p != FINGERPRINT_NOFINGER) {
    p = finger.getImage();
  }
  Serial.print("ID "); Serial.println(id);
  p = -1;
  Serial.println("Place same finger again");
  
    lcd.clear();
    lcd.home();
    lcd.print("Place again");
  
  while (p != FINGERPRINT_OK) {
    p = finger.getImage();
    switch (p) {
      case FINGERPRINT_OK:
        Serial.println("Image taken");
        
          lcd.setCursor(0, 1);
          lcd.print("Image taken");
        
        break;
      case FINGERPRINT_NOFINGER:
        Serial.print(".");
        break;
      case FINGERPRINT_PACKETRECIEVEERR:
        Serial.println("Communication error");
        break;
      case FINGERPRINT_IMAGEFAIL:
        Serial.println("Imaging error");
        break;
      default:
        Serial.println("Unknown error");
        break;
    }
  }

  // OK success!

  p = finger.image2Tz(2);
  switch (p) {
    case FINGERPRINT_OK:
      Serial.println("Image converted");
      break;
    case FINGERPRINT_IMAGEMESS:
      Serial.println("Image too messy");
      return p;
    case FINGERPRINT_PACKETRECIEVEERR:
      Serial.println("Communication error");
      return p;
    case FINGERPRINT_FEATUREFAIL:
      Serial.println("Could not find fingerprint features");
      return p;
    case FINGERPRINT_INVALIDIMAGE:
      Serial.println("Could not find fingerprint features");
      return p;
    default:
      Serial.println("Unknown error");
      return p;
  }

  // OK converted!
  Serial.print("Creating model for #");  Serial.println(id);

  p = finger.createModel();
  if (p == FINGERPRINT_OK) {
    Serial.println("Prints matched!");
    
      lcd.clear();
      lcd.home();
      lcd.print("Matched!");
    
  } else if (p == FINGERPRINT_PACKETRECIEVEERR) {
    Serial.println("Communication error");
    return p;
  } else if (p == FINGERPRINT_ENROLLMISMATCH) {
    Serial.println("Fingerprints did not match");
    
      lcd.clear();
      lcd.home();
      lcd.print("Didn't Match!");
    
    return p;
  } else {
    Serial.println("Unknown error");
    return p;
  }

  Serial.print("ID "); Serial.println(id);
  p = finger.storeModel(id);
  if (p == FINGERPRINT_OK) {
    Serial.println("Stored!");
    
      lcd.clear();
      lcd.home();
      lcd.print("Enrolled!");
    
    delay(1000);
    return p;
  } else if (p == FINGERPRINT_PACKETRECIEVEERR) {
    Serial.println("Communication error");
    return p;
  } else if (p == FINGERPRINT_BADLOCATION) {
    Serial.println("Could not store in that location");
    
      lcd.clear();
      lcd.home();
      lcd.print("Could not enroll");
    
    return p;
  } else if (p == FINGERPRINT_FLASHERR) {
    Serial.println("Error writing to flash");
    return p;
  } else {
    Serial.println("Unknown error");
    return p;
  }
}
