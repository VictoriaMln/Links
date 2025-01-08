import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId; //хранится UUID
    private List<ShortLink> links = new ArrayList<>(); //список ссылок, принадлежащих пользователю

    public User(String userId) {
        this.userId = userId;
        this.links = new ArrayList<>();
    }

    public List<ShortLink> getLinks() {
        return links;
    }
}
