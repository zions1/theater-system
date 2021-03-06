package bd2.adminPanel.model.dictionaries;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dayofweek", schema = "theater")
public class DayOfWeek implements Comparable<DayOfWeek> {
    @Id
    @Column(name = "day_of_week_id")
    private int id;
    private String name;

    public DayOfWeek() {

    }

    public DayOfWeek(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id + ". " + name;
    }

    @Override
    public int compareTo(DayOfWeek t) {
        return Integer.compare(id, t.getId());
    }
}
