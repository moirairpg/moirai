package es.thalesalv.chatrpg.domain.model;

public class Insertion {

    public static int TOP = 1;
    public static int BOTTOM = 0;

    public static int upFromBottom(int index) {

        return BOTTOM - index;
    }
}
