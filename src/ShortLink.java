import java.time.LocalDateTime;

public class ShortLink {
    private String originalUrl; //исходная ссылка
    private String shortUrl; //сокращенная ссылка
    private long maxClicks; //максимальное количество переходов по ссылке
    private long currentClicks; //текущее количество переходов по ссылке
    private LocalDateTime startTime; //время создания
    private LocalDateTime endTime; //время окончания работы ссылки

    public ShortLink(String originalUrl, String shortUrl, long maxClicks, LocalDateTime startTime, LocalDateTime endTime) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.maxClicks = maxClicks;
        this.currentClicks = 0;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public long getMaxClicks() {
        return maxClicks;
    }

    public void setMaxClicks(long maxClicks) {
        this.maxClicks = maxClicks;
    }

    public long getCurrentClicks() {
        return currentClicks;
    }

    public void incrementClicks() { //увеличиваем счётчик переходов
        currentClicks++;
    }

    public boolean isActive() {
        if (maxClicks > 0 && currentClicks >= maxClicks) { //проверка лимита количеств перехода по ссылке
            return false;
        }

        if (isTimeEnded()) {
            return false;
        }
        return true;
    }

    public boolean isTimeEnded() { //проверка лимита по времени
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(endTime);
    }
}
