//
// Created by meirsho on 1/18/17.
//

#include "../include/KeyBoardTask.h"

KeyBoardTask::KeyBoardTask(ConnectionHandler* c,Client* currClient):myCh(c),myClient(currClient){
}
void KeyBoardTask::operator()(){
    run();
}
void KeyBoardTask::run(){
    while(myClient->getNotInterupted()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        ServicePacket *PacketToBeSent = myClient->getPacket(line);
        myCh->sendPacket(PacketToBeSent);
    }
    return;
}
