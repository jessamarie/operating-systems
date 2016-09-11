/* 

Author: Jessica Barre
Email: barrej4@rpi.edu
Program name: "main.c"
Date Due: 9/15/2016 


Number of words that caused out of memory case: 134217728 character pointers.

*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define DOUBLE 2

int size;

int main(){


/** Allocate an array for the words **/

char *substring = "hi";
size = 8;
char ** words = calloc (size, sizeof (char*) );



printf("Allocated initial array of %d character pointers.\n", size);

if(words == NULL){
  perror( "malloc() failed" );
  return EXIT_FAILURE;
}

FILE * file = fopen("hw1-input.txt", "r");

char buff[20];
int i = 0;

while (fscanf(file, "%19[a-zA-Z]%*[^a-zA-Z]", buff) != EOF) {

   
	
  /** Reallocate word** if the current size is not large enough **/
  if(i > size - 1) {
    size = size * DOUBLE;
    words = realloc (words, size * sizeof( char* ) );
    printf("Re-allocated array of %d character pointers.\n", size);
  }

  /** allocate space for current word and copy into words **/

  words[i] = malloc ( sizeof ( char ) * (1 + strlen(buff) ) );

  if(words[i] == NULL) {
    perror( "malloc() failed" );
    return EXIT_FAILURE;
  }

  strcpy(words[i], buff);

  i++;

}

printf("All done (successfully read %d words).\n", i);

/** search the array of words for the desired substring
     and print it if it matches **/

int j = 0;
while ( j < i) {

  if(strstr(words[j], substring) != NULL){
	printf("%s\n", words[j]);
  }


j++;

}


/** Finally free the words array **/
free(words);

return EXIT_SUCCESS;

}




