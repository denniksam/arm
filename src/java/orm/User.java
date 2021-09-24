package orm;
/*
    ORM - Отображение таблицы Users
*/
public class User {
    private String id ;
    private String login ;
    private String passHash ;
    private String name ;
    private String avatar ;
    
    private int isDeleted ;

    public User(String login, String passHash, String name, String avatar) {
        this.login    = login;
        this.passHash = passHash;
        this.name     = name;
        this.avatar   = avatar;
    }

    public User(String id, String login, String passHash, String name, String avatar) {
        this.id       = id;
        this.login    = login;
        this.passHash = passHash;
        this.name     = name;
        this.avatar   = avatar;
    }

    public User() {
        this.login    = null ;
        this.passHash = null ;
        this.name     = null ;
        this.avatar   = null ;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
    
}
