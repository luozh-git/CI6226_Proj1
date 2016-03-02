package ntu.ci6226;/*
 * Created on 07.06.2005
 */

import ntu.ci6226.Person;

import java.util.*;

/**
 * @author ley
 *         <p>
 *         created first in project xml5_coauthor_graph
 */
public class Publication {

    private static int maxNumberOfAuthors = 0;
    private String key;
    private String type;
    private String title;
    private Integer year;
    private String venue;

    private Person[] authors;    // or editors

    public Publication(String key, String type, String title, String year, String venue, Person[] persons) {
        this.key = key;
        this.type = type;
        this.title = title;
        this.year = Integer.parseInt(year);
        this.venue = venue;
        authors = persons;
        if (persons.length > maxNumberOfAuthors)
            maxNumberOfAuthors = persons.length;
    }

    public Person[] getAuthors() {
        return authors;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getVenue() {
        return venue;
    }


}
