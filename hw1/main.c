/* 

Author: Jessica Barre
Email: barrej4@rpi.edu
Program name: "main.c"
Date Due: 9/15/2016 


Number of words that caused out of memory case: 33554432 character pointers.

*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define TWICE 2

int size;

int main( int argc, char * argv[]){


  /** Check for correct number of arguments and abort 
      if incorrect **/

  if ( argc != 3 ) {
    fprintf(stderr, "argc is %d\n", argc);
    return EXIT_FAILURE;
  }


  /** Dynamically allocate an array of character pointers with an initial size of 8 **/

  size = 8;
  char ** words = calloc (size, sizeof (char*) );

  if(words == NULL) {
    perror( "malloc() failed" );
    return EXIT_FAILURE;
  }

  printf("Allocated initial array of %d character pointers.\n", size);


  /* Open the file argument and read it into the words array **/

  FILE * file = fopen(argv[1], "r");

  char buff[20]; 
  int word_count = 0;

  while (fscanf(file, "%19[a-zA-Z]%*[^a-zA-Z]", buff) != EOF) {
	

    /** Reallocate the words array if word_count is greater than it's current size **/

    if(word_count > size - 1) {		
      size = size * TWICE;
      words = realloc (words, size * sizeof( char* ) );
      printf("Re-allocated array of %d character pointers.\n", size);
    }


    /** Allocate space for the current word and copy it into the words array **/

    words[word_count] = malloc ( sizeof ( char ) * (1 + strlen(buff) ) );

    if(words[word_count] == NULL) {
      perror( "malloc() failed" );
      return EXIT_FAILURE;
    }

    strcpy(words[word_count], buff);

    word_count++;

  } // Ends while


  printf("All done (successfully read %d words).\n", word_count);
  

  /** Search the words array for the desired substring and print 
      out the words that having a match **/

  printf("Words containing substring \"%s\" are:\n", argv[2]);

  int current = 0;
  while ( current < word_count) {

    if(strstr(words[current], argv[2]) != NULL){
	  printf("%s\n", words[current]);
    }

    current++;

  }

  /** Finally, free elements from memory **/
  free(words);
  free(file);

  return EXIT_SUCCESS;

}
