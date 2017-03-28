# All Targets

LDFLAGS:=-lboost_system -lboost_locale -lboost_thread
all: Client

# Tool invocations
# Executable "TFTPclient" depends on the files TFTPclient.o and run.o.
Client:      bin/connectionHandler.o bin/KeyBoardTask.o  bin/MessageEncoderDecoder.o bin/MessageEncoderDecoderImpl1.o bin/ServerTask.o bin/ServicePacket.o bin/Client.o
	@echo 'Building target: TFTPclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/TFTPclient   bin/connectionHandler.o bin/KeyBoardTask.o  bin/MessageEncoderDecoder.o bin/MessageEncoderDecoderImpl1.o bin/ServerTask.o bin/ServicePacket.o bin/Client.o $(LDFLAGS)
	@echo 'Finished building target: TFTPclient'
	@echo ' '


# Depends on the source and header files 
# Depends on the source and header files 

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/connectionHandler.o src/connectionHandler.cpp
	# Depends on the source and header files 
bin/KeyBoardTask.o: src/KeyBoardTask.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/KeyBoardTask.o src/KeyBoardTask.cpp


# Depends on the source and header files
bin/MessageEncoderDecoder.o: src/MessageEncoderDecoder.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/MessageEncoderDecoder.o src/MessageEncoderDecoder.cpp
	
# Depends on the source and header files
bin/MessageEncoderDecoderImpl1.o: src/MessageEncoderDecoderImpl1.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/MessageEncoderDecoderImpl1.o src/MessageEncoderDecoderImpl1.cpp
	
# Depends on the source and header files
bin/ServerTask.o: src/ServerTask.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ServerTask.o src/ServerTask.cpp



# Depends on the source and header files
bin/ServicePacket.o: src/ServicePacket.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ServicePacket.o src/ServicePacket.cpp

bin/Client.o: src/Client.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Client.o src/Client.cpp
			
#Clean the build directory
clean: 
	rm -f bin/* 

	
	
	
	
	
	