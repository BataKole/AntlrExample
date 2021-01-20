grammar Expr;

/* the file name and grammar name must match */

@header {
    package antlr;
}

// Start Variable
prog: (decl | expr)+ EOF        # Program
    ;

decl: ID ':' INT_TYPE '=' NUM   # Declaration
    ;

/* Antlr resolves ambiguity in favor of alternative given first. */
expr: expr '*' expr             # Multiplication
    | expr '+' expr             # Addition
    | expr '-' expr             # Subtraction
    | ID                        # Variable
    | NUM                       # Number
    ;

/* Tokens */
ID : [a-z][a-zA-Z0-9_]*;  // identifiers
NUM: '0' | '-'?[1-9][0-9]*;
INT_TYPE: 'INT';
COMMENT: '--' ~[\r\n]* -> skip;
WS: [ \t\n\r]+ -> skip;
