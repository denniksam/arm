package orm;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

/**
 * ORM класс для "задачи"
 * @author samoylenko_d
 */
public class Task {
    private String id ;           // UUID - строковое представление
    private String chiefId ;      // Постановщик задачи (id из табл. Users)
    private String managerId ;    // Исполнитель
    private String title ;        // короткое название
    private String description ;  // описание
    private java.sql.Date startDate ;   // дата постановки
    private java.sql.Date finishDate ;  // дата окончания
    private int status ;          // состояние задачи - в работе (0) / принятая / удаленная (-1) / ... 

    /**
     * Дней до окончания задачи
     * @return положительное или отрицательное число - дней до (или от) окончания
     */
    public int daysLeft() {
        return Period.between( LocalDate.now(), finishDate.toLocalDate() ).getDays() ;
    }
    
    public Task(
            String chiefId, 
            String managerId, 
            String title, 
            String description, 
            String startDate, 
            String finishDate
    ) {
        this.chiefId     = chiefId;
        this.managerId   = managerId;
        this.title       = title;
        this.description = description;
        this.startDate   = java.sql.Date.valueOf( startDate ) ;
        this.finishDate  = java.sql.Date.valueOf( finishDate ) ;
    }

    public Task(
            String id, 
            String chiefId, 
            String managerId, 
            String title, 
            String description, 
            String startDate, 
            String finishDate
    ) {
        this.id          = id;
        this.chiefId     = chiefId;
        this.managerId   = managerId;
        this.title       = title;
        this.description = description;
        this.startDate   = java.sql.Date.valueOf( 
                startDate.substring( 0, startDate.indexOf( " " ) ) ) ;
        this.finishDate  = java.sql.Date.valueOf( 
                finishDate.substring( 0, finishDate.indexOf( " " ) ) ) ; 
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChiefId() {
        return chiefId;
    }

    public void setChiefId(String chiefId) {
        this.chiefId = chiefId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
}
