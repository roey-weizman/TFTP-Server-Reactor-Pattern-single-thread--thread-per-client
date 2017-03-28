//
// Created by meirsho on 1/18/17.
//

#include "../include/ServerTask.h"
ServerTask::ServerTask(ConnectionHandler* c,Client* client):serverCh(c),currClient(client){

}
void ServerTask::operator()(){
    run();
}
void ServerTask::run(){
    while(currClient->getNotInterupted()) {
        ServicePacket *PacketFromServer = serverCh->getPacket();
        if((currClient->askedForFile||currClient->askedForDirc)&&PacketFromServer==nullptr) {
            PacketFromServer = new DataPacket(3, 0, 3, new char(0));
        }
        currClient->dealWithAnswer(PacketFromServer, serverCh);
    }
    return;
}
