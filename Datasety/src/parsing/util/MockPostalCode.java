package parsing.util;

import java.util.List;

public class MockPostalCode {

    String city;
    List<Float> coordinates;
    Integer pop;
    String state;
    Integer _id;

    MockPostalCode() {};

    public MockPostalCode(String city, List<Float> coordinates, Integer pop, String state, Integer id) {
        this.city = city;
        this.coordinates = coordinates;
        this.pop = pop;
        this.state = state;
        this._id = id;
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
        return _id;
    }

    public void setId(Integer id) {
        this._id = id;
    }


    @Override
    public String toString() {
        return "MockPostalCode{" +
                "city='" + city + '\'' +
                ", coordinates=" + coordinates +
                ", pop=" + pop +
                ", state='" + state + '\'' +
                ", id=" + _id +
                '}';
    }
}
