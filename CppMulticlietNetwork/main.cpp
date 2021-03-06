#include <stdio.h>
#include <string.h>         // strlen
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>         // close
#include <arpa/inet.h>      // close
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/time.h>       // FD_SET, FD_ISSET, FD_ZERO
#include <bits/stdc++.h> 
#define TRUE 1
#define FALSE 0
#define PORT 8888

int main(int argc, char* argv[]){
  int opt = TRUE;
  int master_socket, addrlen, new_socket, client_socket[30],
      max_clients=30, activity, i, valread, 
      sd;     // sd = socket descriptor

  int max_sd;
  struct sockaddr_in address;

  char buffer[1025];     // Data buffer

  // Define a set of socket descriptors
  fd_set readfds;

  std::string msg = "ECHO Daemon v1.0 \r\n";
  char msg_char_arr[256];
  std::strcpy(msg_char_arr, msg.c_str());
  
  for(i = 0; i < max_clients; ++i)
    client_socket[i] = 0;

  if((master_socket = socket(AF_INET, SOCK_STREAM,0)) == 0)
  {
    perror("socket failed");
    exit(EXIT_FAILURE);
  }

    //set master socket to allow multiple connections ,  
    //this is just a good habit, it will work without this  
  if( setsockopt(master_socket, SOL_SOCKET, SO_REUSEADDR, (char*)8, sizeof(opt)) < 0 ){
      perror("setsockopt");
      exit(EXIT_FAILURE);
  }

  // type of socket created
  address.sin_family = AF_INET;
  address.sin_addr.s_addr = INADDR_ANY;
  address.sin_port = htons(PORT);

  // Bind socket to local host
  if( bind(master_socket, (struct sockaddr*)&address, sizeof(address)) < 0 ){
    perror("can not bind to the port");
    exit(EXIT_FAILURE);
  }

  printf("Listener is on port %d \n", PORT);

  //try to specify maximum of 3 pending connections for the master s
  if(listen(master_socket, 3) < 0){
    perror("listen");
    exit(EXIT_FAILURE);
  }

  // accept the incoming connection  
  addrlen = sizeof(address);
  puts("Waiting for connections ...");

  while(TRUE){
    // Clear the socket set
    FD_ZERO(&readfds);

    // Add master socket to the set
    FD_SET(master_socket, &readfds);
    max_sd = master_socket;
    // Add child sockets to the set
    for(i = 0; i  < max_clients; ++i){
      sd = client_socket[i];

        //if valid socket descriptor then add to read list  
      if( sd > 0 )
        FD_SET(sd, &readfds);

      // Highest valid socket descriptor number to add later 
      if( sd > max_sd )
        max_sd = sd;
  
    }

    // wait for an activity on one of the sockets, timeout is NULL so wait indefinitely.
    activity = select(max_sd + 1, &readfds, NULL, NULL, NULL);
    if(((activity < 0) && (errno != EINTR))){
      printf("select error");
    }

    if(FD_ISSET(master_socket, &readfds)){
      if((new_socket = accept(master_socket,
                             (struct sockaddr*)&address,
                             (socklen_t*)&addrlen)) < 0 )
        {
          perror("accept");
          exit(EXIT_FAILURE);
        }
      
      printf("New Connection, socket fd is %d , \
                              ip is %s,         \
                              port = %d\n", 
                              new_socket, inet_ntoa(address.sin_addr), ntohs(address.sin_port));

      int temp = strlen(msg_char_arr);
      if(send(new_socket, msg_char_arr, temp, 0) != temp){
        perror("send");
      }

      puts("Welcome msg sent successfully\n");

      //add new socket to array of sockets  
      for(i = 0; i < max_clients; ++i){
        // if the position is empty
        if(client_socket[i] == 0){
          
          client_socket[i] = new_socket;
          printf("Adding to list of sockets as %d\n", i);

          break;
        }
      }      
    }

    for(i = 0; i < max_clients; ++i){
      sd = client_socket[i];
      if(FD_ISSET(sd, &readfds)){
        // Check if it was for closing, and also read the incoming msg
        if((valread=read(sd, buffer, 1024)) == 0){
          //Somebody disconnected , get his details and print  
          getpeername(sd, (struct sockaddr*)&address, (socklen_t*)&addrlen);
          printf("Host disconnected, ip %s, port %d \n", 
                    inet_ntoa(address.sin_addr),
                    ntohs(address.sin_port));
          // close the socket and mark as 0 in the list for reuse
          close(sd);
          client_socket[i] = 0;
        }
        // Echo back the msg that came in.
        else{
            buffer[valread] = '\0';
            send(sd, buffer, strlen(buffer), 0);
        }
      }
    }

  }

  return 0;
}
