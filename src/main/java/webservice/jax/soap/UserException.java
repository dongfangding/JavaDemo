package webservice.jax.soap;

import java.io.PrintStream;
import java.io.PrintWriter;

public class UserException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 3155730342029480074L;

    /* (non-Javadoc)
     * @see java.lang.Throwable#getLocalizedMessage()
     */
    @Override
    public String getLocalizedMessage() {
        // TODO Auto-generated method stub
        return super.getLocalizedMessage();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public synchronized Throwable getCause() {
        // TODO Auto-generated method stub
        return super.getCause();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#initCause(java.lang.Throwable)
     */
    @Override
    public synchronized Throwable initCause(Throwable cause) {
        // TODO Auto-generated method stub
        return super.initCause(cause);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace()
     */
    @Override
    public void printStackTrace() {
        // TODO Auto-generated method stub
        super.printStackTrace();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    @Override
    public void printStackTrace(PrintStream s) {
        // TODO Auto-generated method stub
        super.printStackTrace(s);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        // TODO Auto-generated method stub
        super.printStackTrace(s);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#fillInStackTrace()
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        // TODO Auto-generated method stub
        return super.fillInStackTrace();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getStackTrace()
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        // TODO Auto-generated method stub
        return super.getStackTrace();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#setStackTrace(java.lang.StackTraceElement[])
     */
    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        // TODO Auto-generated method stub
        super.setStackTrace(stackTrace);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return super.getMessage();
    }

    public UserException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public UserException() {

    }
}
