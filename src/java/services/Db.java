package services;
/**
 * Класс для работы с БД
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Db {
    
    private static final String PREFIX = "arm0_" ;
    
    private static Connection connection ;

    public static Connection getConnection() {
        if( connection == null ) {  // первый запрос
            String connectionString = 
                    "jdbc:oracle:thin:oracle/8f48iZCm@10.3.0.69:49161:XE";
                    // "jdbc:oracle:thin:system/root@10.2.205.13:1521:XE";
            try {
                DriverManager.registerDriver(
                    new oracle.jdbc.driver.OracleDriver()
                ) ;
                connection = DriverManager.getConnection(
                        connectionString
                ) ;
            } catch( SQLException ex ) {
                System.err.println("Db error: " + ex.getMessage() ) ;
            }
        }
        return connection;
    }
    
    private static boolean createTaskTable() {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = "CREATE TABLE " + PREFIX + "task ("
                + "id          RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,"
                + "chiefId     RAW(16),"
                + "managerId   RAW(16),"
                + "title       NVARCHAR2(64),"
                + "description NVARCHAR2(512),"
                + "startDate   DATE,"
                + "finishDate  DATE"            
                + ")" ;
            stmt.executeUpdate( query ) ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    public static boolean addTask( orm.Task task ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            /*String startDate = 
                    ( task.getStartDate().equals( "" ) )
                    ? "CURRENT_TIMESTAMP"
                    : "TO_DATE('"+task.getStartDate()+"','YYYY-MM-DD')";
            */
            query = String.format(
                "INSERT INTO %stask(chiefId,managerId,title,description,startDate,finishDate) "
                + "VALUES('%s','%s','%s','%s',TO_DATE('%s','YYYY-MM-DD'),TO_DATE('%s','YYYY-MM-DD'))",    
                PREFIX, 
                task.getChiefId(), 
                task.getManagerId(),
                task.getTitle(),
                task.getDescription(),
                task.getStartDate(),
                task.getFinishDate()
            ) ;
            stmt.executeQuery( query ) ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    public static boolean makeTasks() {
        if( addTask( new orm.Task(
                "CB0307F6EBFD9840E05011AC040030C8",
                "CB0307F6EBFE9840E05011AC040030C8",
                "Модуль авторизации",
                "Разработать и реализовать механизм авторизации пользователя",
                "2021-09-08",
                "2021-09-18"
        ) ) ) System.out.println("Task 1 - OK") ;
        else System.err.println("Task 1 - NO") ;
        
        if( addTask( new orm.Task(
                "CB0307F6EBFE9840E05011AC040030C8",
                "CB0307F6EBFD9840E05011AC040030C8",
                "Дизайн главной страницы",
                "Разработать и реализовать дизайн главной страницы",
                "2021-09-01",
                "2021-09-10"
        ) ) ) System.out.println("Task 2 - OK") ;
        else System.err.println("Task 2 - NO") ;
        
        if( addTask( new orm.Task(
                "CB0307F6EBFD9840E05011AC040030C8",
                "CB0307F6EBFF9840E05011AC040030C8",
                "ORM для задач",
                "Описать класс для отображения таблицы Task",
                "2021-09-02",
                "2021-09-06"
        ) ) ) System.out.println("Task 3 - OK") ;
        else System.err.println("Task 3 - NO") ;
        
        return true ;
    }
    
    /**
     * Получение списка задач
     * @param user Пользователь, для которого выбираются задачи
     * @return Список задач, в которых user постановщик или исполнитель
     */
    public static ArrayList<orm.Task> getTaksLits( orm.User user ) {
        ArrayList<orm.Task> result = null ;
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                    "SELECT    t.id,"
                            + "t.chiefId,"
                            + "t.managerId,"
                            + "t.title,"
                            + "t.description,"
                            + "TRUNC( t.startDate, 'J' ),"
                            + "t.finishDate,"
                            + "t.status "
                    + "FROM %stask t "
                    + "WHERE t.chiefId='%s' OR t.managerId='%s' "
                    + "ORDER BY t.status DESC, t.startDate DESC",
                    PREFIX,
                    user.getId(),
                    user.getId()
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                result = new ArrayList<>() ;
                do {
                    orm.Task task = 
                        new orm.Task(
                            res.getString( 1 ),  // id
                            res.getString( 2 ),  // chiefId
                            res.getString( 3 ),  // managerId
                            res.getString( 4 ),  // title
                            res.getString( 5 ),  // description
                            res.getString( 6 ),  // startDate
                            res.getString( 7 )   // finishDate
                    ) ;
                    task.setStatus( res.getInt( 8 ) ) ;
                    result.add( task ) ;
                    
                } while( res.next() ) ;
            } 
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
        }
        return result ;
    }
    
    public static boolean updateTaskStatus( String id, String action ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            // Удаление - установка статуса "-1" (реально - не удаляем)
            // "принять" - статус 1
            // "отклонить" - статус 2
            int status = 0 ;
            if( "del".equals( action ) ) status = -1 ;
            else if( "reject".equals( action ) ) status = 2 ;
            else if( "accept".equals( action ) ) status = 1 ;
            else return false ;
            
            query = String.format(
                    "UPDATE %stask t SET t.status= %d WHERE t.id='%s' ",
                    PREFIX, status, id
            ) ;
            stmt.executeUpdate( query ) ;
            return true ; 
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
        }
        return false ;
    }
    
    /**
     * Обновление срока окончания задачи (дедлайна)
     * @param id ид задачи
     * @param date новая дата в формате YYYY-MM-DD
     * @return успешность обновления true/false
     */
    public static boolean updateTaskDeadline( String id, String date ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                "UPDATE %stask t SET t.finishDate=TO_DATE('%s','YYYY-MM-DD') WHERE t.id='%s' ",
                PREFIX, date, id
            ) ;
            stmt.executeUpdate( query ) ;
            // stmt.executeUpdate( "COMMIT" ) ;
            getConnection().commit() ;
            return true ; 
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
        }
        return false ;
    }
    
    /**
     * Обновление описания задачи (текста)
     * @param id ид задачи
     * @param value новое описание
     * @return успешность обновления true/false
     */
    public static boolean updateTaskDescription( String id, String value ) {
        if( value == null || id == null ) return false ;
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                "UPDATE %stask t SET t.description='%s' WHERE t.id='%s' ",
                PREFIX, 
                value.replace( "'","''" ), 
                id
            ) ;
            stmt.executeUpdate( query ) ;
            // stmt.executeUpdate( "COMMIT" ) ;
            // getConnection().commit() ;
            return true ; 
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
        }
        return false ;
    }
    
    private static boolean createUsersTable() {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = "CREATE TABLE " + PREFIX + "users ("
               + "id       RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,"
               + "login    NVARCHAR2(32),"
               + "passhash CHAR(32),"
               + "name     NVARCHAR2(32),"
               + "avatar   NVARCHAR2(32)"
               + ")" ;
            stmt.executeUpdate( query ) ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    public static boolean addUser( orm.User user ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                "INSERT INTO %susers(login,passhash,name,avatar) VALUES('%s','%s','%s','%s')",    
                PREFIX, user.getLogin(), user.getPassHash(), user.getName(), user.getAvatar() 
            ) ;
            stmt.executeQuery( query ) ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    public static boolean makeUsers() {
        if( createUsersTable() ) {
            addUser( new orm.User( 
                    "admin", 
                    services.Hasher.MD5( "123" ), 
                    "Вениамин Простодухов",
                    "ava1.jpg" ) ) ;
            addUser( new orm.User( 
                    "moder", 
                    services.Hasher.MD5( "321" ), 
                    "Жанна Квашина",
                    "ava5.jpg" ) ) ;
            addUser( new orm.User( 
                    "user", 
                    services.Hasher.MD5( "555" ), 
                    "Изанбег Шульц",
                    "ava2.png" ) ) ;
            return true ;
        }
        return false ;
    }
    
    public static orm.User authUser( String login, String passw ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                "SELECT u.id, u.name, u.avatar FROM %susers u "
                + "WHERE u.login='%s' AND u.passhash='%s' "
                + "AND (U.IS_DELETED = 0 OR U.IS_DELETED IS NULL) ",
                PREFIX, 
                login.replace( "'", "''" ),  // экранирование (escaping)
                services.Hasher.MD5( passw )
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                return new orm.User( 
                        res.getString( 1 ),  // id
                        login, 
                        "",   // hash
                        res.getString( 2 ), // name
                        res.getString( 3 )  // avatar
                ) ;
            } else return null ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return null ;
        }
    }
    
    public static orm.User getUserById( String userId ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                    "SELECT u.login, u.name, u.avatar, u.is_deleted FROM %susers u WHERE u.id='%s'",
                    PREFIX, userId
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                orm.User user = new orm.User( 
                        userId,
                        res.getString( 1 ),  // login                        
                        "",   // hash
                        res.getString( 2 ), // name
                        res.getString( 3 )  // avatar
                ) ;
                user.setIsDeleted( res.getInt( 4 ) ) ;
                return user ;
            } else return null ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return null ;
        }
    }
    
    public static boolean isLoginUsed( String login ) {
        // Проверка логина на занятость
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                    "SELECT u.id FROM %susers u WHERE u.login='%s' ",
                    PREFIX, 
                    login.replace( "'", "''" )  // экранирование (escaping)
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                return true ;
            } else return false ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return true ;
        }
    }
    
    /**
     * Получение списка всех пользователей (кроме удаленных)
     * @return ArrayList<orm.User>
     */
    public static ArrayList<orm.User> getUsersList() {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                    "SELECT u.id, u.login, u.name, u.avatar, u.is_deleted "
                    + "FROM %susers u "
                    + "WHERE u.is_deleted IS NULL OR u.is_deleted = 0",
                    PREFIX
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                ArrayList<orm.User> list = new ArrayList<>() ;
                do {
                    orm.User user =
                        new orm.User( 
                            res.getString( 1 ),  // id        
                            res.getString( 2 ),  // login                        
                            "",   // hash
                            res.getString( 3 ), // name
                            res.getString( 4 )  // avatar
                        ) ;
                    user.setIsDeleted( res.getInt( 5 ) ) ;
                    list.add( user ) ;
                } while( res.next() ) ;
                return list ;
            } else return null ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return null ;
        }
    }
    
    /**
     * Получение списка всех пользователей (включая удаленных)
     * @return ArrayList<orm.User>
     */
    public static ArrayList<orm.User> getUsersListFull() {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            query = String.format(
                    "SELECT u.id, u.login, u.name, u.avatar, u.is_deleted FROM %susers u ",
                    PREFIX
            ) ;
            ResultSet res = stmt.executeQuery( query ) ;
            if( res.next() ) {
                ArrayList<orm.User> list = new ArrayList<>() ;
                do {
                    orm.User user =
                        new orm.User( 
                            res.getString( 1 ),  // id        
                            res.getString( 2 ),  // login                        
                            "",   // hash
                            res.getString( 3 ), // name
                            res.getString( 4 )  // avatar
                        ) ;
                    user.setIsDeleted( res.getInt( 5 ) ) ;
                    list.add( user ) ;
                } while( res.next() ) ;
                return list ;
            } else return null ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return null ;
        }
    }
    
    
    /**
     * Обновление данных в БД для переданного пользователя
     * @param user пользователь, который обновляется
     * @return true/false - успешность  обновления
     */
    public static boolean updateUser( orm.User user ) {
        String query = null ;
        try( 
            Statement stmt = getConnection().createStatement() 
        ) {
            boolean isFirst = true ;
            query = "UPDATE " + PREFIX + "users u SET " ;
            if( ! "".equals( user.getLogin() ) ) {
                query += "u.login = '" + user.getLogin() + "' " ;
                isFirst = false ;
            }
            if( user.getPassHash() != null &&
              ! "".equals( user.getPassHash() ) ) {
                if( ! isFirst ) query += ", " ;
                query += "u.passhash = '" + user.getPassHash() + "' " ;
                isFirst = false ;
            }
            if( user.getName() != null &&
              ! "".equals( user.getName() ) ) {
                if( ! isFirst ) query += ", " ;
                query += "u.name = '" + user.getName() + "' " ;
                isFirst = false ;
            }
            if( user.getAvatar()!= null &&
              ! "".equals( user.getAvatar() ) ) {
                if( ! isFirst ) query += ", " ;
                query += "u.avatar = '" + user.getAvatar() + "' " ;
            }
            query += " WHERE u.id = '" + user.getId() + "'" ;
            
            stmt.executeUpdate( query ) ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    /**
     * Удаление пользователя (пометка is_deleted)
     * @param uid идентификатор пользователя
     * @return успешность  обновления
     */
    public static boolean deleteUser( String uid ) {
        String query = "UPDATE " + PREFIX + "users u SET u.is_deleted=1 "
                + "WHERE u.id=?" ;
        try( 
            PreparedStatement stmt = getConnection().prepareStatement( query )
        ) {
            stmt.setString( 1, uid ) ;
            stmt.executeUpdate() ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
    
    /**
     * Восстановление пользователя (пометка is_deleted = 0)
     * @param uid идентификатор пользователя
     * @return успешность  обновления
     */
    public static boolean restoreUser( String uid ) {
        String query = "UPDATE " + PREFIX + "users u SET u.is_deleted=0 "
                + "WHERE u.id=?" ;
        try( 
            PreparedStatement stmt = getConnection().prepareStatement( query )
        ) {
            stmt.setString( 1, uid ) ;
            stmt.executeUpdate() ;
            return true ;
        } catch( SQLException ex ) {
            System.err.println( "Db error: " + ex.getMessage() + " : " + query ) ;
            return false ;
        }
    }
}
