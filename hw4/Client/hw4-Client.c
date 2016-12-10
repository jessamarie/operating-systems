/* tcp-client.c */

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

#define BUFFER_SIZE 20000



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

char* read_file(char* directory, char* filename) {

	char filepath[50];
	strcpy(filepath, directory);
	strcat(filepath, "/");
	strcat(filepath, filename);

	char buffer[BUFFER_SIZE*10];

	if( access( filepath, F_OK | R_OK) != 0)
	{

		return "ERROR";

	} else {

		FILE * file = fopen( filepath , "r");

		if( file == NULL)
		{
			fprintf( stderr, "Error file open() failed\n");
			exit(EXIT_FAILURE);
		}

		fread(buffer, sizeof(buffer),BUFFER_SIZE*10,file);
		//printf("buffer contains: %s\n", buffer);
	}


	char * string = malloc( sizeof ( char ) * (strlen(buffer) + 1));

	strcpy(string,buffer);

	return string;

}


int main(){

	/* create TCP client socket (endpoint) */
	int sock = socket( PF_INET, SOCK_STREAM, 0 );

	if ( sock < 0 )
	{
		perror( "socket() failed" );
		exit( EXIT_FAILURE );
	}

	struct hostent * hp = gethostbyname( "127.0.0.1" );

	if ( hp == NULL )
	{
		perror( "gethostbyname() failed" );
		exit( EXIT_FAILURE );
	}


	/* Open socket */


	struct sockaddr_in server;
	server.sin_family = PF_INET;

	memcpy( (void *)&server.sin_addr, (void *)hp->h_addr,
			hp->h_length );
	unsigned short port = 8127;
	server.sin_port = htons( port );

	printf( "server address is %s\n", inet_ntoa( server.sin_addr ) );

	if ( connect( sock, (struct sockaddr *)&server,
			sizeof( server ) ) < 0 )
	{
		perror( "connect() failed" );
		exit( EXIT_FAILURE );
	} else {
		printf( "Connection was successful.\n" );
	}


	/* Send message to server */

	char buff[BUFFER_SIZE];
	int i;
	for (i=0;i<BUFFER_SIZE;i++){
		buff[i] = ' ';
	}

	while(1) {

		printf( "Sent: " );

		/* STORE <filename> <bytes>\n<file-contents>
		 * READ <filename> <byte-offset> <length>\n
		 * LIST\n
		 * */
		// STORE abc.txt 10000\napples and oranges are cool
		// READ abc.txt 3000 200\n
		// "STORE cool.txt 10000\nI like tomatoes";
		// "READ cool.txt 3000 200\n";
		// "LIST";

		while(fgets(buff, 2000, stdin) != NULL) {



			char buff2[BUFFER_SIZE*11];
			int i;
			for (i=0;i<BUFFER_SIZE*11;i++){
				buff2[i] = ' ';
			}

			fflush(NULL);

			if (buff[0]=='S'&& buff[1]=='T'&& buff[2]=='O'&& buff[3]=='R'&& buff[4]=='E'){
				 fgets(buff+strlen(buff),2000,stdin);
					fflush(NULL);
			}


#if 0
			/* If STORE get the newline data, too */
			if (buff[0]=='S'&& buff[1]=='T'&& buff[2]=='O'&& buff[3]=='R'&& buff[4]=='E'){

				char **lines;
				size_t numlines;
				lines = strsplit(buff, " ", &numlines);

				char* temp = read_file("../Input", lines[1]);
				//fflush(NULL);
				fflush( stdout );

				//printf("contents of file are: %s\n", temp);

				if (strcmp(temp, "ERROR") == 0) {
					perror("can't find directory\n");
					continue;
				} else {
					strcpy(buff2,buff);
					strcat(buff2,"\n");
					strcat(buff2,temp);
				}



			} else {
				strcpy(buff2,buff);
			}

#endif

			int n = write(sock, buff, strlen(buff) );
			fflush( NULL );

			if ( n < strlen(buff) ) {
				perror("write() failed");
				exit(EXIT_FAILURE);
			}



			/* Receive message */

			char buffer[ BUFFER_SIZE ];

			n = read( sock, buffer, BUFFER_SIZE );  // BLOCK

			if ( n < 0 )
			{
				perror( "read() failed" );
				exit( EXIT_FAILURE );
			}
			else
			{
				printf( "Client received: \"%s\"", buffer );
			}

			return EXIT_SUCCESS;

		//	printf( "Sent: " );

		}
	}

	close( sock );



}
