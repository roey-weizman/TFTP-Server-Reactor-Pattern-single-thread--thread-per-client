/*
 * ServicePacket.cpp
 *
 *  Created on: Jan 12, 2017
 *      Author: meirsho
 */

#include "../include/ServicePacket.h"
#include<iostream>

ServicePacket::ServicePacket(short opcode) :
        opcode(opcode) {
}
string ServicePacket::toString() {
    return "" + opcode;
}
short ServicePacket::getOpCode() {
    return opcode;
}
ServicePacket::~ServicePacket() {
    // TODO Auto-generated destructor stub
}




AckPacket::AckPacket(short opcode, short blockNum) :
        ServicePacket::ServicePacket(opcode), blockNum(blockNum) {
}
string AckPacket::toString() {

    return "ACK";
}
short AckPacket::getBlockNumber() {
    return blockNum;
}




BcastPacket::BcastPacket(short opcode, char del, string name) :ServicePacket(opcode), delOrAdd(del), fileName(name) {}
string BcastPacket::toString() {
    return "BCAST";
}
char BcastPacket::getDelOrAdd() {
    return delOrAdd;
}
string BcastPacket::getFileName() {
    return fileName;
}




DataPacket::DataPacket(short opcode, short packetSize, short numOfBlock,char* currentDataBlockSize) :ServicePacket(opcode), packetSize(packetSize), numOfBlock(numOfBlock),currentDataBlockSize(currentDataBlockSize) {
}
string DataPacket::toString() {
    return "DataPacket";
}
short DataPacket::getPacketSize() {
    return packetSize;
}
char* DataPacket::getCurrentDataBlockSize() {
    return currentDataBlockSize;
}
short DataPacket::getNumOfBlock() {
    return numOfBlock;
}





DeleteReq::DeleteReq(short opcode, string filename) :
        ServicePacket::ServicePacket(opcode), fileName(filename) {
}
string DeleteReq::toString() {
    return "DELRQ ";
}
string DeleteReq::getFileName() {
    return fileName;
}



DirectoryReq::DirectoryReq(short opcode) :
        ServicePacket::ServicePacket(opcode) {
}
string DirectoryReq::toString() {
    return "DIRQ";
}





Disconnect::Disconnect(short opcode) :
        ServicePacket::ServicePacket(opcode) {
}
string Disconnect::toString() {
    return "DISC";
}






ErrorPacket::ErrorPacket(short opcode, short errorcode, string errormessage) :
        ServicePacket::ServicePacket(opcode), errorCode(errorcode), errorMesssage(
        errormessage) {
}
string ErrorPacket::toString() {
    return "Error ";
}
short ErrorPacket::getErrorCode() {
    return errorCode;
}
string ErrorPacket::getErrorMessage() {
    return errorMesssage;
}


BullShit::BullShit(short opcode) :ServicePacket::ServicePacket(opcode) {
}


LoginPacket::LoginPacket(short opcode, string user) :
        ServicePacket::ServicePacket(opcode), username(user) {
}
string LoginPacket::toString() {
    return "LOGRQ ";
}
string LoginPacket::getUserName() {
    return username;
}




ReadReq::ReadReq(short opcode, string filename) :
        ServicePacket::ServicePacket(opcode), fileName(filename) {
}
string ReadReq::toString() {
    return "RRQ ";
}
string ReadReq::getFileName() {
    return fileName;
}




WriteReq::WriteReq(short opcode, string filename) :
        ServicePacket::ServicePacket(opcode), fileName(filename) {
}
string WriteReq::toString() {
    return "WRQ ";
}
string WriteReq::getFileName() {
    return fileName;
}

