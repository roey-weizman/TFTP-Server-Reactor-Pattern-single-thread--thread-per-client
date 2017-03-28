/*
 * ServicePacket.h
 *
 *  Created on: Jan 12, 2017
 *      Author: meirsho
 */

#ifndef SERVICEPACKET_H_
#define SERVICEPACKET_H_
#include <string>
#include "string.h"
using namespace std;
class ServicePacket {


public:
    short opcode;
    ServicePacket(short opcode);
    virtual ~ServicePacket();
    virtual string toString();
    short getOpCode();
};

class AckPacket: public ServicePacket {
private:
    short blockNum;
public:
    AckPacket(short opcode, short blockNum);
    string toString();
    short getBlockNumber();
};
class BullShit: public ServicePacket {
private:
public:
    BullShit(short opcode);

};
class BcastPacket: public ServicePacket {
private:
    char delOrAdd;
    string fileName;
public:
    BcastPacket(short opcode, char deloradd, string name);
    string toString();
    char getDelOrAdd();
    string getFileName();
};
class DataPacket: public ServicePacket {
private:
    short packetSize;
    short numOfBlock;
    char* currentDataBlockSize;
public:
    DataPacket(short opcode, short packetSize, short numOfBlock,
               char* currentDataBlockSize);
    string toString();
    short getPacketSize();
    char* getCurrentDataBlockSize();
    short getNumOfBlock();

};

class DeleteReq: public ServicePacket {
private:
    string fileName;
public:
    DeleteReq(short opcode, string filename);
    string toString();
    string getFileName();

};
class DirectoryReq: public ServicePacket {
private:
public:
    DirectoryReq(short opcode);
    string toString();

};

class Disconnect: public ServicePacket {
private:
public:
    Disconnect(short opcode);
    string toString();

};

class ErrorPacket: public ServicePacket {
private:
    short errorCode;
    string errorMesssage;
public:
    ErrorPacket(short opcode, short errorcode, string errormessage);
    string toString();
    short getErrorCode();
    string getErrorMessage();

};
class LoginPacket: public ServicePacket {
private:
    string username;
public:
    LoginPacket(short opcode, string user);
    string toString();
    string getUserName();
};
class ReadReq: public ServicePacket {
private:
    string fileName;
public:
    ReadReq(short opcode, string filename);
    string toString();
    string getFileName();
};
class WriteReq: public ServicePacket {
private:
    string fileName;
public:
    WriteReq(short opcode, string filename);
    string toString();
    string getFileName();
};
#endif /* SERVICEPACKET_H_ */
