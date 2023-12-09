package Domain.classroom;

import Domain.enums.Days;
import Domain.enums.Shift;
public class Classroom {
    private int id;
    private Days daysOfWeek;
    private Shift shift;
    private String schedule;
    private final int teacherId;
    private final int disciplineId;


    public Classroom(int id, Days daysOfWeek, Shift shift, String schedule, final int teacherId, final int disciplineId) {
        this.daysOfWeek = daysOfWeek;
        this.shift = shift;
        this.schedule = schedule;
        this.teacherId = teacherId;
        this.disciplineId = disciplineId;
    }

    public int getId() {

        return id;
    }

    public Days getDaysOfWeek() {

        return daysOfWeek;
    }

    public void setDaysOfWeek(Days daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Shift getShift() {

        return shift;
    }

    public void setShift(Shift shift) {

        this.shift = shift;
    }

    public String getSchedule() {

        return schedule;
    }

    public void setSchedule(String schedule) {

        this.schedule = schedule;
    }

    public int getTeacher() {

        return teacherId;
    }


    public int getDiscipline() {

        return disciplineId;
    }


}
