package im.denz.jsonlogs;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Usage: java -jar jsonlogs.jar <input.file>");
            System.exit(1);
        }
        KeyCountResult result = JsonLogProcess.parseFile(args[0]);
        result.getExtCount().forEach((ext,count) -> {
            System.out.println(String.format("%s: %s",ext,count));
        });
    }
}
