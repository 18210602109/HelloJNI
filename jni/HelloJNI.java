public class HelloJNI{
    public native void displayHelloJNI();//所有native关键词修饰的都是对本地的声明
    
    static{
        System.loadLibrary("MyLib");//载入本地库
    }

    public static void main(String[] args){
        new HelloJNI().displayHelloJNI();
    }
}
