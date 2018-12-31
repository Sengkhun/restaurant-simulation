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
public class Table {

    int id;
    int number_of_chairs;   // number of chair that one table have

    int waiter_id = -1;   // table id

    int customer_id;   // customer id
    int customer_in;   // time customer arrive
    int customer_out;  // time customer leave

    public Table(int id, int number_of_chairs) {
        this.id = id;
        this.number_of_chairs = number_of_chairs;
        this.customer_out = 0;
    }

}
