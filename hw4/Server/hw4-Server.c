/*
Author: Jessica Marie Barre
Email: barrej4@rpi.edu
Program name: hw3.c
Date Due: Dec 1, 2016
Purpose: Creates a storage server

This code is modeled after the professor's
tcp-server.c example
 */

#include <sys/stat.h>
#include <sys/types.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <assert.h>
#include <ctype.h>
#include <dirent.h>
#include <sys/wait.h>
#include <stdbool.h>

/* Testing TCP: netcat <IP_Address> <port_number>
 * netcat -u <IP_Address> <port_number>
 *
 * */






#define BUFFER_SIZE 1024

int create_socket(struct sockaddr_in* server, unsigned short port) {

	/* Create the listener socket as TCP socket */
	int sd = socket(PF_INET, SOCK_STREAM, 0);

	if (sd < 0) {

		perror("ERROR: Could not create socket.");
		exit(EXIT_FAILURE);

	}

	server->sin_port = htons(port);
	int len = sizeof(*server);

	if (bind(sd, (struct sockaddr*) &*server, len) < 0) {

		perror("Error: bind() failed.");
		exit(EXIT_FAILURE);
	}

	listen(sd, 5); /* 5 is the max number of waiting clients */

	printf("Started server; listening on port: %d\n", port);
	fflush(NULL);

	return sd;
}

void create_dir() {

	if (mkdir(".storage", 0777) ==0){
		//printf("Created Directory\n");
	}
	else if (mkdir(".storage", 0777) == -1){
		//printf("Recreated New Directory\n");
		char *cmd = "rm -rf .storage";
		system(cmd);
		mkdir(".storage", 0777);
	}
	else{
		perror("ERROR: can't create directory\n");
	}
}


/** Splits a string only once by the desired delimeter **/
char **split_from_content(const char* str, const char* delim, size_t* numtokens) {

	char *s = strdup(str);
	size_t tokens_alloc = 1;
	size_t tokens_used = 0;

	char **tokens = calloc(tokens_alloc, sizeof(char*));

	char *token, *rest = s;

	if ((token = strsep(&rest, delim)) != NULL) {

		if (tokens_used == tokens_alloc) {
			tokens_alloc *= 2;
			tokens = realloc(tokens, tokens_alloc * sizeof(char*));
		}

		/* Skip last delimeter */

		if(strcmp(token, "")) {
			tokens[tokens_used++] = strdup(token);
			tokens[tokens_used++] = strdup(rest);
		}
	}

	if (tokens_used == 0) {
		free(tokens);
		tokens = NULL;
	} else {
		tokens = realloc(tokens, tokens_used * sizeof(char*));
	}

	*numtokens = tokens_used;

	free(s);

	return tokens;
}

char **strsplit(const char* str, const char* delim, size_t* numtokens) {

	char *s = strdup(str);
	size_t tokens_alloc = 1;
	size_t tokens_used = 0;

	char **tokens = calloc(tokens_alloc, sizeof(char*));

	char *token, *rest = s;

	while ((token = strsep(&rest, delim)) != NULL) {


		if (tokens_used == tokens_alloc) {
			tokens_alloc *= 2;
			tokens = realloc(tokens, tokens_alloc * sizeof(char*));
		}


		/* Skip last delimeter */

		if(strcmp(token, "")) {
			tokens[tokens_used++] = strdup(token);
		}
	}


	if (tokens_used == 0) {
		free(tokens);
		tokens = NULL;
	} else {
		tokens = realloc(tokens, tokens_used * sizeof(char*));
	}

	*numtokens = tokens_used;

	free(s);

	return tokens;
}


bool is_valid_file(char const *name)
{
	return !(strcmp(name, ".") == 0) && !(strcmp(name, "..") == 0);
}


char * list_data(int newsock) {

	// To do do not call when server just starts
	// assume that assume there are no files at all.


	// Rather than using opendir/closedir, use array of structs

	char * response;

	//DIR* dir;
	struct dirent **d;

	//dir = opendir(".storage/");

	int n = scandir(".storage/", &d,0,alphasort);

	if(n < 0) {
		printf("scandir() failed.");
		exit(EXIT_FAILURE);
	} else {

		// get size
		int size = 0;
		int i = 0;
		char * string = malloc( 50 * sizeof(char));
		strcpy(string, "");
		while (i < n - 1) {
			if (is_valid_file(d[i]->d_name)) {
				strcat(string,  d[i]->d_name);
				strcat(string, " ");
				size++;
			}
			i++;
		}

		if (is_valid_file(d[i]->d_name)) {
			strcat(string,  d[i]->d_name);
			size++;
		}

		char snum[5];

		response = malloc( 100 * sizeof(char));

		sprintf(snum,"%d",size);
		strcpy(response, snum );
		strcat(response, " ");
		strcat(response, string);
		strcat(response, "\n");

	}

	return response;
}

char* store_data(int newsock, char* filename, char* filepath, int bytes , char* data) {

	char* response;


	if( access( filepath, F_OK ) == 0)
	{
		response = malloc( 20 * sizeof(char));
		strcpy(response, "ERROR FILE EXISTS\n");

	} else {

		/* store any data after the '\n' character currently
		 * in the buffer */

		if(bytes < 1) {

			response = malloc( 20 * sizeof(char));
			strcpy(response, "ERROR INVALID REQUEST\n");
			return response;

		}

		FILE * file = fopen( filepath , "wb");

		if( file == NULL)
		{
			response = malloc( 20 * sizeof(char));
			strcpy(response, "ERROR INVALID REQUEST\n");
			return response;
		}

		fwrite(data, sizeof(char), strlen(data), file);

		/* while number of bytes rcvd/stored is less
		 * than expected bytes..*/

		int bytes_written = strlen(data);

		while (bytes_written < bytes){

			/* read more bytes */

			char buff[BUFFER_SIZE];

			int n = recv(newsock, buff,BUFFER_SIZE,0);
			fflush(NULL);

			buff[n] = '\0';

			if (n < 0) {
				response = malloc( 30 * sizeof(char) );
				strcpy( response, "ERROR INVALID REQUEST\n" );
				return response;

			} else {

				/* write more bytes */
				fwrite(buff, sizeof(char),n,file);

				bytes_written = bytes_written + n;

			//	printf("BYTES WRITTEN: %d bytes %d\n", bytes_written,bytes);
			} /* done */

		}

		if(bytes_written != bytes) {

			response = malloc( 20 * sizeof(char));
			strcpy(response, "ERROR INVALID REQUEST\n");
			return response;

		}


		fclose(file);

		printf( "[child %d] Stored file \"%s\" (%d bytes)\n", getpid(), filename, bytes);
		fflush(NULL);

		response = malloc( 5 * sizeof(char));

		strcpy(response, "ACK\n");

	}

	return response;

}

char * read_data(int newsock, char * filename, char* filepath, int offset, int length){

	char* response;
	char* content = malloc((length + 1) * sizeof(char));
	strcpy(content, "");

	if( access( filepath, F_OK | R_OK) != 0)
	{
		response = malloc( 30 * sizeof(char));
		strcpy(response, "ERROR NO SUCH FILE\n");
		printf("[child %d] Sent %s", getpid(), response);
		fflush(NULL);

	} else {

		if(length < 1) {

			response = malloc( 20 * sizeof(char));
			strcpy(response, "ERROR INVALID REQUEST\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);
			return response;

		}

		FILE * file = fopen( filepath , "r");

		if( file == NULL)
		{
			response = malloc( 20 * sizeof(char));
			strcpy(response, "ERROR INVALID REQUEST\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);
			return response;
		}

		fseek(file,0,SEEK_END);

		long int file_len = ftell(file);
		rewind(file);


		if (file_len < length + offset || file_len-1 < offset){
			fclose(file);
			response = malloc( 30 * sizeof(char) );
			strcpy( response, "ERROR INVALID BYTE RANGE\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);
			return response;

		} else {


			char buff[length + 1];

			fseek(file, (long)offset , SEEK_SET);

			int bytes_read = fread(buff, sizeof(char),length,file);

			if (bytes_read != length){

				fclose(file);
				response = malloc( 30 * sizeof(char) );
				strcpy( response, "ERROR INVALID BYTE RANGE\n" );
				return response;
			}

			buff[length] = '\0';

			strcpy(content, buff);

			response = malloc( (length + 40) * sizeof(char) );

			strcpy( response, "ACK " );

			char snum[10];

			sprintf(snum,"%d",length);

			strcat( response, (char *) snum );
			strcat( response, "\n" );

			send(newsock, response, strlen(response), 0);
			fflush(NULL);

			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

			strcpy( response, content);
			//strcat( response, "\n" );

			fclose(file);

			printf("[child %d] Sent %d bytes of \"%s\" from offset %d\n", getpid(), length, filename, offset);
			fflush(stdout);

		}

	}

	return response;
}

int isDigit (const char * s)
{
	if (s == NULL || *s == '\0' || isspace(*s))
		return 0;
	char * p;
	strtod (s, &p);
	return *p == '\0';
}

void do_command(size_t numlines, size_t numparts, int newsock, char** parts,
		char** lines) {


	char * response;

	if (strcmp(parts[0], "STORE") == 0) {

		if (numparts != 3 || !isDigit(parts[2])) {

			response = malloc( 30 * sizeof(char) );
			strcpy( response, "ERROR INVALID PARAMETERS\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		}  else {
			char filepath[50];
			strcpy(filepath, ".storage/");
			strcat(filepath, parts[1]);

			response = store_data(newsock, parts[1], filepath, atoi(parts[2]), lines[1]);

			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		}

	} else if (strcmp(parts[0], "READ") == 0) {

		if (numparts != 4) {

			response = malloc( 30 * sizeof(char) );
			strcpy( response, "ERROR INVALID PARAMETERS\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		} else if (!isDigit(parts[2]) || !isDigit(parts[3])) {

			response = malloc( 30 * sizeof(char) );
			strcpy( response, "ERROR INVALID PARAMETERS\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		} else {

			char filepath[50];
			strcpy(filepath, ".storage/");
			strcat(filepath, parts[1]);

			response = read_data(newsock, parts[1], filepath, atoi(parts[2]), atoi(parts[3]));
		}

	} else if (strcmp(parts[0], "LIST") == 0) {

		if (numparts != 1) {

			response = malloc( 30 * sizeof(char) );
			strcpy( response, "ERROR INVALID PARAMETERS\n");
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		} else {

			response = list_data(newsock);
			printf("[child %d] Sent %s", getpid(), response);
			fflush(NULL);

		}

	} else {

		response = malloc( 30 * sizeof(char) );
		strcpy( response, "ERROR INVALID PARAMETERS\n");
		printf("[child %d] Sent %s", getpid(), response);
		fflush(NULL);

	}

	/* Send Acknowledge/Error response  to client*/

	int n = send(newsock, response, strlen(response), 0);
	fflush(NULL);

	if (n != strlen(response)) {
		perror("Error send() failed\n.");
	}

	free(response);
}

void connection(int newsock, const struct sockaddr_in* client) {

	int n;
	char buffer[BUFFER_SIZE];

	/* Receive instruction */

	do {

		n = recv(newsock, buffer, BUFFER_SIZE, 0);
		fflush(stdout);

	//	printf("Content to write is: %s\n", buffer);


		if (n < 0) {

			perror("recv() failed");
			fflush(NULL);

		} else if (n == 0) {

			fflush(NULL);

			//printf("CHILD %d: Rcvd 0 from recv(); closing socket\n", getpid());

		} else {

			buffer[n] = '\0';

			/* Split at newline */

			char **lines;
			size_t numlines;

			lines = split_from_content(buffer, "\n", &numlines);

			printf("[child %d] Received %s\n", getpid(), lines[0]);
			fflush(NULL);

			/* Split parts of instruction */

			char **parts;
			size_t numparts;

			parts = strsplit(lines[0], " ", &numparts);
#if 0
			size_t j;
			for (j = 0; j < numlines; j++) {
				printf("%s\n", lines[j]);
			}

			for (j = 0; j < numparts; j++) {
				printf("%s\n", parts[j]);
			}

#endif

			/* Carry out instruction **/
			do_command(numlines, numparts, newsock, parts,
					lines);

			/* free space */

			size_t i;
			for (i = 0; i < numlines; i++) {
				free(lines[i]);
			}
			if (lines != NULL)
				free(lines);

			for (i = 0; i < numparts; i++) {
				free(parts[i]);
			}
			if (parts != NULL)
				free(parts);

		}

	} while (n > 0);

	close(newsock);

	printf("[child %d] Client disconnected\n", getpid());

	exit(EXIT_SUCCESS); /* child terminates here! */
}

int main( int argc, char **argv){

	/** Check for correct number of arguments and exit
      if incorrect **/

	if ( argc != 2 ) {
		fprintf(stderr, "Error: Invalid arguments\nUsage: ./a.out <port number>>");
		return EXIT_FAILURE;
	}

	/* socket structures */

	struct sockaddr_in server;

	server.sin_family = PF_INET;
	server.sin_addr.s_addr = INADDR_ANY;

	unsigned short port =  atoi(argv[1]); /* use port = 8127 for testing */

	/* Create the listener socket as TCP socket */

	int sd = create_socket(&server, port);

	create_dir(); 	/* Create the directory for storage */

	struct sockaddr_in client;
	int fromlen = sizeof( client );

	int pid;

	while ( 1 )
	{
		int newsock = accept( sd, (struct sockaddr *)&client,
				(socklen_t*)&fromlen );

		printf("Received incoming connection from: %s\n", inet_ntoa( (struct in_addr)client.sin_addr));
		fflush(NULL);

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
			connection(newsock, &client); /* Child code */
		}
		else /* pid > 0   PARENT */
		{			/* parent simply closes the new client socket (endpoint) */
			wait(NULL);
			close( newsock );
		}
	}

	close( sd );

	return EXIT_SUCCESS;

}
