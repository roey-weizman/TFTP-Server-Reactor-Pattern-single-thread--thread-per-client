//
// Created by meirsho on 1/18/17.
//

#ifndef UNTITLED2_KEYBOARDTASK_H
#define UNTITLED2_KEYBOARDTASK_H


#include "connectionHandler.h"
#include "Client.h"
#include <iostream>
class KeyBoardTask {
private:
    ConnectionHandler* myCh;
    Client* myClient;
public:
    KeyBoardTask(ConnectionHandler* c,Client* currClient);
    void operator()();
    void run();
};


#endif //UNTITLED2_KEYBOARDTASK_H
