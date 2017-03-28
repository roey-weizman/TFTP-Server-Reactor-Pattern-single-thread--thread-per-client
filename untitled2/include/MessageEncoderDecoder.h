
/*
 * MessageEncoderDecoder.h
 *
 *  Created on: Jan 13, 2017
 *      Author: meirsho
 */
#include "ServicePacket.h"
#include <vector>
#ifndef MESSAGEENCODERDECODER_H_
#define MESSAGEENCODERDECODER_H_
template<typename T>
class MessageEncoderDecoder {

public:
    MessageEncoderDecoder<T>();
    virtual T decodeNextByte(char nextByte)=0;
    virtual char* encode(T message)=0;
    virtual ~MessageEncoderDecoder(){}
};

template<typename ServicePacket>
class MessageEncoderDecoderImpl1{
private:
    char* decoderBuffer = new char[518];
    int index = 0;
    short currentOpcode = 0;
    char* opcodeArray = new char[2];
    short packetSize = 0;
public:
    vector<char> encode(ServicePacket* message) ;
    ServicePacket* decodeNextByte(char nextByte) ;
    virtual ~MessageEncoderDecoderImpl1();
    vector<char> CombineCharArrays(vector<char> a, vector<char> b);
    short bytesToShort(char* bytesArr);
    vector<char> shortToBytes(short num );
    ServicePacket* CreateDelimiter0Packets(short opcode, char* myBuffer);
    char* GetPartOfCharArray(char* ch,int from,int to);
    ServicePacket* CreateEasyPackets(short opcode, char* myBuffer);
    vector<char> GetVectorFromString(string s);
    char* GetCharArrayFromVector(vector<char> vec);
    void clearFields();

};

#endif /* MESSAGEENCODERDECODER_H_ */

