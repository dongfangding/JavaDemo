package jdk.designPatterns.responsibility;

public class ResponisibilityHandler implements Handler {
    private Handler handler;

    ResponisibilityHandler() {
        this.handler = new ResponisibilityHandler();
    }

    ResponisibilityHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void handlerRequest(Boy boy) {
        if (boy.isHasResponsibility()) {
            System.out.println("终于还有一个责任心，程序终止！");
        } else {
            handler.handlerRequest(boy);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
