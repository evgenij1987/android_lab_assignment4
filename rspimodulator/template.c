#include <time.h>
#include <stdint.h>
#include <bcm2835.h>
#include <string.h>
// Access from ARM Running Linux


#define BCM2708_PERI_BASE        0x20000000
#define GPIO_BASE                (BCM2708_PERI_BASE + 0x200000) /* GPIO controller */

#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include "template.h"

#define PAGE_SIZE (4*1024)
#define BLOCK_SIZE (4*1024)

int  mem_fd;
void *gpio_map;

// I/O access
volatile unsigned *gpio;


// GPIO setup macros. Always use INP_GPIO(x) before using OUT_GPIO(x) or SET_GPIO_ALT(x,y)
#define INP_GPIO(g) *(gpio+((g)/10)) &= ~(7<<(((g)%10)*3))
#define OUT_GPIO(g) *(gpio+((g)/10)) |=  (1<<(((g)%10)*3))
#define SET_GPIO_ALT(g,a) *(gpio+(((g)/10))) |= (((a)<=3?(a)+4:(a)==4?3:2)<<(((g)%10)*3))

#define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
#define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0

void setup_io();

//
// Set up a memory regions to access GPIO
//
void setup_io()
{
   /* open /dev/mem */
   if ((mem_fd = open("/dev/mem", O_RDWR|O_SYNC) ) < 0) {
      printf("can't open /dev/mem \n");
      exit(-1);
   }

   /* mmap GPIO */
   gpio_map = mmap(
      NULL,             //Any adddress in our space will do
      BLOCK_SIZE,       //Map length
      PROT_READ|PROT_WRITE,// Enable reading & writting to mapped memory
      MAP_SHARED,       //Shared with other processes
      mem_fd,           //File to map
      GPIO_BASE         //Offset to GPIO peripheral
   );

   close(mem_fd); //No need to keep mem_fd open after mmap

   if (gpio_map == MAP_FAILED) {
      printf("mmap error %d\n", (int)gpio_map);//errno also set!
      exit(-1);
   }

   // Always use volatile pointer!
   gpio = (volatile unsigned *)gpio_map;
   printf("SETUP FINISHED");
} // setup_io


/* this will register an contructor that calls the setup_io() when the app starts */
static void con() __attribute__((constructor));

void con() {
    setup_io();
}



/* use this to send power to a pin */
void set_pin(int pin) {
	GPIO_SET = 1<<pin;

}

/* use this to set no power to a pin */
void clr_pin(int pin) {
	GPIO_CLR = 1<<pin;
}


int main(int argv, char** argc)
{
    // this is called after the contructor!
	
	char plug []={'1','0', '0', '0', '0', '1', '0', '0', '0', '0','0','1','\n'};
	
	
 
    // you must run this as root!!!

    INP_GPIO(17); // must use INP_GPIO before we can use OUT_GPIO
    OUT_GPIO(17);
    
    int i;
    for(i=0;i<20;i++){
		
		transmit(plug);
	}
    return 0;
}
/**
 * Method accepts a string containing a sequence of ones and zeroes.
 * The whole sequence will be transmitted via radio according to the
 * signal encoding of mumbi FS3000 wireless switch unit. Make sure
 * the string contains a house code 5 bits a switch code 5 bits, an ON/OFF
 * bit and inverted ON/FF bit for synchronization.
 */
void transmit(char plug []){
	size_t sequence_lenght=strlen(plug)-1;
	if(sequence_lenght!=12){
		dieGracefully("Sequence for modulation too long %d",sequence_lenght);		
	}
	
	set_pin(17); //init power
	mnanosleep(370000);
	
	
	int i;
	for(i=0;i<sequence_lenght;i++){
		
		if(plug[i]=='1'){
			printf("bit %c \n",plug[i]);
			clr_pin(17);
			mnanosleep(1110000); //3*370000
			set_pin(17);
			mnanosleep(370000);
			clr_pin(17);
			mnanosleep(1110000);
			set_pin(17);
			mnanosleep(370000);
			
								
		}else if(plug[i]=='0'){
			printf("bit %c \n",plug[i]);
			clr_pin(17);
			mnanosleep(1110000); //3*370000
			set_pin(17);
			mnanosleep(1110000);
			clr_pin(17);
			mnanosleep(370000);
			set_pin(17);
			mnanosleep(370000);								
		}

		
	}
	printf("long sleep \n");
	clr_pin(17);
	mnanosleep(47360000-(8*12*370000+370000));
}

void mnanosleep(int nanoseconds){
	
	struct timespec req={0},rem={0};
	req.tv_nsec=nanoseconds;
	nanosleep(&req,&rem);
}


void dieGracefully(const char *format, ...) {
	va_list arg;
	va_start(arg, format);
	vfprintf(stderr, format, arg);
	va_end(arg);
	exit(1);
}
