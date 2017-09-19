package com.gytoth.tlog16rs.entities;

import com.gytoth.tlog16rs.core.NotNewMonthException;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Entity;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "time_logger")
@NoArgsConstructor
public class TimeLogger {

    @Id
    @GeneratedValue
    int id;

    String name;

    public TimeLogger(String name) {
        this.name = name;
    }

    @OneToMany(cascade = ALL, fetch = LAZY)
    private List<WorkMonth> months = new ArrayList<>();

    protected boolean isNewMonth(WorkMonth month) {
        return months.stream().noneMatch(o -> o.getDate().equals(month.getDate()));
    }

    public void addMonth(WorkMonth month) throws NotNewMonthException {
        if (isNewMonth(month)) {
            months.add(month);
        } else {
            throw new NotNewMonthException("This month is already added!");
        }
    }

}
