
#include "../include/Client.h"
#include <boost/thread.hpp>
#include <boost/locale.hpp>

using namespace boost::filesystem;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

vector<char>  Client::CombineCharArrays(vector<char> a, vector<char> b) {
    vector<char> ans(0);

    for(unsigned int i=0;i<a.size();i++){
        ans.push_back(a[i]);
    }
    for(unsigned int j=0;j<b.size();j++){
        ans.push_back(b[j]);
    }

    return ans;
}
char*  Client::CombineChar(char* a, char* b,int aSize,int bSize) {

    char* ans=new char[aSize+bSize]();
    for(int i=0;i<aSize;i++) {
        ans[i] = a[i];
    }
    for(int j=0;j<bSize;j++) {
        ans[aSize+j] = b[j];

    }
    return ans;

}

ServicePacket* Client::getPacket(string line) {
    if (line.compare("DIRQ") == 0) {
        askedForDirc=true;
        return new DirectoryReq((short) 6);
    }
    else if (line.compare(0, 5, "Error") == 0) {
        return new ErrorPacket((short) 5,(short)line.at(6),"Error");//not sure if line.at(8)..
    }
    else if (line.compare("DISC") == 0) {
        askDisc=true;
        return new Disconnect((short) 10);

    }
    else if (line.compare(0,4,"ACK<") == 0){
        return new AckPacket((short) 4,(short)(line.at(4)-'0'));
    }
    else if((line.compare(0,6,"LOGRQ<")==0)&& (line.compare(line.length()-1,1,">")==0)){
        return new LoginPacket((short)7,line.substr(6,line.size() - 7));//substr(8)?
    }
    else if(line.compare(0,3,"WRQ")==0){
        askingToWrite=true;
        if(line.length()==3||line.length()==4||line.at(3)!=' '){
            return new BullShit((short)11);
        }
        string fileName=line.substr(4);
        ifstream is(fileName, ifstream::binary);
        is.seekg (0, is.end);
        wrqLength =is.tellg();
        if(wrqLength==-1){
            return new() BullShit((short)11);
        }
        is.seekg (0, is.beg);
        wrqbuffer = new char [wrqLength];
        is.read (wrqbuffer,wrqLength);

        return new WriteReq((short)2,fileName);
    }
    else if(line.compare(0,3,"RRQ")==0) {
        askedForFile=true;
        if(line.length()==3){
            fileNameAsked="";
        }
        else{fileNameAsked=line.substr(4); }
        return new ReadReq((short)1,fileNameAsked);
    }
    else if(line.compare(0,5,"DELRQ")==0){
        return new DeleteReq((short)8,line.substr(6));
    }else{
        ServicePacket *answer=new BullShit((short) 11);

        return answer ;
    }

    //bcast,data

}
void Client::dealWithAnswer(ServicePacket *answer,ConnectionHandler *connectionHandler) {
  //  cout << "im dealing with " << answer->toString() << endl;

    short opcode = answer->getOpCode();

    switch (opcode) {

        case 3: {//data
            if (askedForFile) {//i don't do anything with file i got from the server.------fixed!

    ofstream myfile;
    std:: ofstream output(fileNameAsked, std::ios::binary);
char* temp=new char(' ');
                if(((DataPacket *) answer)->getPacketSize()!=1&&(((DataPacket *) answer)->getCurrentDataBlockSize())!=temp) {
    ans = CombineChar(ans, ((DataPacket *) answer)->getCurrentDataBlockSize(), size,
                      ((DataPacket *) answer)->getPacketSize());
    size += ((DataPacket *) answer)->getPacketSize();
}
                    if ((size < 512 && ((DataPacket *) answer)->getPacketSize() <= 512) || (size > 512 && ((DataPacket *) answer)->getPacketSize() > 0 && ((DataPacket *) answer)->getPacketSize() < 512)||((DataPacket *) answer)->getPacketSize()<512) {
                        cout << "RRQ " << fileNameAsked << " complete" <<std::endl;

                        output.write(ans,size);
                        output.close();
                        askedForFile = false;
                        ans=new char[0]();
                        dataFile.clear();
                        size=0;

                    }


            }
            if (askedForDirc) {

                char *incomeData = ((DataPacket *) answer)->getCurrentDataBlockSize();
                vector<char> income(0);
                if (dataFile.size() != 0) {
                    for (int i = 0; i < ((DataPacket *) answer)->getPacketSize(); i++) {
                        income.push_back(incomeData[i]);
                    }
                    dataFile = CombineCharArrays(dataFile, income);

                } else {
                    for (int i = 0; i < ((DataPacket *) answer)->getPacketSize(); i++) {
                        dataFile.push_back(incomeData[i]);
                    }

                }
                string s;
                if ((dataFile.size() < 512 && income.size() <= 512) ||
                    (dataFile.size() > 512 && income.size() > 0 && income.size() < 512)) {

                    char curr;
                    s = "";
                    for (unsigned int j = 0; j < dataFile.size(); j++) {
                        curr = dataFile[j];
                        if (curr == '/') {

                        } else if (!(curr == '0')) {
                            s += curr;
                        } else if (curr == '0') {

                            cout << s << endl;
                            s = "";
                        }
                    }

                    askedForDirc = false;
                    dataFile.clear();
                }
            }

            AckPacket *ackp = new AckPacket((short) 4, ((DataPacket *) answer)->getNumOfBlock());
            connectionHandler->sendPacket(ackp);
            break;

        }
        case 4: {//ack
            short blockno = ((AckPacket *) answer)->getBlockNumber();
            cout << "ACK " << blockno << std::endl;
            if (askDisc) notInterupted = false;

            if (askingToWrite) {

                if (wrqLength < 512) {
                    askingToWrite = false;
                    DataPacket *dataP = new DataPacket((short) 3, (short) wrqLength, blockno + 1, wrqbuffer);
                    connectionHandler->sendPacket(dataP);
                    wrqLength=0;
                    wrqbuffer=new char[0]();
                } else {
                    char* chunk=GetPartOfCharArray(wrqbuffer,0,513);
                    char* temp=GetPartOfCharArray(wrqbuffer,513,wrqLength);
                    wrqbuffer = temp;
                    wrqLength=wrqLength-512;
                    DataPacket *dataP = new DataPacket((short) 3, (short) 512, blockno + 1, chunk);
                    connectionHandler->sendPacket(dataP);
                }
                }

            break;
        }
        case 5: {//error
            short code = ((ErrorPacket *) answer)->getErrorCode();
            cout << "Error " << code << std::endl;
            if (code == 1) {
                askedForFile = false;
            }
            if (code == 5) {
                askingToWrite = false;
            }
            if (code == 3) {
                askingToWrite = false;//maybe del ans read also
            }

            break;
        }
        case 9: {//bcast
            char deloradd = ((BcastPacket *) answer)->getDelOrAdd();
            string name = ((BcastPacket *) answer)->getFileName();
            short DelRrAdd = (short) (deloradd & 0xff);
            if (DelRrAdd == 1){
                cout << "Bcast " << "add " << name << std::endl;

        }
            else if(DelRrAdd==0) {
                cout << "Bcast " << "del " << name << std::endl;

            }
            break;
              }
    }



}
bool Client::getNotInterupted(){
    return notInterupted;
};

void Client::readFromClientAndSend(ConnectionHandler *connectionHandler,Client *thisClient){
    const short bufsize = 1024;
    char buf[bufsize];
    std::cin.getline(buf, bufsize);
    std::string line(buf);
    int len=line.length();
    ServicePacket* PacketToBeSent=thisClient->getPacket(line);
    connectionHandler->sendPacket(PacketToBeSent);
    return;
}


char* Client::GetPartOfCharArray(char* ch,int from,int to){//include from does not include to.
    int size=to-from;
    char* ans=new char[size];
    for(unsigned int i=0;i<size;i++){
        ans[i]=ch[from+i];
    }
    return ans;

}
int main (int argc, char *argv[]) {
            if (argc < 3) {
                std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
                return -1;
            }
    std::string host =argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler *connectionHandler=new ConnectionHandler(host, port);
    Client *thisClient=new Client();

    if (!connectionHandler->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    KeyBoardTask keyBoardThread(connectionHandler,thisClient);
    ServerTask ServerThread(connectionHandler,thisClient);
    while (thisClient->getNotInterupted()) {
        boost::thread t1(boost::ref(keyBoardThread));
        boost::thread t2(boost::ref(ServerThread));
        t1.join();
        t2.join();
    }
    return 0;
}

