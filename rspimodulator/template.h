
#ifndef TEMPLATE_H_

#define TEMPLATE_H_
#define RETRANSMISSIONS 30
#define PULSE_WIDTH 370000
#define SIGNAL_LENGTH 47360000
#define SINGLE_BIT_SIGNAL_LENGTH 8
void dieGracefully(const char *format, ...);
void transmit(char plug []);
void mnanosleep(int nanoseconds);
void send(char sequence [] );
void clr_pin(int pin);
#endif 
