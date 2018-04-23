package jdk.designPatterns.responsibility;

public class Test {
    public static void main(String[] args) {
        Boy boy = new Boy(false, false, true);
        Handler handler = new CarHandler();
        CarHandler ch = new CarHandler();
        HouseHandler hh = new HouseHandler();
        ResponisibilityHandler ph = new ResponisibilityHandler();
        ch.setHandler(hh);
        handler.handlerRequest(boy);
    }

}
