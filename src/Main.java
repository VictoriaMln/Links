import java.util.Scanner;
import java.util.UUID;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LinkManager linkManager = new LinkManager();

        while (true) {
            System.out.println("\nМЕНЮ (выберите пункт):");
            System.out.println("1. Создать ссылку");
            System.out.println("2. Открыть ссылку");
            System.out.println("3. Показать все ссылки");
            System.out.println("4. Удалить короткую ссылку");
            System.out.println("5. Изменить количество переходов по короткой ссылке");
            System.out.println("0. Выйти");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createLink(scanner, linkManager);
                    break;
                case "2":
                    openLink(scanner, linkManager);
                    break;
                case "3":
                    showAllLinks(scanner, linkManager);
                    break;
                case "4":
                    deleteLink(scanner, linkManager);
                    break;
                case "5":
                    changeLink(scanner, linkManager);
                    break;
                case "0":
                    System.out.println("Выход");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная цифра");
            }
        }
    }

    private static void createLink(Scanner scanner, LinkManager linkManager) { //создание ссылки
        linkManager.removeLinks(); //удаляем неактивные ссылки
        String userId = checkUserId(scanner); //спрашиваем ID пользователя
        System.out.println("Введите исходную ссылку");
        String originaUrl = scanner.nextLine();
        System.out.println("Введите количество доступных переходов (0 - без ограниченний)");
        long maxClicks = Long.parseLong(scanner.nextLine());
        var shortLink = linkManager.createShortLink(originaUrl, userId, maxClicks);
        System.out.println("Ссылка: " + shortLink.getShortUrl());
    }

    private static void openLink(Scanner scanner, LinkManager linkManager) { //переход по ссылке
        linkManager.removeLinks(); //удаляем неактивные ссылки
        String userId = checkUserId(scanner); //спрашиваем ID пользователя
        System.out.println("Введите короткую ссылку: ");
        String shortUrl = scanner.nextLine();
        String result = linkManager.openShortLink(userId, shortUrl);
        if (result == null) {
            System.out.println("Ссылка недоступна ");
        } else {
            System.out.println("Оригинальный URL: " + result);
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Не поддерживается");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                System.out.println("Не поддерживается");
                return;
            }
            try {
                desktop.browse(new URI(result));
            } catch (Exception e) {
                System.out.println("Ошибка");
                e.printStackTrace();
            }
        }
    }

    private static void showAllLinks(Scanner scanner, LinkManager linkManager) { //показать список всех ссылок пользователя
        linkManager.removeLinks(); //удаляем неактивные ссылки
        String userId = checkUserId(scanner); //спрашиваем ID пользователя
        var user = linkManager.getCreateUser(userId);
        var links = user.getLinks();
        if (links.isEmpty()) {
            System.out.println("Ссылки отсутствуют ");
            return;
        }
        System.out.println("Список ссылок: ");
        for (ShortLink link : links) {
            String status = link.isActive() ? "Активна" : "Не активна";
            System.out.printf("Короткая: %s; Оригинальная: %s; Количество переходов: %d/%d; Статус: %s;\n", link.getShortUrl(), link.getOriginalUrl(), link.getCurrentClicks(), link.getMaxClicks(), status);
        }
    }

    public static void deleteLink(Scanner scanner, LinkManager linkManager) {
        linkManager.removeLinks(); //удаляем неактивные ссылки
        String userId = checkUserId(scanner); //спрашиваем ID пользователя
        System.out.println("Введите короткую ссылку, которую необходимо удалить: ");
        String shortUrl = scanner.nextLine();
        if (linkManager.deleteShortLink(userId, shortUrl)) {
            System.out.println("Ссылка удалена ");
        }
        else {
            System.out.println("Невозможно удалить ");
        }
    }

    public static void changeLink(Scanner scanner, LinkManager linkManager) {
        linkManager.removeLinks(); //удаляем неактивные ссылки
        String userId = checkUserId(scanner); //спрашиваем ID пользователя
        System.out.println("Введите короткую ссылку, которую необходимо редактировать: ");
        String shortUrl = scanner.nextLine();
        System.out.println("Введите количество доступных переходов (0 - без ограниченний)");
        long maxClicks = Long.parseLong(scanner.nextLine());
        if (linkManager.changeShortLink(userId, shortUrl, maxClicks)) {
            System.out.println("\nСсылка изменена ");
        }
        else {
            System.out.println("Невозможно изменить, ссылки не существует ");
        }
    }

    public static String checkUserId(Scanner scanner) {
        System.out.println("Введите UUID. Нажмите Enter для генерации нового");
        String userId = scanner.nextLine().trim();
        if (userId.isEmpty()) {
            userId = UUID.randomUUID().toString();
            System.out.println("Ваш UUID: " + userId);
        }
        else {
            System.out.println("Вы используете UUID: " + userId);
        }
        return userId;
    }
}
