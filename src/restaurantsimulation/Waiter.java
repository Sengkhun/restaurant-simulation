/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restaurantsimulation;

/**
 *
 * @author sengkhunlim
 */
public class Waiter {

    int id;
    int ability_serve_at_one_time; // how many table waiter can serve at one time
    Boolean neverServe = true;

    public Waiter(int id) {
        this.id = id;
        this.ability_serve_at_one_time = 3; // default value is 3
    }

    public Waiter(int id, int ability_serve_at_one_time) {
        this.id = id;
        this.ability_serve_at_one_time = ability_serve_at_one_time;
    }

}
