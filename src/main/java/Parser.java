import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    public Parser() {
        // Maybe something in the future
    }

    private int processNumberMsg(String msg, int size) throws ChiException {
        String refine = msg.trim();
        if (refine.split(" ").length > 1) {
            throw new ChiException("Too many words nyan!");
        } else {
            try {
             int index = Integer.parseInt(refine);
             if (index > size) {
                 throw new ChiException("Too big index nyan1")
             } else if (index < 0) {
                 throw new ChiException("No negative indexes nyan!");
             }
             return index - 1;
            } catch (NumberFormatException e) {
                throw new ChiException("This is not a number nyan!");
            }
        }
    }

    private boolean processDeadlineMsg(String msg) throws ChiException {
        String refine = msg.trim();
        String[] refineMore = refine.split("by");
        if (refineMore.length > 2) {
            throw new ChiException("Too many /by-s nyannn!!!");
        } else {
            try {
                LocalDate d = LocalDate.parse(refineMore[1].trim().split(" ")[0].trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (refineMore[1].trim().split(" ").length == 2) {
                    LocalTime t = LocalTime.parse(refineMore[1].trim().split(" ")[1].trim(),
                            DateTimeFormatter.ofPattern("HH:mm"));

                }
            } catch (DateTimeParseException e) {
                return false;
            }
            return true;
        }
    }

    private String processEventMsg(String msg) {

    }
    public String processMessage(String msg, TaskList tl, Storage sge) throws ChiException, IOException {
        // Obtain 1st word
        String[] command = msg.trim().split(" ");
        if (command.length == 1) {
            if (command[0].equals("list")) {
                return tl.getTasksMsg();
            }
            // Unknown message, or command lacks task
            throw new ChiException(msg.trim().toLowerCase());
        } else {
            // Check for keywords
            switch (command[0].toLowerCase()) {
                case "mark":
                    // Retrieve the task from the list
                    int processed = processNumberMsg(msg.substring(4), tl.getSize());
                    Task doneTask = tl.getTask(processed);
                    // Mark as done
                    doneTask.markAsDone();
                    sge.updateFile(doneTask, tl, "mark");
                    return String.format("Great job nyan~!\n%s\n", doneTask);
                case "unmark":
                    int processed = processNumberMsg(msg.substring(6), tl.getSize());
                    Task doneTask1 = tl.getTask(processed);
                    doneTask1.markAsUndone();
                    sge.updateFile(doneTask1, tl, "unmark");
                    return String.format("Let's get it done next time nyan~!\n%s\n", doneTask1);
                case "todo":
                    // Obtain the ToDo
                    Task newTask = new Todo(msg.substring(4).trim(), false);
                    // Add task to list
                    tl.addTask(newTask);
                    sge.updateFile(newTask, tl, "todo");
                    return String.format("Ok! Chi-san has added:\n%s\nYou have %d tasks nyan~!\n",
                            newTask, tl.getSize());
                case "deadline":
                    // Separate task and deadline
                    String[] content = msg.substring(8).split("/by");
                    // Create new Deadline object
                    if (content[0].trim().equals("")) {
                        throw new ChiException("deadline");
                    }
                    // Create new Deadline object
                    LocalDate d = LocalDate.parse(content[1].trim().split(" ")[0].trim(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime t;
                    Task newTask1;
                    if (content[1].trim().split(" ").length == 2) {
                        t = LocalTime.parse(content[1].trim().split(" ")[1].trim(),
                                DateTimeFormatter.ofPattern("HH:mm"));
                        newTask1 = new Deadline(content[0].trim(), d, t, false);
                    } else {
                        newTask1 = new Deadline(content[0].trim(), d, false);
                    }
                    tl.addTask(newTask1);
                    sge.updateFile(newTask1, tl, "deadline");
                    return String.format("Ok! Chi-san has added:\n%s\nYou have %d tasks nyan~!\n",
                            newTask1, tl.getSize());
                case "event":
                    // Separate task and timing
                    String[] content1 = msg.substring(5).split("/at");
                    if (content1[0].trim().equals("")) {
                        throw new ChiException("event");
                    }
                    // Create new Event object
                    LocalDate ds = LocalDate.parse(content1[1].trim().split(" ")[0].trim(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime t1;
                    LocalTime t2;
                    Task newTask2;
                    if (content1[1].trim().split(" ").length == 2) {
                        t1 = LocalTime.parse(content1[1].trim().split(" ")[1].trim().split("-")[0],
                                DateTimeFormatter.ofPattern("HH:mm"));
                        t2 = LocalTime.parse(content1[1].trim().split(" ")[1].trim().split("-")[1],
                                DateTimeFormatter.ofPattern("HH:mm"));
                        newTask2 = new Event(content1[0].trim(), ds, t1, t2, false);
                    } else {
                        newTask2 = new Event(content1[0].trim(), ds, false);
                    }
                    tl.addTask(newTask2);
                    sge.updateFile(newTask2, tl, "event");
                    return String.format("Ok! Chi-san has added:\n%s\nYou have %d tasks nyan~!\n",
                            newTask2, tl.getSize());
                case "delete":
                    int processed = processNumberMsg(msg.substring(6), tl.getSize());
                    Task toDelete = tl.getTask(processed);
                    tl.deleteTask(toDelete);
                    sge.updateFile(toDelete, tl, "delete");
                    return String.format("Chi-san has removed task:\n %s\nYou now have %d tasks nyan~!\n",
                            toDelete, tl.getSize());
                default:
                    // Some message which does not start with a keyword
                    throw new ChiException(msg);
            }
        }
    }
}
