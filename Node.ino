/* 
  NORTH Internet of Things
  Node System
  Version 0.0.0 / **.**.20**
*/

#include <NewPing.h>    // Gerekli kütüphaneler import edildi.
#include <LoRa_E32.h>
#include <SoftwareSerial.h>

#define SENSOR_NUM 3    // Gerekli tanımlamalar yapıldı.
#define LORA_TX_PIN 5
#define LORA_RX_PIN 6
#define TRIGGER_PIN_1 7
#define ECHO_PIN_1 8
#define TRIGGER_PIN_2 9
#define ECHO_PIN_2 10
#define TRIGGER_PIN_3 11
#define ECHO_PIN_3 12
#define MAX_DISTANCE 200

// Sensör nesneleri oluşturuldu. 3 sensör de bu array üzerinden sürülecek.

NewPing sensor[SENSOR_NUM] = {
  NewPing(TRIGGER_PIN_1, ECHO_PIN_1, MAX_DISTANCE), 
  NewPing(TRIGGER_PIN_2, ECHO_PIN_2, MAX_DISTANCE),
  NewPing(TRIGGER_PIN_3, ECHO_PIN_3, MAX_DISTANCE)
};
SoftwareSerial comSerial(LORA_TX_PIN, LORA_RX_PIN);
LoRa_E32 e32ttl(&comSerial);

static const uint32_t serialBaud = 115200;

unsigned int distance[SENSOR_NUM];

struct NodePoint {
  bool free[SENSOR_NUM] = {true, true, true};
}nodePoint;

void setup() {
  Serial.begin(serialBaud);
  e32ttl.begin();
  Serial.println("*****************************");
  Serial.println("* NORTH  Internet of Things *");
  Serial.println("*****************************");
}

void loop() {
  /*
  Sensörler park alanları ile ilgili ölçümleri bu kısımda yapıyor. Belli bir mesafe altındaki
  algılamaları dolu olarak değerlendiriyor ve bilgiler array içerisine aktarılıyor.
  */
  for(uint8_t i=0; i<SENSOR_NUM; i++) {
    distance[i] = sensor[i].ping_cm();
  }

  if(distance[0] < 130) nodePoint.free[0] = false;
  else if(distance[0] == 0) nodePoint.free[0] = true;
  else nodePoint.free[0] = true;

  if(distance[1] < 90) nodePoint.free[1] = false;
  else if(distance[1] == 0) nodePoint.free[1] = true;
  else nodePoint.free[1] = true;

  if(distance[2] < 130) nodePoint.free[2] = false;
  else if(distance[2] == 0) nodePoint.free[2] = true;
  else nodePoint.free[2] = true;

  dataOut(nodePoint.free);
  delay(200);
}

/*
Verilerin gönderileceği fonksiyon yazıldı. Bu fonksiyonda otoparkların dolu boş bilgileri array halinde fonksiyona
gönderiliyor ve struct kullanılarak alıcı istasyona aktarılıyor.
*/
void dataOut(bool freeArray[]) {
  struct NodeDataStruct {
    uint8_t spotFree[3];
  }NodeDataPacket;

  *(int* )NodeDataPacket.spotFree[0] = freeArray[0];
  *(int* )NodeDataPacket.spotFree[1] = freeArray[1]; 
  *(int* )NodeDataPacket.spotFree[2] = freeArray[2]; 

  ResponseStatus rs = e32ttl.sendFixedMessage(0, 10, 23, &NodeDataPacket, sizeof(NodeDataStruct));

}