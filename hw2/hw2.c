/*

Author: Jessica Barre
Email: barrej4@rpi.edu
Program name: "main.c"
Date Due: 10/3/2016


*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

char* readfile(char * filename, size_t*);
char** parseExpression(char *, int *, size_t);
void printExpression(char**, int);
int evaluateExpression(char**, int, int);
int getResult(char, int[], int);
void testPipe(int rc);
void testFork( int pid);
void testWait(int status, int pid);

int child_result = -1;

void testPipe(int rc) {

    if ( rc == -1 )
    {
      perror( "pipe() failed" );
      exit(EXIT_FAILURE);
    }
}

void testFork(int pid) {
    if( pid == -1)
    {
        perror( "fork() failed" );
        exit(EXIT_FAILURE);
    }
}

void testWait(int status , int child_pid) {

    if ( WIFSIGNALED( status ) )
    {
      printf( "PID %d: child %d terminated abnormally\n", getpid(), child_pid );
    }
    else if ( WIFEXITED( status ) )
    {
      int rc = WEXITSTATUS( status );
      if(rc != 0){
        printf("PID %d: child %d terminated successfully with nonzero exit status %d\n", getpid(), child_pid, rc );
        exit(EXIT_FAILURE);
      }
    }
}

int getResult(char op, int operands[], int nth_child){

    int next;
    int result = 0;

    if (op == '+'){
        for (next = 0; next < nth_child; next++){
            result = result + operands[next];
        }
    }
    else if(op == '-'){
        result = operands[0];
        for (next = 1; next < nth_child; next++){
            result = result - operands[next];
        }
    }
    else if (op == '*'){
        result = 1;
        for (next = 0; next < nth_child; next++)
            result = result * operands[next];
    }
    else if(op == '/'){
        result = operands[0];
        for (next = 1; next < nth_child; next++){
                result = result/operands[next];
        }
    }

    return result;
}

int evaluateExpression(char** elements, int count, int rp){

  if (strchr("+-/*", *elements[1]) == NULL) {
    printf("PID %d: ERROR: unknown \"%s\" operator; exiting\n", getpid(), elements[1]);
    fflush(NULL);
    exit(EXIT_FAILURE);
  }

  char op = *elements[1];
  int numChildren = count - 3; /** where 3 takes off ( , ) and operator */
  int operands[20];
  int p[numChildren][2];   /** Create a pipe for each operand **/
  int rc = -1;
  int i = 2;
  int nthChild = 0;

  printf("PID %d: Starting \"%c\" operation\n", getpid(), op);
  fflush(NULL);

  if (numChildren < 2) {
    printf("PID %d: ERROR: not enough operands; exiting\n", getpid());
    fflush(NULL);
    }



  while (i < count) {

    int num = -1;

    /** Pipe and fork if the element is a positive/negative digit or a subexpression **/

    if ( isdigit(*elements[i]) != 0 || strchr("(", *elements[i]) != NULL ||
       (strchr("-", *elements[i]) && isdigit(elements[i][1]) != 0 ) ) {

      if (op == '/' && i > 0) {

          if(*elements[i] == '0') {
            printf( "PID %d: ERROR: division by zero not allowed; exiting\n", getpid());
            int status;
            wait(&status);
            exit(EXIT_FAILURE);
          }

      }

      /** pipe **/
      rc = pipe(p[nthChild]);
      testPipe(rc);


      /** If element is a digit parse it **/
      if ( isdigit(*elements[i]) ||
      (strchr("-", *elements[i]) && isdigit(elements[i][1]) != 0 ) ) {

        num = atoi(elements[i]);

      }

      /** fork **/
      int pid = fork();
      testFork(pid);


      if (pid == 0) {

        if ( strchr("(", *elements[i]) != NULL ) { /** This child process is a subexpression **/

          int subCount = 0;
          int len = strlen(elements[i]);
          char** subExp = parseExpression(elements[i], &subCount, len);

          /** Prints the current expression */
          printf("PID %d: My expression is \"", getpid());
          printExpression(subExp, subCount);
          printf("\"\n");

          num = evaluateExpression(subExp, subCount, p[nthChild][1]);

          if (op == '/' && nthChild > 0) {

            if (child_result == 0)  {

              printf( "PID %d: ERROR: division by zero not allowed; exiting\n", getpid());
              int status;
              wait(&status);
              exit(EXIT_FAILURE);

          }

        }

          /** Write the number to the pipe **/
          if (num != 0) {

            close(p[nthChild][0]);
            p[nthChild][0] = -1;
            write(p[nthChild][1], &num, sizeof(num));

          } else {

            perror("Can't evaluate expression");

          }

        }

        else { /** This child process is a digit **/

          printf("PID %d: My expression is \"%d\"\n", getpid(), num);
          fflush(NULL);

          close(p[nthChild][0]);
          p[nthChild][0] = -1;

          write(p[nthChild][1], &num, sizeof(num));
          printf( "PID %d: Sending \"%d\" on pipe to parent\n", getpid(), num);

        }

       exit(EXIT_SUCCESS);

     } /** end child process **/

     nthChild++;

  }

  i++;

  } /** End while: all child processes are finished **/


  /** Read and Waitpid **/

  int child = 0;

  while (child < numChildren)c{

    close (p[child][1]);
    p[child][1] = -1;

    int status;
    pid_t child_pid = wait(&status);
    testWait(status, child_pid);

    read(p[child][0], &operands[child], sizeof(int));

    child++;

  }


/** Get the Final Result if there are enough operands **/

  if (numChildren < 2) {
      exit(EXIT_FAILURE);
    }

    int result = getResult(op, operands, numChildren);

    if(rp != -1) {

      child_result = result;
      write( rp, &result, sizeof(result) );

      printf("PID %d: Processed \"", getpid());
      printExpression(elements, count);

      printf("\"; sending \"%d\" on pipe to parent\n", result);
      return rp;

    } else {

       return result;

    }

    return 0;
}

void printExpression(char** elements, int count){

  printf("(");
  int i = 1;
  while (i < count) {

    if(strchr("(", *elements[i])){
      printf(" %s", elements[i]);
    }
    else if(strchr("-", *elements[i]) && isdigit(elements[i][1]) != 0 ){
     printf(" %s", elements[i]);
    }
    else if( isdigit(*elements[i]) != 0 ){

     printf(" %s", elements[i]);
    }
    else if(strchr(")", *elements[i])){
      printf("%s", elements[i]);
    }
    else{
    printf("%s", elements[i]);
    }

    i=i+1;

  }

}

char** parseExpression(char * expression, int * count, size_t len) {

  int size = 3;          /** The current size of the elements array **/

  /** Dynamically allocate an elements array to hold 3 elements **/
  char** elements = calloc (size, sizeof (char*) );

  if(elements == NULL) {
    perror( "calloc() failed" );
    exit(EXIT_FAILURE);
  }

  int current = 1;       /** The current index of the expression **/

  /** assign opening parens and operator to first two elements **/
  elements[*count] = malloc ( sizeof ( char ) * 1);
  if(elements[*count] == NULL) {
    perror( "malloc() failed" );
    exit(EXIT_FAILURE);
  }

  strcpy(elements[*count],"(");

  *count = *count + 1;

  int next = current;
  while(expression[next] != ' '){
    next++;
  }

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

#if 0
   printf("count is %d\n", *count);
   int k = 0;
  while (k  < *count){
    printf("ELEMENT at %d: %s \n", k, elements[k]);
    k++;
  }
#endif

  return elements;

}

char* readfile(char * filename, size_t *len){

  /** Open the file **/
  FILE * file;

  if( (file = fopen(filename, "r") ) == NULL) {
    fprintf(stderr, "unable to open file %s", filename);
    exit(EXIT_FAILURE);
  }

  char buff[40]; /** This will hold the expression we want parsed **/
  char* expression = malloc( sizeof( char ) );;

  /** Start by searching for the line with the elements **/

  while ( fgets(buff, sizeof(buff), file) ) {

    /** We only want an expression, ignore the lines
       with comments, blanks, etc  **/

    if (buff[0] == '(') {

      *len = strlen(buff);

      /** Remove newline **/
      if (*len > 0 && buff[*len-1] == '\n') buff[--*len] = '\0';

      *len = strlen(buff);

      expression = realloc(expression, sizeof( char ) * (1 + *len) );

      strcpy(expression, buff);

    } else {
      continue;
    }

  } // End While

    return expression;


}
/** MAIN **/

int main( int argc, char * argv[]){
  /** Check for the proper arg count **/

  if ( argc != 2 ) {
    fprintf(stderr, "ERROR: Invalid arguments\nUSAGE: ./a.out <input-file>\n");
    return EXIT_FAILURE;
  }

  size_t len = 0;  /** The length of the expression  **/
  char* expression = readfile(argv[1], &len);

  int count = 0;    /** The number of elements in the elements array **/
  char** elements = parseExpression(expression, &count, len);


  /** Now that all the elements of the expression are broken up,
      that is, we have a dynamically allocated array of elements,
      such as (+ 5 3 (* 7 -8))) = ["(", "+", "5", "3", (* 7 -8)",
      we can now calculate the expression with IPC */


  printf("PID %d: My expression is \"", getpid());
  printExpression(elements, count);
  printf("\"\n");
  fflush(NULL);

  int answer = evaluateExpression(elements, count, -1);

  printf("PID %d: Processed \"", getpid());
  printExpression(elements, count);
  printf("\"; final answer is \"%d\"", answer);


  free(expression);
  free(elements);

  return EXIT_SUCCESS;

}
