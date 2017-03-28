/*
 * echoClient.h
 *
 *  Created on: Jan 11, 2017
 *      Author: meirsho
 */

#ifndef CLIENT_H_
#define CLIENT_H_
#include <iostream>
#include <fstream>
#include "connectionHandler.h"
#include "ServicePacket.h"
#include<vector>
#include "MessageEncoderDecoder.h"
#include <boost/filesystem.hpp>
#include <thread>
#include <stdlib.h>
#include <boost/algorithm/string.hpp>
#include "KeyBoardTask.h"
#include "ServerTask.h"
#include <stdlib.h>
#include <boost/locale.hpp>
#include <boost/thread.hpp>
using boost::asio::ip::tcp;
class Client {
private:
    short blockNum;
    vector<char> dataFile=vector<char>(0);
    bool askingToWrite=false;
    bool askDisc=false;
    string fileNameAsked;
    vector<char> wrqFile;
    char* wrqbuffer=new char[0]();
    int wrqLength;
    bool notInterupted=true;
    int size=0;
    char* ans=new char[0]();
public:
    bool askedForFile=false;
    bool askedForDirc=false;


    vector<char>  CombineCharArrays(vector<char> a, vector<char> b);
     ServicePacket* getPacket(string line);
     void dealWithAnswer(ServicePacket* answer,ConnectionHandler *connectionHandler);
     void readFromClientAndSend(ConnectionHandler* connectionHandler,Client *thisClient);
    bool getNotInterupted();
    void run();
    char* GetPartOfCharArray(char* ch,int from,int to);
    char*  CombineChar(char* a, char* b,int aSize,int bSize);
    };

#endif /* ECHOCLIENT_H_ */
