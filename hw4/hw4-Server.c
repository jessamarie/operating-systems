/*
Author: Jessica Marie Barre
Email: barrej4@rpi.edu
Program name: hw3.c
Date Due: Dec 1, 2016
Purpose: Creates a storage server

This code is modeled after the professor's
tcp-server.c example
 */

/*
The application-level protocol must be implemented exactly as shown below:
	STORE <filename> <bytes>\n<file-contents>
		-- add file <filename> to the storage server
		-- if <filename> is invalid or <bytes> is zero or invalid,
			return "ERROR INVALID REQUEST\n"
		-- if the file already exists, return "ERROR FILE EXISTS\n"
		-- return "ACK\n" if successful

	READ <filename> <byte-offset> <length>\n
		-- server returns <length> bytes of the contents of file <filename>,
			starting at <byte-offset>
		-- if <filename> is invalid or <length> is zero or invalid,
			return "ERROR INVALID REQUEST\n"
		-- if the file does not exist, return "ERROR NO SUCH FILE\n"
		-- if the file byte range is invalid, return "ERROR INVALID BYTE RANGE\n"
		-- return "ACK" if successful, following it with the length and data, as follows:
			ACK <bytes>\n<file-excerpt>

	LIST\n
		-- server returns a list of files currently stored on the server
		-- the list must be sent in alphabetical order
		-- the format of the message containing the list of files is as follows:
			<number-of-files> <filename1> <filename2> ... <filenameN>\n
		-- therefore, if no files are stored, "0\n" is returned
 */

/* print statements */

//printf("[child %d] Received STORE abc.txt 25842\n", port);
//printf("[child %d] Stored file \"abc.txt\" (25842 bytes)\n", port);
//printf("Sent ACK");
//printf("Received READ xyz.jpg 5555 2000", port);
//printf("[child %d] Sent ERROR NO SUCH FILE\n", port);
//printf("[child %d] Client disconnected\n", port);
//printf("[child %d] Received STORE abc.txt 25842\n", port);

/*
	 bash$ ./a.out 9876
Started server; listening on port: 9876
Received incoming connection from: <client-hostname-or-IP>
[child 13455] Received STORE abc.txt 25842
[child 13455] Stored file "abc.txt" (25842 bytes)
[child 13455] Sent ACK
[child 13455] Received READ xyz.jpg 5555 2000
[child 13455] Sent ERROR NO SUCH FILE
[child 13455] Client disconnected
...
Received incoming connection from: <client-hostname-or-IP>
[child 11938] Received STORE def.txt 79112
[child 11938] Stored file "def.txt" (79112 bytes)
[child 11938] Sent ACK
[child 11938] Client disconnected
...
Received incoming connection from: <client-hostname-or-IP>
[child 19232] Received READ abc.txt 4090 5000
[child 19232] Sent ACK 5000
[child 19232] Sent 5000 bytes of "abc.txt" from offset 4090
[child 19232] Received LIST
[child 19232] Sent 2 abc.txt def.txt
[child 19232] Client disconnected
 * **/

#include <sys/types.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <stdio.h>
#include <strings.h>
#include <unistd.h>
#include<pthread.h>


#define BUFFER_SIZE 1024

struct instruction {
	int code;
	char* filename;
	char* data;
	int* bytes;
	int length;


} instruction;

int do_instruction(int newsock, void * s) {



}

struct instruction parse_intruction(int newsock, char buffer[]) {

	struct instruction instr;
	int current = 0, next = 0;  /** The current index of the expression **/


	/** assign opening parens and operator to first two elements **/
	elements[*count] = malloc ( sizeof ( char ) * 1);

	for(current = 0; current < len(buffer); current++) {

		while(expression[next] != ' '){
			next++;
		}


		current = next;

	}



	if(elements[*count] == NULL) {
		perror( "malloc() failed" );
		exit(EXIT_FAILURE);
	}

	strcpy(elements[*count],"(");

	*count = *count + 1;



	elements[*count] = malloc ( sizeof ( char ) * (next - current));
	strncpy(elements[*count], expression + current, next - current);

	*count = *count + 1;
	current = next;


	/** Note that count should = 2 and the elements array should so farcontain
	       {'(', 'r'}, where r is an unknown operator**/


	/** Now lets parse the rest of the expression **/

	while(current < len) {

		/** Reallocate the elements array to hold one more element if necessary **/

		if (*count > size - 2) {
			size = size + 1;
			elements = realloc (elements, (int) size * sizeof( char * ) );

			if(elements == NULL) {
				perror( "realloc() failed" );
				exit(EXIT_FAILURE);
			}
		}

		/** Parse the string by digits and subexpressions, ignore white space **/

		next = current + 1;
		char n = (char) expression[next];
		if (isdigit(expression[current])
				|| (strchr("-", expression[current]) != NULL && isdigit(n) != 0) ) {

			int numDigits = 1;

			while(isdigit(n)){
				next++;
				numDigits++;
				n = (char) expression[next];
			}

			/** allocate space for the number of digits needed **/
			elements[*count] = malloc ( sizeof ( char ) * numDigits);

			if(elements[*count] == NULL) {
				perror( "malloc() failed" );
				exit(EXIT_FAILURE);
			}

			elements[*count] = malloc ( sizeof ( char ) * (next - current));
			strncpy(elements[*count], expression + current, next - current);

			current = next;

		}

		else if (expression[current] == '('){

			next = current +  1;
			int subsize = 1;
			int oParens = 1;
			int cParens = 0;

			/** locate the matching closing parentheses **/
			while( cParens != oParens && next < len ) {

				if (expression[next] == '('){
					oParens++;
				}

				else if(expression[next] == ')'){
					cParens++;
				}

				next++;
				subsize = subsize + 1;

			}


			if(cParens != oParens){
				perror("parentheses don't match");
				exit(EXIT_FAILURE);
			}

			/** copy over subexpression **/
			elements[*count] = malloc ( sizeof ( char ) * subsize);
			if(elements[*count] == NULL) {
				perror( "malloc() failed" );
				exit(EXIT_FAILURE);
			}

			elements[*count] = malloc ( sizeof ( char ) * (next - current));
			strncpy(elements[*count], expression + current, next - current);

			current = next;

		}else if (current == len - 1){

			if (expression[current] == ')') {

				elements[*count] = malloc ( sizeof ( char ) * 1);

				if(elements[*count] == NULL) {
					perror( "malloc() failed" );
					exit(EXIT_FAILURE);
				}

				strcpy(elements[*count],")");


			} else {
				perror("This expression has mismatched parentheses");
				exit(EXIT_FAILURE);
			}
			current++;

		}else {
			current++;
			continue;

		}

		*count = *count + 1;

	} /** ends while  **/

	return elements;




}

int create_socket(struct sockaddr_in* server, unsigned short port) {

	/* Create the listener socket as TCP socket */
	int sd = socket(PF_INET, SOCK_STREAM, 0);

	if (sd < 0) {

		perror("ERROR: Could not create socket.");
		exit(EXIT_FAILURE);

	}

	printf("SUCCESS: Socket Created (remove later).");

	server->sin_port = htons(port);
	int len = sizeof(*server);

	if (bind(sd, (struct sockaddr*) &*server, len) < 0) {

		perror("Error: bind() failed.");
		exit(EXIT_FAILURE);
	}

	listen(sd, 5); /* 5 is the max number of waiting clients */

	printf("Started server; listening on port: %d\n", port);

	return sd;
}

void create_dir() {
	/* TODO: Create the directory for storage */
	if (stat("./.storage", &st) == -1) {
		mkdir("./.storage", 0700);
	} else {
		system("exec rm -r ./.storage/*");
	}
}

void connection(int newsock, const struct sockaddr_in* client) {

	int n;
	char buffer[BUFFER_SIZE];

	do {

		printf("CHILD %d: Blocked on recv()\n", getpid());

		/* can also use read() and write()..... */

		n = recv(newsock, buffer, BUFFER_SIZE, 0);

		if (n < 0) {

			perror("recv() failed");

		} else if (n == 0) {

			printf("CHILD %d: Rcvd 0 from recv(); closing socket\n", getpid());

		} else {

			printf("CHILD %d: Rcvd message from %s: %s\n", getpid(),
					inet_ntoa((struct in_addr) client->sin_addr), buffer);

			buffer[n] = '\0';

			char** instruction = parse_intruction(newsock, buffer);
			do_instruction(newsock, instruction);
			/* send ack message back to the client */
			n = send(newsock, "ACK\n", 4, 0);
			fflush(NULL);

			if (n != 4) {
				perror("send() failed");
			}
		}

	} while (n > 0);
	/* this do..while loop exits when the recv() call
	 returns 0, indicating the remote/client side has
	 closed its socket */
	printf("CHILD %d: Bye!\n", getpid());
	close(newsock);
	exit(EXIT_SUCCESS); /* child terminates here! */
}

int main( int argc, char **argv){

	/** Check for correct number of arguments and abort
      if incorrect **/

	if ( argc != 2 ) {
		fprintf(stderr, "Error: Invalid arguments\nUsage: ./a.out <port number>>");
		return EXIT_FAILURE;
	}

	/* socket structures */

	struct sockaddr_in server;

	server.sin_family = PF_INET;
	server.sin_addr.s_addr = INADDR_ANY;

	unsigned short port =  atoi(argv[1]); //port = 8127;



	/* Create the listener socket as TCP socket */

	int sd = create_socket(&server, port);

	/* TODO: Create the directory for storage */
	create_dir();



	struct sockaddr_in client;
	int fromlen = sizeof( client );

	int pid;

	while ( 1 )
	{

		/* PARENT: Blocked on accept() */
		int newsock = accept( sd, (struct sockaddr *)&client,
				(socklen_t*)&fromlen );

		printf("Received incoming connection from: %s\n", inet_ntoa( (struct in_addr)client.sin_addr));


		/* handle new socket in a child process,
	       allowing the parent process to immediately go
	       back to the accept() call */
		pid = fork();

		if ( pid < 0 )
		{
			perror( "ERROR:fork() failed" );
			exit( EXIT_FAILURE );
		}
		else if ( pid == 0 )
		{
			connection(newsock, &client);
		}
		else /* pid > 0   PARENT */
		{
			/* parent simply closes the new client socket (endpoint) */
			close( newsock );
		}
	}

	close( sd );

	return EXIT_SUCCESS;

}
