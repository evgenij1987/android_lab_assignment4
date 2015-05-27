

#include <stdio.h>
void dieGracefully(const char *format, ...) {
	va_list arg;
	va_start(arg, format);
	vfprintf(stderr, format, arg);
	va_end(arg);
	exit(1);
}
