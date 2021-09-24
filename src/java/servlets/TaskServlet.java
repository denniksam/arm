package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Сервлет для работы с задачами
 * @author samoylenko_d
 */
public class TaskServlet extends HttpServlet {
        
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Прием формы новой задачи
        String managerId       = req.getParameter( "managerId" ) ;
        String taskTitle       = req.getParameter( "taskTitle" ) ;
        String taskDescription = req.getParameter( "taskDescription" ) ;
        String taskDeadline    = req.getParameter( "taskDeadline" ) ;
        
        String taskMessage = null ;
        
        if( managerId == null || "".equals( managerId ) ) {
            taskMessage = "Не указан исполнитель" ;
        } else if( taskTitle == null || "".equals( taskTitle ) ) {
            taskMessage = "Не указано название задачи" ;
        } else if( taskDescription == null || "".equals( taskDescription ) ) {
            taskMessage = "Не указано описание задачи" ;
        } else if( taskDeadline == null || "".equals( taskDeadline ) ) {
            taskMessage = "Не указан срок задачи" ;
        } 
        
        HttpSession session = req.getSession() ;
        
        if( taskMessage == null ) {  // ни одна из проверок не установила сообщение - все ОК
            // Добавляем в БД
            if( services.Db.addTask( 
                    new orm.Task(
                            services.Auth.getUser().getId(),  // chief - current user
                            managerId, 
                            taskTitle, 
                            taskDescription, 
                            ( new java.sql.Date( new java.util.Date().getTime() ) ).toString() ,  // дата постановки - сейчас
                            taskDeadline )
            ) )
                taskMessage = "Задача добавлена" ;
            else 
                taskMessage = "Внутренняя ошибка" ;
        }
        else {
            // Сохраняем в сессии переданные данные (для повторной подставновки в форму)
            session.setAttribute( "taskManagerId", managerId ) ;
            session.setAttribute( "taskTaskTitle", taskTitle ) ;
            session.setAttribute( "taskTaskDescription", taskDescription ) ;
            session.setAttribute( "taskTaskDeadline", taskDeadline ) ;
            
        }
        
        // Сохраняем сообщение в сессии
        session.setAttribute( "taskMessage", taskMessage ) ;
        // Работа метода Post заканчивается перенаправлением
        resp.sendRedirect( req.getContextPath() + "/" ) ;
    }
    
}
