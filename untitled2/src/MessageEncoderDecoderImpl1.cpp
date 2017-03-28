

/*
 * MessageEncoderDecoderImpl1.cpp
 *
 *  Created on: Jan 13, 2017
 *      Author: meirsho
 */

#include "../include/MessageEncoderDecoder.h"
#include <vector>
#include <algorithm>
#include<iostream>

using namespace std;
template<typename ServicePacket>
MessageEncoderDecoderImpl1<ServicePacket>::~MessageEncoderDecoderImpl1() {
    // TODO Auto-generated destructor stub
}
template<typename ServicePacket>
vector<char> MessageEncoderDecoderImpl1<ServicePacket>::encode(ServicePacket* message){
    vector<char> ans(0);
    short opcode = message->getOpCode();
    vector<char> temp0 = shortToBytes(opcode);

    vector<char> delimeter(1);
    switch (opcode) {
        case 1: {
            vector<char> temp1 = GetVectorFromString(dynamic_cast<ReadReq*>(message)->getFileName());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp1);
            ans = CombineCharArrays(ans, delimeter);
            break;

        }case 2: {
            vector<char> temp2 = GetVectorFromString(dynamic_cast<WriteReq *>(message)->getFileName());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp2);
            ans = CombineCharArrays(ans, delimeter);
            break;
        }case 3: {

            vector<char> temp3 = shortToBytes((dynamic_cast<DataPacket*>(message))->getPacketSize());
            vector<char> temp31 = shortToBytes(((dynamic_cast<DataPacket*>(message))->getNumOfBlock()));
            vector<char> temp32 = GetVectorFromString(string((dynamic_cast<DataPacket*>(message))->getCurrentDataBlockSize()));
            ans = CombineCharArrays(temp0, temp3);
            ans = CombineCharArrays(ans, temp31);
            ans = CombineCharArrays(ans, temp32);
            break;
        }case 4: {
            vector<char> temp4 = shortToBytes((dynamic_cast<AckPacket*>(message))->getBlockNumber());
            ans = CombineCharArrays(temp0, temp4);
            break;
        }case 5: {
            vector<char> temp5 = shortToBytes((dynamic_cast<ErrorPacket *>(message))->getErrorCode());
            vector<char> temp51 = GetVectorFromString((dynamic_cast<ErrorPacket*>(message))->getErrorMessage());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp5);
            ans = CombineCharArrays(ans, temp51);
            ans = CombineCharArrays(ans, delimeter);
            break;
        }case 6: {
            ans = temp0;
            break;
        }case 7: {

            vector<char> temp7 = GetVectorFromString(dynamic_cast<LoginPacket *>(message)->getUserName());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp7);
            ans = CombineCharArrays(ans, delimeter);

            break;
        }case 8: {
            vector<char> temp8 = GetVectorFromString(dynamic_cast<DeleteReq*>(message)->getFileName());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp8);
            ans = CombineCharArrays(ans, delimeter);
            break;
        }case 9: {
            vector<char> temp9(1);
            temp9[0] = (dynamic_cast<BcastPacket &>(*message)).getDelOrAdd();
            vector<char> temp91 = GetVectorFromString(dynamic_cast<BcastPacket &>(*message).getFileName());
            delimeter[0] = '\0';
            ans = CombineCharArrays(temp0, temp9);
            ans = CombineCharArrays(ans, temp91);
            ans = CombineCharArrays(ans, delimeter);

            break;
        }case 10:{
            ans = temp0;
            break;
        }
        case 11:{
            ans = temp0;
            break;
        }
    }

    return ans;

}
template<typename ServicePacket>
ServicePacket* MessageEncoderDecoderImpl1<ServicePacket>::  decodeNextByte(char nextByte) {

    decoderBuffer[index]=nextByte;
    if (index == 1 && currentOpcode == 0) {
        opcodeArray[0]= decoderBuffer[0];
        opcodeArray[1] = decoderBuffer[1];
        currentOpcode = bytesToShort(opcodeArray);
    }

    if (index >= 1) {
        if( currentOpcode == 10 || currentOpcode == 6){
            return CreateEasyPackets(currentOpcode, decoderBuffer);
        }}
    if (index > 1) {

        if( currentOpcode == 10 || currentOpcode == 6|| currentOpcode==11){
            return CreateEasyPackets(currentOpcode, decoderBuffer);
        }
        if(currentOpcode==4 && index==3){
            char* blocks= GetPartOfCharArray(decoderBuffer, 2, 4);
            short numOfBlocks=bytesToShort(blocks);
            AckPacket *ac=new AckPacket(currentOpcode, numOfBlocks);
           clearFields();
            return ac;
        }
        if(currentOpcode==3){
            if(index >5){
                packetSize= bytesToShort(GetPartOfCharArray(decoderBuffer,2,4));
                if(index+1==packetSize+6) {
                    char * data=GetPartOfCharArray(decoderBuffer, 6, index + 1);
                    DataPacket *d = new DataPacket(currentOpcode, packetSize,
                                                   bytesToShort(GetPartOfCharArray(decoderBuffer, 4, 6)),
                                                   GetPartOfCharArray(decoderBuffer, 6, index + 1));

                    clearFields();
                    return d;
                }
            }
        }
        if (nextByte == '\0'&& (!(currentOpcode == 9))) {
            if (currentOpcode == 1 || currentOpcode == 2 || (currentOpcode == 5) && index > 3 || currentOpcode == 7
                || currentOpcode == 8) {
                return CreateDelimiter0Packets(currentOpcode, decoderBuffer);
            }
        }
        if( nextByte == '\0'&&currentOpcode == 9&&index>3){
            string pack="";
            for(int j=0;j<index;j++) {
                pack += decoderBuffer[j];
            }
                char delOrAdd = decoderBuffer[2];
                string filename5(pack.substr(3,index-1));
            BcastPacket *b=new BcastPacket(currentOpcode, delOrAdd, filename5);
            clearFields();

            return b;
            }





    }
    index++;
    return nullptr;
}
template<typename ServicePacket>
vector<char>  MessageEncoderDecoderImpl1<ServicePacket>::CombineCharArrays(vector<char> a, vector<char> b) {
    vector<char> ans(a.size()+b.size());
    for(unsigned int i=0;i<a.size();i++){
        ans[i]=a[i];
    }
    for(unsigned int j=0;j<b.size();j++){
        ans[a.size()+j]=b[j];//why not i?
    }

    return ans;
}
template<typename ServicePacket>

short MessageEncoderDecoderImpl1<ServicePacket>:: bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;

}
template<typename ServicePacket>
vector<char> MessageEncoderDecoderImpl1<ServicePacket>:: shortToBytes(short num )
{

    vector<char> bytesArr(2);
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);

    return bytesArr;

}
template<typename ServicePacket>
ServicePacket* MessageEncoderDecoderImpl1<ServicePacket>::CreateDelimiter0Packets(short opcode, char* myBuffer) {
    ServicePacket* s = new ServicePacket(opcode);
    string pack="";
    for(int j=0;j<index;j++) {
        pack += myBuffer[j];
    }
    switch (opcode) {
        case 1: {
            string filename(pack.substr(2, pack.size()));
            s = new ReadReq(opcode, filename);
            break;
        }case 2: {
            string filename1(pack.substr(2, pack.size()));
            s = new WriteReq(opcode, filename1);
            break;
        }case 5: {
            short errCode = bytesToShort(GetPartOfCharArray(myBuffer, 2, 4));
            string errMsg(pack.substr(4, pack.size()));
            s = new ErrorPacket(opcode, errCode, errMsg);
            break;
        }case 7: {
            string filename3(pack.substr(2, pack.size()));
            s = new LoginPacket(opcode, filename3);
            break;
        }case 8: {
            string filename4(pack.substr(2, pack.size()));
            s = new DeleteReq(opcode, filename4);
            break;
        }
    }
    clearFields();
    return s;

}
template<typename ServicePacket>
char* MessageEncoderDecoderImpl1<ServicePacket>:: GetPartOfCharArray(char* ch,int from,int to){//include from does not include to.
    int size=to-from;
    char* ans=new char[size];
    for(unsigned int i=0;i<size;i++){
        ans[i]=ch[from+i];
    }
    return ans;

}
template<typename ServicePacket>
ServicePacket* MessageEncoderDecoderImpl1<ServicePacket>::CreateEasyPackets(short opcode, char* myBuffer){
    ServicePacket *s;
    switch (opcode) {
        case 6:
            s= new DirectoryReq(opcode);
            break;
        case 10:
            s= new Disconnect(opcode);
            break;
        case 11:
            s= new BullShit(opcode);
            break;
    }

    clearFields();
    return s;
}
template<typename ServicePacket>
vector<char> MessageEncoderDecoderImpl1<ServicePacket>::GetVectorFromString(string s){

    vector<char> ans(s.length());
    for(unsigned int i=0;i<s.length();i++){
        ans[i]=s[i];
    }

    return ans;

}
template<typename ServicePacket>
char* MessageEncoderDecoderImpl1<ServicePacket>::GetCharArrayFromVector(vector<char> vec){
    char* ans=new char[vec.size()]();
    for(int i=0;i<vec.size();i++) {
        ans[i] = vec[i];
    }

    return ans;
}
template<typename ServicePacket>
void MessageEncoderDecoderImpl1<ServicePacket>::clearFields(){
    decoderBuffer = new char[518];
     index = 0;
     currentOpcode = 0;
     opcodeArray = new char[2];
     packetSize = 0;
}

