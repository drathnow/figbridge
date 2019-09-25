package zedi.pacbridge.test;

public class StringReverser {

    public static String reverse(String string) {
        if (string.length() == 0)
            return string;
        return reverse(string.substring(1, Math.max(1, string.length())))+string.charAt(0);
    }
    
}
