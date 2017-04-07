echo Generate Jni header from compiled Java class
rm com_caine_ui_ActivateWindowJni.h
cd ../main/java/

javac com/caine/ui/ActivateWindowJni.java
javah com.caine.ui.ActivateWindowJni

rm -f com/caine/ui/*.class

mv com_caine_ui_ActivateWindowJni.h ../../jni/
cd ../../jni/
