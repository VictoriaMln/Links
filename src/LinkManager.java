import java.io.File;
import java.io.IOException;import java.io.File;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;

public class LinkManager {
    private Map<String, User> userMap = new HashMap<>(); //для хранения всех пользователей по UUID в качестве ключа

    public User getCreateUser(String userId) { //получить или создать пользователя
        if (!userMap.containsKey(userId)) {
            User newUser = new User(userId); //создаем и добавляем нового пользователя, если он не существует
            userMap.put(userId, newUser);
        }
        return userMap.get(userId);
    }
    //метод для создания короткой ссылки
    public ShortLink createShortLink(String originalUrl, String userId, long maxClicks) {
        String shortUrl = "shrt.com/" + generateRandomCode();
        LocalDateTime now = LocalDateTime.now(); //настоящее время
        LocalDateTime endTime = now.plusHours(24); //время окончания действия короткой ссылки
        ShortLink shortLink = new ShortLink(originalUrl, shortUrl, maxClicks, now, endTime);
        User user = getCreateUser(userId); //проверяем наличие пользователя
        user.getLinks().add(shortLink); //добавляем пользователю новую короткую ссылку
        return shortLink;
    }

    public String openShortLink(String userId, String shortUrl) { //метод для проверки ссылки (переход по ссылке)
        User user = userMap.get(userId);
        if (user == null) {
            return null;
        }
        //проверяем наличие короткой ссылки у данного пользователя
        Optional<ShortLink> optionalLink = user.getLinks().stream().filter(link -> link.getShortUrl().equals(shortUrl)).findFirst();
        if (optionalLink.isEmpty()) {
            return null;
        }
        ShortLink shortLink = optionalLink.get();
        if (!shortLink.isActive()) { //проверяем, не закончились ли лимиты по ссылке
            return null;
        }
        shortLink.incrementClicks(); //увеличиваем количество переходов по ссылке
        return shortLink.getOriginalUrl(); //возвращаем длинную ссылку, соответствующую короткой
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 4);//генерируем короткую ссылку
    }

    public void removeLinks() { //метод для удаления просроченных ссылок или тех, у которых вышел лимит переходов
        for (User user : userMap.values()) {
            user.getLinks().removeIf(link -> !link.isActive());
        }
    }

    public boolean deleteShortLink(String userId, String shortUrl) { //метод для удаления коротких ссылок по запросу пользователя
        User user = userMap.get(userId);
        if (user == null) {
            return false;
        }
        return user.getLinks().removeIf(link -> link.getShortUrl().equals(shortUrl));
    }

    public boolean changeShortLink(String userId, String shortUrl, long maxClicks) {
        User user = userMap.get(userId);
        if (user == null) {
            return false;
        }
        for (ShortLink link : user.getLinks()) {
            if (link.getShortUrl().equals(shortUrl)) {
                link.setMaxClicks(maxClicks);
            }
        }
        System.out.printf("Установлено максимальное количество переходов для ссылки %s : %d", shortUrl, maxClicks);
        return true;
    }
}