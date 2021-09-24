/*
 * RegServlet - сервлет регистрации нового пользователя / личный кабинет
 */
package servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class RegServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher( "/userdata.jsp" )
           .forward( req, resp ) ;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // передаются данные формы
        orm.User user = services.Auth.getUser() ;
        HttpSession session = req.getSession() ;
        String message = null ;
        
        // Извлекаем параметры - данные из полей формы
        String userLogin = req.getParameter( "userLogin" ) ;
        String pass1     = req.getParameter( "pass1" ) ;
        String pass2     = req.getParameter( "pass2" ) ;
        String userName  = req.getParameter( "userName" ) ;
        Part filePart    = req.getPart( "userAvatar" ) ;
        
        if( user == null ) {
            // Регистрация - нет авторизованного пользователя
            // Проверяем данные шаг за шагом
            if( userLogin == null || "".equals( userLogin ) ) {
                message = "Логин не может быть пустым" ;
            } else /* Проверяем логин на занятость */ 
            if( services.Db.isLoginUsed( userLogin ) ) {
                message = "Логин уже занят другим пользователем" ;
            } else if( pass1 == null || "".equals( pass1 ) ) {
                message = "Пароль не может быть пустым" ;
            } else if( ! pass1.equals( pass2 ) ) {
                message = "Пароли не совпадают" ;
            } else if( userName == null || "".equals( userName ) ) {
                message = "Имя не может быть пустым" ;
            } else {
                // создаем объект для нового пользователя
                orm.User newUser = new orm.User() ;
                // заполняем обязательными данными
                newUser.setLogin( userLogin ) ;
                newUser.setPassHash( services.Hasher.MD5( pass2 ) ) ;
                newUser.setName( userName ) ;
                // аватарку не требуем. Если есть, загружаем
                try {
                    String newAvatar = uploadAvatar( filePart, req ) ;
                    if( newAvatar != null ) {
                        newUser.setAvatar( newAvatar ) ;
                    }
                }
                catch( IOException ex ) {
                    message = "Проблемы с загрузкой файла" ;
                    System.err.println( ex.getMessage() ) ;
                }
                
                if( message == null ) {  // предыдущие блоки не создали ошибки
                    // newUser заполнен и готов к внесению в БД
                    if( services.Db.addUser( newUser ) )
                        message = "Регистрация прошла успешно" ;
                    else 
                        message = "Внутренняя ошибка" ;
                }
            }
        } else {
            // Обновление данных - пользователь авторизован
            // Проверяем данные, НО: если данных нет, то считаем, что изменений нет
            message = null ;  // сбрасываем сообщение
            if( userLogin != null && ! "".equals( userLogin ) ) {
                // переданы данные о логине
                // Проверяем, что логин другой (меняется)
                if( ! userLogin.equals( user.getLogin() ) ) {
                    // переданный логин (userLogin) не равен сохраненному user.getLogin()
                    // проверяем логин на занятость
                    if( services.Db.isLoginUsed( userLogin ) ) {
                        message = "Логин уже занят другим пользователем" ;
                    } else {
                        // Есть новый логин, он не занят - обновляем данные
                        user.setLogin( userLogin ) ;
                    }
                } // иначе оставляем логин без изменений
            }
            
            if( message == null ) {  // предыдущий блок не создал ошибки
                // Пароли...
                if( pass1 != null && ! "".equals( pass1 ) ) {
                    // Передан первый пароль, тогда проверяем и второй
                    if( ! pass1.equals( pass2 ) ) {
                        message = "Пароли не совпадают" ;
                    } else {
                        // Пароли переданы и они одинаковые - обновляем
                        user.setPassHash( services.Hasher.MD5( pass2 ) ) ;
                    }
                }
            }
            
            if( message == null ) {  // предыдущие блоки не создали ошибки
                // Имя...
                if( userName != null && ! "".equals( userName ) ) {
                    // Проверяем, что имя отличается от хранимого
                    if( ! userName.equals( user.getName() ) ) {
                        user.setName( userName ) ;
                    }
                }
            }
            
            if( message == null ) { 
                // файл - аватарка
                try {
                    String newAvatar = uploadAvatar( filePart, req ) ;
                    if( newAvatar != null ) {
                        // задание: удалить старый файл аватара перед заменой
                        File oldAvatar = new File(
                                req.getServletContext().getRealPath( "/ava/" )
                                + "\\"
                                + user.getAvatar()
                        ) ;
                        oldAvatar.delete() ;
                        
                        user.setAvatar( newAvatar ) ;
                    }
                }
                catch( IOException ex ) {
                    message = "Проблемы с загрузкой файла" ;
                    System.err.println( ex.getMessage() ) ;
                }
            }
            
            if( message == null ) {
                // объект user заполнен новыми данными - обновляем
                if( services.Db.updateUser( user ) ) {
                    message = "Обновлено успешно" ;
                } else {
                    message = "Внутренняя ошибка" ;
                }
            }
        }
        
        session.setAttribute( "regMessage", message ) ;
        resp.sendRedirect( req.getRequestURI() ) ;
    }
    
    /**
     * Загрузка и сохранение файла (аватарки)
     * @param filePart данные формы
     * @return имя сохраненного файла
     */
    private String uploadAvatar( Part filePart, HttpServletRequest req ) throws IOException {
        if( filePart.getSize() > 0 ) {  // По размеру определяем есть ли файл
            String fileName = filePart.getSubmittedFileName() ;
            // генерируем случайное имя для файла
            // отделяем расширение
            String ext = fileName.substring(      // часть строки
                    fileName.lastIndexOf( "." )   // от последней "."
            ) ;
            // путь к "рабочей" папке
            String path = req.getServletContext()
                             .getRealPath( "/ava/" ) ;
            // поскольку файл может существовать, делаем цикл
            File file ;
            do {
                // генерируем имя
                fileName = services  
                            .Hasher
                            .MD5( fileName )
                            .substring( ext.length() )
                        + ext ;
                // создаем файловый объект
                file = new File( path + "\\" + fileName ) ;
            } while( file.exists() ) ;  // если файл есть - повторяем цикл
            
            Files.copy(
                    filePart.getInputStream(),
                    file.toPath(), 
                    StandardCopyOption.REPLACE_EXISTING ) ;
            
            return fileName ;               
        }
        return null ;  // size - 0, сохранения не было
    }
    
}
