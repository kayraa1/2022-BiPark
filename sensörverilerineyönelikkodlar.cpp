/* 
  NORTH Internet of Things
  Main System
  Version 0.0.0 / **.**.20**
*/

#include <Arduino.h>    // Gerekli kütüphaneler import edildi.
#include <LoRa_E32.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include "Lot.cpp"

#define WIFI_SSID "WIFI ISIM"   // Gerekli tanımlamalar yapıldı.
#define WIFI_PASSWORD "WIFI SIFRE"
#define LORA_TX_PIN 5
#define LORA_RX_PIN 6

SoftwareSerial comSerial(LORA_TX_PIN, LORA_RX_PIN);
LoRa_E32 e32ttl(&comSerial);
Lot lot;

static const uint32_t serialBaud = 115200;

struct NodeDataStruct {
    uint8_t spotFree[3];
}NodeDataPacket;

void setup() {
    Serial.begin(serialBaud);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED){
        Serial.print(".");
        delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();
    e32ttl.begin();     // LoRa haberleşmesi başlatıldı.
}

void loop() {
    
    /*
    Düğüm noktalarından gelen park alanına ait bilgiler bu kısımda dinleniyor.
    Gelen bilgiler oluşturulan park modelindeki ilgili 'member-field'lara aktarılıyor.
    Daha sonra seri porta yazdırılarak gerekli kontroller yapılıyor.
    Bu aşamadan sonra Firebase gerçek zamanlı veri tabanına veriler yazdırılıyor.
    İlerleyen zamanlarda veri tabanındaki bilgiler mobil uygulama üzerinde gösterilmeye başlanılacak.
    */

    while (e32ttl.available()  > 1) {
    ResponseStructContainer rscMain = e32ttl.receiveMessage(sizeof(MainDataStruct));
    struct NodeDataStruct NodeDataPacket = *(NodeDataStruct *) rscMain.data;

    rscMain.close();
    }

    if(NodeDataPacket.spotFree[0] == 1) lot.normalSpot[0].changeStatus(1);
    else lot.normalSpot[0].changeStatus(0);

    if(NodeDataPacket.spotFree[1] == 1) lot.normalSpot[1].changeStatus(1);
    else lot.normalSpot[1].changeStatus(0);

    if(NodeDataPacket.spotFree[2] == 1) lot.normalSpot[2].changeStatus(1);
    else lot.normalSpot[2].changeStatus(0);

    Firebase.RTDB.setInt(&fbdo, "spot/free", lot.normalSpot[0].isFree())
    Firebase.RTDB.setInt(&fbdo, "spot/free", lot.normalSpot[1].isFree())
    Firebase.RTDB.setInt(&fbdo, "spot/free", lot.normalSpot[2].isFree())


    Serial.println("**************************************");
    Serial.println("1. otopark alanının durumu: "); if(normalSpot[0].isFree() == true) Serial.println("BOŞ"); else Serial.println("DOLU");
    Serial.println("**************************************");
    Serial.println("2. otopark alanının durumu: "); if(normalSpot[1].isFree() == true) Serial.println("BOŞ"); else Serial.println("DOLU");
    Serial.println("**************************************");
    Serial.println("3. otopark alanının durumu: "); if(normalSpot[2].isFree() == true) Serial.println("BOŞ"); else Serial.println("DOLU");

    delay(500);
}