package parsing.util;

import java.util.List;

/**
 * Created by pawel on 02.06.2016.
 */
public class MockPostalCode {

    String city;
    List<Float> coordinates;
    Integer pop;
    String state;
    Integer id;

    MockPostalCode() {};

    public MockPostalCode(String city, List<Float> coordinates, Integer pop, String state, Integer id) {
        this.city = city;
        this.coordinates = coordinates;
        this.pop = pop;
        this.state = state;
        this.id = id;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getPop() {
        return pop;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "MockPostalCode{" +
                "city='" + city + '\'' +
                ", coordinates=" + coordinates +
                ", pop=" + pop +
                ", state='" + state + '\'' +
                ", id=" + id +
                '}';
    }
}
