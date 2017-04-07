rm -f *.o *.so
g++ -c -I/usr/lib/jvm/default-java/include/ \
    -I/usr/lib/jvm/default-java/include/linux/ -I/usr/includes/X11/ -fPIC -Wall ActivateWindowJni.cpp
g++ -lc -lX11 -shared ActivateWindowJni.o -o libawjni.so
rm -f ../../libawjni.so
cp -f libawjni.so ../../libs/
ls -l ../../libs/libawjni.so
