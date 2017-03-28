//
// Created by meirsho on 1/18/17.
//

#ifndef UNTITLED2_SERVERTASK_H
#define UNTITLED2_SERVERTASK_H

#include <iostream>
#include "connectionHandler.h"
#include "Client.h"
class ServerTask {
private:
    ConnectionHandler* serverCh;
    Client* currClient;
public:
    ServerTask(ConnectionHandler* c,Client* client);
    void run();
    void operator()();
};


#endif //UNTITLED2_SERVERTASK_H
