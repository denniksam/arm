package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Сервлет управления задачами - удаление, изменения статуса
 * @author samoylenko_d
 */
public class TaskControlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession() ;
        String messageTag = "TaskControlMessage" ;
        
        String id = req.getPathInfo().substring( 1 ) ;  // убираем первый "/"
        String action = req.getParameter( "action" ) ;
        if( "date".equals( action ) ) {
            // запрос на изменение срока задачи
            String date = req.getParameter( "date" ) ;
            services.Db.updateTaskDeadline( id, date ) ;
        } else if( "description".equals( action ) ) {
            // запрос на изменение описания (текста) задачи
            String value = req.getParameter( "value" ) ;
            services.Db.updateTaskDescription( id, value ) ;
        } else {
            // изменение статуса задачи
            if( services.Db.updateTaskStatus( id, action ) ) {
                session.setAttribute( messageTag, "Статус задачи успешно изменен" ) ;
            } else {
                session.setAttribute( messageTag, "Ошибка изменения статуса задачи" ) ;
            }
        }
        // после изменения задач переходим на главную страницу (к списку)
        // req.getRequestDispatcher( "/index.jsp" ).forward( req, resp ) ;
        resp.sendRedirect( req.getContextPath() + "/" ) ;
    }
    
}

/* 
    Задания:
    - редактирование срока задачи только для "своих" задач
    - реализовать возможность редактирования описания (текста) задачи
*/