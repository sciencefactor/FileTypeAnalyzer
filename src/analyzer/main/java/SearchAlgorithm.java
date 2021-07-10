import java.util.ArrayList;

interface SearchAlgorithm {
    int search(byte[] text, byte[] pattern);
}

class naiveSearch implements SearchAlgorithm {
    public int search(byte[] text, byte[] pattern) {
        return naiveSearcher(text, pattern);
    }

    private static int naiveSearcher(byte[] text, byte[] pattern) {
        int counter = 0;

        for (int i = 0; i < text.length - pattern.length + 1; i++) {
            int checker = 0;
            for (int j = 0; j < pattern.length; j++) {
                if (text[i + j] == pattern[j]) {
                    checker++;
                } else {
                    break;
                }

            }
            if (checker == pattern.length) {
                counter++;
            }
        }
        return counter;
    }


}

class KMPSearch implements SearchAlgorithm {
    public int search(byte[] text, byte[] pattern) {
        return searchKMP(text, pattern);
    }

    private static byte[] prefixFunction(byte[] str) {
        byte[] prefixFunc = new byte[str.length];
        for (int i = 1; i < str.length; i++) {
            int j = prefixFunc[i - 1];
            while (j > 0 && str[i] != str[j]) {
                j = prefixFunc[j - 1];
            }
            if (str[i] == str[j]) {
                j += 1;
            }
            prefixFunc[i] = (byte)j;
        }
        return prefixFunc;
    }

    private static int searchKMP(byte[] text, byte[] pattern) {
        byte[] prefixFunc = prefixFunction(pattern);
        int occurrences = 0;
        int j = 0;
        for (int k : text) {
            while (j > 0 && k != pattern[j]) {
                j = prefixFunc[j - 1];
            }
            if (k == pattern[j]) {
                j += 1;
            }
            if (j == pattern.length) {
                occurrences++;
                j = prefixFunc[j - 1];
            }
        }
        return occurrences;
    }

}

class RabinKarpSearch implements SearchAlgorithm {


    @Override
    public int search(byte[] text, byte[] pattern) {
        return RabinKarp(text, pattern);
    }

    /* 1 */
    public static int RabinKarp(byte[] text, byte[] pattern) {
        /* 2 */
        int a = 53;
        long m = 1_000_000_000 + 9;

        /* 3 */
        long patternHash = 0;
        long currSubstrHash = 0;
        long pow = 1;

        for (int i = 0; i < pattern.length; i++) {
            patternHash += pattern[i] * pow;
            patternHash %= m;

            currSubstrHash += text[text.length - pattern.length + i] * pow;
            currSubstrHash %= m;

            if (i != pattern.length - 1) {
                pow = pow * a % m;
            }
        }

        /* 4 */
        int occurrences = 0;

        for (int i = text.length; i >= pattern.length; i--) {
            if (patternHash == currSubstrHash) {
                boolean patternIsFound = true;

                for (int j = 0; j < pattern.length; j++) {
                    if (text[i - pattern.length + j] != pattern[j]) {
                        patternIsFound = false;
                        break;
                    }
                }

                if (patternIsFound) {
                    occurrences++;
                }
            }

            if (i > pattern.length) {
                /* 5 */
                currSubstrHash = (currSubstrHash - text[i - 1] * pow % m + m) * a % m;
                currSubstrHash = (currSubstrHash + text[i - pattern.length - 1]) % m;
            }
        }


        return occurrences;
    }

    public static long charToLong(char ch) {
        return (long)(ch - 'A' + 1);
    }
}