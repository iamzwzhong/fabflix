public class User {

    private final String username;
    private final String cid;

    public User(String username, String cid) {
        this.username = username;
        this.cid = cid;
    }

    public String getCid() {
        return this.cid;
    }
}