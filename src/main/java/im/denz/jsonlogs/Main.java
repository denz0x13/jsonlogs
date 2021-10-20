package im.denz.jsonlogs;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Usage: java -jar jsonlogs.jar <input.file>");
            System.exit(1);
        }
        KeyCountResultV2 result = JsonLogProcess.parseFileV2(args[0]);

        result.getExtCount().forEach((h,res) -> {
            System.out.println(String.format("-%d----",h));
            res.forEach((key, value) -> System.out.println(String.format("%s: %s", key, value)));
        });
    }
}
