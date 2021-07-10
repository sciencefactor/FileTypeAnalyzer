import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        File inputFile;
        File patterns; //= new File("patterns.db");
        if (args.length != 0) {
            patterns = new File(args[1]);
            inputFile = new File(args[0]);
        } else {
            inputFile = new File("test_files");
            patterns = new File("patterns.db");
        }
        Pattern[] patternList = Pattern.asPatternList(patterns);
        typeCheck(inputFile, patternList);

    }

    public static void typeCheck(File inputFile, Pattern[] patterns) throws InterruptedException {
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        File[] dirListing = inputFile.listFiles();

        ContextSearch search = new ContextSearch();
        search.setMethod(new RabinKarpSearch());

        assert dirListing != null;
        for (File file : dirListing) {
            RunnableSearch fileSearch = new RunnableSearch(file, patterns, search);
            executor.submit(fileSearch);
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
    }
}

class Pattern {
    int priority;
    byte[] regexp;
    String name;

    public Pattern(int priority, byte[] regexp, String name) {
        this.priority = priority;
        this.regexp = regexp;
        this.name = name;
    }

    public static Pattern[] asPatternList(File db) throws FileNotFoundException {
        ArrayList<String> stringPattern = new ArrayList<>();
        if (db.exists()) {
            Scanner scanner = new Scanner(db);
            while (scanner.hasNext()) {
                stringPattern.add(scanner.nextLine());
            }
            scanner.close();
        } else {
            System.out.println("No such pattern database");
        }
        Pattern[] patterns = new Pattern[stringPattern.size()];
        for (int i = 0; i < stringPattern.size(); i++) {
            String[] singPattern = stringPattern.get(i).split(";?\";?\"?");
            Pattern pattern = new Pattern(Integer.parseInt(singPattern[0]), singPattern[1].getBytes(), singPattern[2]);
            patterns[i] = pattern;
        }

        return patterns;
    }

    public static Pattern maxPiority(ArrayList<Pattern> patterns) {
        int compare = -500;
        int index = 0;
        for (int i = 0; i < patterns.size(); i++) {
            if (patterns.get(i).priority > compare) {
                compare = patterns.get(i).priority;
                index = i;
            }
        }
        return patterns.get(index);
    }
}

class RunnableSearch implements Runnable {
    File inputFile;
    Pattern[] patterns;
    ContextSearch searchMethod;

    public RunnableSearch(File inputFile, Pattern[] patterns, ContextSearch searchMethod) {
        this.inputFile = inputFile;
        this.patterns = patterns;
        this.searchMethod = searchMethod;
    }

    @Override
    public void run() {
        ArrayList<Pattern> found = new ArrayList<>();
        for (Pattern pattern : this.patterns) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(this.inputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] slice = new byte[200000];
            while (true) {
                try {
                    if (!(inputStream.available() > 0)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream.read(slice);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (this.searchMethod.find(slice, pattern.regexp) > 0) {
                    found.add(pattern);
                    break;
                }
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (found.size() != 0) {
            Pattern result = Pattern.maxPiority(found);
            System.out.println(this.inputFile.getName() + ": " + result.name);
        } else {
            System.out.println(this.inputFile.getName() + ": " + "Unknown file type");
            ;
        }
    }

    @Override
    public String toString() {
        return "RunnableSearch{" +
                "inputFile=" + inputFile +
                ", pattern_1=" + patterns[0].toString() +
                ", searchMethod=" + searchMethod +
                '}';
    }
}

