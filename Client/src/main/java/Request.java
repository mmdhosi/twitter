public class Request {
    private RequestTypes requestType;
    private String username;

    public RequestTypes getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypes requestType) {
        this.requestType = requestType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    enum RequestTypes{
        GET_PROFILE, FOLLOW, UNFOLLOW, BLOCK, UNBLOCK
    }
}
