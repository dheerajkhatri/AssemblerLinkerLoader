; program to swap data between two memory locations
MACRO SWPW &m1,&m2
    LDA &m1
    MOV B, A 
    LDA &m2
    STA &m1 
    MOV A, B
    STA &m2
MEND
jmp start
var db 7,4
start: 
swp var[0],var[1]
swpw var[0],var[1]
hlt