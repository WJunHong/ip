import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Hello master I am Chi\nHow may I serve you today nyan~?");
        Chi bot = new Chi();
        // Stores the text input of user
        String echo;
        // Request for user input
        echo = userInput.nextLine();
        while (!echo.equals("bye")) {
            try {
                bot.respondToMsg(echo);
                echo = userInput.nextLine();
            } catch (ChiException e) {
                // Output error message
                System.out.println(e);
                echo = userInput.nextLine();
            }
        }
        // Ending statement and close scanner
        System.out.println("Sayonara, see you next time nyan~");
        userInput.close();
    }
}
