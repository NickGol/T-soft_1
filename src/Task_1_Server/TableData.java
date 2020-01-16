package Task_1_Server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TableData {

    private String direction;
    private String user;
    private String data;

    private String time;

    public TableData(String direction, String user, String data) {
        this.direction = direction;
        this.user = user;
        this.data = data;
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("HH:mm:ss");
        time = formatForDateNow.format(dateNow);
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

}
