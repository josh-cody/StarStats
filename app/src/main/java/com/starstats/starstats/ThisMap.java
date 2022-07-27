package com.starstats.starstats;

public class ThisMap {
    String map;
    String mode;
    String eventStart, eventEnd;


    public ThisMap(String map, String mode, String eventStart, String eventEnd) {

        char[] eventStartArray = eventStart.toCharArray();

        //2022-07-19T08:00:00.000Z"
        eventStartArray = insert(eventStartArray, '-', 4); //2022-0719T080000.000Z"
        eventStartArray = insert(eventStartArray, '-', 7);
        eventStartArray = insert(eventStartArray, ':', 13);
        eventStartArray = insert(eventStartArray, ':', 16);

        eventStart = String.valueOf(eventStartArray);

        char[] eventEndArray = eventEnd.toCharArray();

        //2022-07-19T08:00:00.000Z"
        eventEndArray = insert(eventEndArray, '-', 4); //2022-0719T080000.000Z"
        eventEndArray = insert(eventEndArray, '-', 7);
        eventEndArray = insert(eventEndArray, ':', 13);
        eventEndArray = insert(eventEndArray, ':', 16);

        eventEnd = String.valueOf(eventEndArray);


        this.map = map;
        this.mode = mode;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }

    private static char[] insert(char[] a, char key, int index)
    {
        char[] result = new char[a.length + 1];

        System.arraycopy(a, 0, result, 0, index);
        result[index] = key;
        System.arraycopy(a, index, result, index + 1, a.length - index);

        return result;
    }

}

