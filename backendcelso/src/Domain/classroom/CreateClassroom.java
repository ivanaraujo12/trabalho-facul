package Domain.classroom;

import Domain.discipline.Discipline;
import Domain.enums.Days;
import Domain.enums.Shift;
import Domain.teacher.Teacher;

public record CreateClassroom(int id, Days daysOfWeek, Shift shift, String schedule, Teacher teacher, Discipline discipline) {

}
