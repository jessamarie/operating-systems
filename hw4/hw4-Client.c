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

#define BUFFER_SIZE 1024

int main( int argc, char **argv){

	/** Check for correct number of arguments and abort
      if incorrect **/

	if ( argc != 5 ) {
		fprintf(stderr, "Error: Invalid arguments\nUsage Incorrect");
		return EXIT_FAILURE;
	}

	/* create TCP client socket (endpoint) */
	int sock = socket( PF_INET, SOCK_STREAM, 0 );

	if ( sock < 0 )
	{
		perror( "socket() failed" );
		exit( EXIT_FAILURE );
	}

	struct hostent * hp = gethostbyname( "linux00.cs.rpi.edu" );
	if ( hp == NULL )
	{
		perror( "gethostbyname() failed" );
		exit( EXIT_FAILURE );
	}


	/* Get server information */


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
	}


	/* Send message to server */
	/* STORE <filename> <bytes>\n<file-contents>
	 * READ <filename> <byte-offset> <length>\n
	 * LIST\n
	 * */

	char* command = argv[1];
	char* file_name = argv[2];
	char* bytes = arg[3];
	char* file_contents = argv[4];

	char * msg = command + " " + file_name + " " + bytes +
			" " + file_contents;
	int n = write( sock, msg, strlen( msg ) );
	fflush( NULL );
	if ( n < strlen( msg ) )
	{
		perror( "write() failed" );
		exit( EXIT_FAILURE );
	}


	/*Get message from server */
	char buffer[ BUFFER_SIZE ];
	n = read( sock, buffer, BUFFER_SIZE );  // BLOCK
	if ( n < 0 )
	{
		perror( "read() failed" );
		exit( EXIT_FAILURE );
	}
	else
	{
		buffer[n] = '\0';
		printf( "Received message from server: %s\n", buffer );
	}

	close( sock );

	return EXIT_SUCCESS;
}
