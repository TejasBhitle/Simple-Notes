package tejas.recyclerview1;

public class MyData {
    private String Data_Title;
    private String Data_Content;
    private String Data_id;
    private String Data_date;
    private String Data_color;
    private String Data_isLocked;


    //Constructor
    public MyData(String data_Title, String data_Content,String data_id,String data_date,String data_color,String data_isLocked) {
        Data_Title = data_Title;
        Data_Content = data_Content;
        Data_id = data_id;
        Data_date = data_date;
        Data_color=data_color;
        Data_isLocked=data_isLocked;
    }

    //Getters and Setters

    public String getData_color() {
        return Data_color;
    }

    public void setData_color(String data_color) {
        Data_color = data_color;
    }

    public String getData_date() {
        return Data_date;
    }

    public void setData_date(String data_date) {
        Data_date = data_date;
    }

    public String getData_id() {
        return Data_id;
    }

    public void setData_id(String data_id) {
        Data_id = data_id;
    }

    public String getData_Title() {
        return Data_Title;
    }

    public void setData_Title(String data_Title) {
        Data_Title = data_Title;
    }

    public String getData_Content() {
        return Data_Content;
    }

    public void setData_Content(String data_Content) {
        Data_Content = data_Content;
    }

    public String getData_isLocked() {
        return Data_isLocked;
    }
}
