#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cstddef>
#include <X11/Xlib.h>
#include <X11/Xmd.h>
#include <X11/Xatom.h>

#include "com_caine_ui_ActivateWindowJni.h"

#define MAX_PROPERTY_VALUE_LEN 4096

static bool isTargetWindow(Display * display, Window window, char * windowName) {
    char* windowNamePtr;

    if (XFetchName(display, window, &windowNamePtr) != 0) {

        if (strcmp(windowNamePtr, windowName) == 0) {
            return true;
        }
    }

    return false;
}

// from https://www.experts-exchange.com/questions/21341279/Enumerating-through-all-windows-XQueryTree-using-Xlib-h-and-grabbing-the-text-not-just-title-using-something-like-XGetTextProperty.html
static Window findTargetWindow (Display *display, Window rootWindow, char * windowName)
{
    Window parent;
    Window *children;

    unsigned int noOfChildren;
    int status;

    if (isTargetWindow(display, rootWindow, windowName)) {
        return rootWindow;
    }

    status = XQueryTree (display, rootWindow, &rootWindow, &parent, &children, &noOfChildren);

    if (status == 0) {
        printf ("ERROR - Could not query the window tree. Aborting.\r\n");
        return (Window) NULL;
    }
    if (noOfChildren == 0) return (Window) NULL;

    for (unsigned i=0; i < noOfChildren; i++) {
        Window targetWindow = findTargetWindow(display, children[i], windowName);
        if (targetWindow != (Window) NULL) {
            XFree ((char*) children);
            return targetWindow;
        }
    }

    XFree ((char*) children);
    return (Window) NULL;
}

// from wmctrl
static char *get_property (Display *disp, Window win, /*{{{*/
        Atom xa_prop_type, const char *prop_name, unsigned long *size) {
    Atom xa_prop_name;
    Atom xa_ret_type;
    int ret_format;
    unsigned long ret_nitems;
    unsigned long ret_bytes_after;
    unsigned long tmp_size;
    unsigned char *ret_prop;
    char *ret;

    xa_prop_name = XInternAtom(disp, prop_name, False);

    /* MAX_PROPERTY_VALUE_LEN / 4 explanation (XGetWindowProperty manpage):
     *
     * long_length = Specifies the length in 32-bit multiples of the
     *               data to be retrieved.
     */
    if (XGetWindowProperty(disp, win, xa_prop_name, 0, MAX_PROPERTY_VALUE_LEN / 4, False,
            xa_prop_type, &xa_ret_type, &ret_format,
            &ret_nitems, &ret_bytes_after, &ret_prop) != Success) {
        printf("Cannot get %s property.\n", prop_name);
        return NULL;
    }

    if (xa_ret_type != xa_prop_type) {
        printf("Invalid type of %s property.\n", prop_name);
        XFree(ret_prop);
        return NULL;
    }

    /* null terminate the result to make string handling easier */
    tmp_size = (ret_format / (32 / sizeof(long))) * ret_nitems;
    ret = (char *) malloc(tmp_size + 1);
    memcpy(ret, ret_prop, tmp_size);
    ret[tmp_size] = '\0';

    if (size) {
        *size = tmp_size;
    }

    XFree(ret_prop);
    return ret;
}

// from wmctrl
static int client_msg(Display *disp, Window win, const char *msg, /* {{{ */
    unsigned long data0, unsigned long data1,
    unsigned long data2, unsigned long data3,
    unsigned long data4) {
  XEvent event;
  long mask = SubstructureRedirectMask | SubstructureNotifyMask;

  event.xclient.type = ClientMessage;
  event.xclient.serial = 0;
  event.xclient.send_event = True;
  event.xclient.message_type = XInternAtom(disp, msg, False);
  event.xclient.window = win;
  event.xclient.format = 32;
  event.xclient.data.l[0] = data0;
  event.xclient.data.l[1] = data1;
  event.xclient.data.l[2] = data2;
  event.xclient.data.l[3] = data3;
  event.xclient.data.l[4] = data4;

  if (XSendEvent(disp, DefaultRootWindow(disp), False, mask, &event)) {
    return EXIT_SUCCESS;
  }
  else {
    fprintf(stderr, "Cannot send %s event.\n", msg);
    return EXIT_FAILURE;
  }
}

// from wmctrl
static void activateTargetWindow(Display * display, Window targetWindow)
{
    printf("target is 0x%lx\n", targetWindow);

    unsigned long *desktop;

    /* desktop ID */
    if ((desktop = (unsigned long *)get_property(display, targetWindow,
            XA_CARDINAL, "_NET_WM_DESKTOP", NULL)) == NULL) {
        if ((desktop = (unsigned long *)get_property(display, targetWindow,
                XA_CARDINAL, "_WIN_WORKSPACE", NULL)) == NULL) {
            printf("Cannot find desktop ID of the window.\n");
        }
    }

    if (desktop) {
        if (client_msg(display, DefaultRootWindow(display),
                    "_NET_CURRENT_DESKTOP",
                    *desktop, 0, 0, 0, 0) != EXIT_SUCCESS) {
            printf("Cannot switch desktop.\n");
        }
        XFree(desktop);
    }

    client_msg(display, targetWindow, "_NET_ACTIVE_WINDOW", 0, 0, 0, 0, 0);
    XMapRaised(display, targetWindow);
}

static char* create_char_array(JNIEnv * env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    char* buf = (char *) malloc(len);
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

static void free_char_array(char * array) {
    free(array);
}

static Window getRootWindow(Display * display) {
    int screen = DefaultScreen (display);
    return RootWindow (display, screen);
}

JNIEXPORT jint JNICALL Java_com_caine_ui_ActivateWindowJni_activateWindow(JNIEnv *env, jobject obj,
        jbyteArray javaWindowName){

    Display *display = XOpenDisplay(NULL);
    if(display == NULL){
        printf("Could not open display\n");
        return -1;
    }

    char *windowName = create_char_array(env, javaWindowName);
    Window rootWindow = getRootWindow(display);
    Window targetWindow = findTargetWindow(display, rootWindow, windowName);

    if (targetWindow != (Window) NULL) {
        activateTargetWindow(display, targetWindow);
    } else {
        printf("Can not find target window\n");
        return -1;
    }

    // Free resources. Note: if not close display, the activation would not work!
    free_char_array(windowName);
    XCloseDisplay(display);
    return 0;
}
