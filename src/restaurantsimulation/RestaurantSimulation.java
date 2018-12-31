/*
 * Feature Added
 * - number of people come per group
 * - number of chairs per table
 * - the ability that waiter can serve tables at a time
 */
package restaurantsimulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantSimulation {
    public static void main(String[] args) {

        // Total tables = 12
        int [][] numTables = { 
            {2, 6}, // there are 6 two chairs tables
            {4, 3}, // there are 3 four chairs tables
            {6, 3} // there are 3 six chairs tables
        };

        // Total waiters = 4
        int [][] numWaiters = {
            {1, 2}, // 2 waiters can serve only 1 table at a time
            {2, 2}, // 2 waiters can serve 2 tables at a time
        };

        // initial customer info
        int numCustomers = 200;
        int maxNumCustomerPerGroup = 6; // maximum number of people come per group
        int [] duration_before = { 5, 10 }; // for random 5 to 10
        int [] duration_ordering = { 30, 40 };   // for random 15 to 30

        Table [] tables = makeArrayOfTables(numTables);
        Waiter [] waiters = makeArrayOfWaiters(numWaiters);
        Customer [] customers = makeArrayOfCustomers(
                numCustomers, 
                maxNumCustomerPerGroup, 
                duration_before, 
                duration_ordering
        );

        int cumulativeTime = 0;
        int cumulativeWaitingTime = 0;
        int maxWait = 0;
        int numberOfCustomerWait = 0;

        for (int i = 0; i < numCustomers; i++) {

            cumulativeTime += customers[i].duration_before;
            int numberOfPeople = customers[i].number_of_people;
            Table nextTable = checkAvailableTables(tables, numberOfPeople);
            Waiter nextWaiter = checkAvailableWaiters(tables, waiters);
            int tableNextAvailableTime = nextTable.customer_out;
            int waiterNextAvailableTime = getWaiterNextAvailableTime(tables, nextWaiter);
            Customer nextCustomer = customers[i];

            // assign customer and waiter to table
            nextTable.customer_id = nextCustomer.id;
            nextTable.waiter_id = nextWaiter.id;
            nextTable.customer_in = max(cumulativeTime, tableNextAvailableTime, waiterNextAvailableTime);
            nextTable.customer_out = nextTable.customer_in + nextCustomer.duration_ordering;

            int waitTime = nextTable.customer_in - cumulativeTime;
            cumulativeWaitingTime += waitTime;
            maxWait = waitTime > maxWait ? waitTime : maxWait;
            numberOfCustomerWait = waitTime > 0 ? numberOfCustomerWait + 1 : numberOfCustomerWait;

            //Report:
            System.out.format("time: %-7d", cumulativeTime);
            System.out.format("customer: %-10s", (nextCustomer.id + 1) + "(" + nextCustomer.number_of_people + ")");
            System.out.format("table: %-10s", (nextTable.id + 1) + "(" +  nextTable.number_of_chairs + ")");
            System.out.format("waiter: %-7d", nextWaiter.id + 1);
            System.out.format("duration: %-7d", nextCustomer.duration_ordering);
            System.out.format("customer_in: %-7d", nextTable.customer_in);
            System.out.format("customer_out: %-7d", nextTable.customer_out);
            System.out.format("wait: %-7d", + waitTime);
            System.out.println();
        }

        System.out.println();
        System.out.println("Maxumin wait time: " + maxWait);
        System.out.println("Average wait time: " + (numberOfCustomerWait == 0 ? 0 :(cumulativeWaitingTime / numberOfCustomerWait)));
        System.out.println("Total wait time: " + cumulativeWaitingTime);


    }   // end of main

    public static int max(int num1, int num2, int num3){
        int max = num1;
        max = max < num2 ? num2 : max;
        max = max < num3 ? num3 : max;
        return max;
    }
    public static int min(int num1, int num2){return num1 > num2 ? num2 : num1;}

    public static Table checkAvailableTables(Table [] tables, int numberOfPeople) {
        int earliestTimeOff = 0;
        Table table = null;

        for (int i = 0; i < tables.length; i++) {
            if (tables[i].number_of_chairs >= numberOfPeople) {
                table = tables[i];
                earliestTimeOff = table.customer_out;
                break;
            }
        }

        for(int i = 0; i < tables.length; i++) {
            if (tables[i].number_of_chairs >= numberOfPeople && earliestTimeOff > tables[i].customer_out) {
                earliestTimeOff = tables[i].customer_out;
                table = tables[i];
            }
        }
        return table;

    } // end of checkAvailableTables

    public static Waiter checkAvailableWaiters(Table [] tables, Waiter [] waiters) {
        int earliestTimeOff = -1;
        Table tempTable;
        Waiter tempWaiter;
        Waiter nextWaiter = null;


        for (int i = 0; i < waiters.length; i++) {
            tempWaiter = waiters[i];
            if (tempWaiter.neverServe) {
                tempWaiter.neverServe = false;
                return tempWaiter;
            }
        }

        // 2d array to store waiterId and waiterEarliestTimeOff
        int [][] allWaitersEarliestTimeOff = new int[waiters.length][2];
        for (int i = 0; i < waiters.length; i++) {
            int timeOff = getWaiterNextAvailableTime(tables, waiters[i]);
            allWaitersEarliestTimeOff[i] = new int[]{ i, timeOff };
        }

        // get waiter who have the nearest timeOff
        earliestTimeOff = allWaitersEarliestTimeOff[0][1];
        nextWaiter = waiters[allWaitersEarliestTimeOff[0][0]];
        for (int i = 1; i < waiters.length; i++) {
            if (earliestTimeOff > allWaitersEarliestTimeOff[i][1]) {
                int waiterId = allWaitersEarliestTimeOff[i][0];
                nextWaiter = waiters[waiterId];
            }
        }
        
        return nextWaiter;

    } // end of checkAvailableWaiters

    public static int getWaiterNextAvailableTime(Table [] tables, Waiter waiter) {
        int earliestTimeOff = -1;
        Table tempTable = null;
        List<Integer> waiterTimeOff = new ArrayList<Integer>();
        int ability_serve_at_one_time = waiter.ability_serve_at_one_time;
        int numberOfServeTable = 0;

        for (int i = 0; i < tables.length; i++) {
            tempTable = tables[i];
            if (tempTable.waiter_id == waiter.id) {
                waiterTimeOff.add(tempTable.customer_out);
            }
        }

        numberOfServeTable = waiterTimeOff.size();
        if (numberOfServeTable == 0) {
            return 0;
        }

        // sort desc
        Collections.sort(waiterTimeOff, Collections.reverseOrder());

        int index = numberOfServeTable < ability_serve_at_one_time ? numberOfServeTable : ability_serve_at_one_time;
        earliestTimeOff = waiterTimeOff.get(index - 1);

        return earliestTimeOff;

    } // end of getWaiterNextAvailableTime

    public static int rand(int from, int to) {
        return (int)((Math.random() * (to - from)) + from);
    }

    public static Table [] makeArrayOfTables(int [][] numTables) {
        
        // find number of table to create
        int length = 0;
        for (int[] table : numTables) {
            // table[1] is the number of tables
            length += table[1];
        }
        
        // assign table with number of chair
        Table [] tables = new Table[length];
        int index = 0;
        for (int[] table : numTables) {
            for (int i = 0; i < table[1]; i++) {
                tables[index] = new Table(index, table[0]);
                index++;
            }
        }
        
        return tables;

    }   // end of makeArrayOfTables

    public static Waiter [] makeArrayOfWaiters(int [][] numWaiters) {
        
        // find number of waiter to create
        int length = 0;
        for (int[] waiter : numWaiters) {
            // waiter[1] is the number of waiters
            length += waiter[1];
        }
        
        // assign waiter with number of chair
        Waiter [] waiters = new Waiter[length];
        int index = 0;
        for (int[] waiter : numWaiters) {
            for (int i = 0; i < waiter[1]; i++) {
                waiters[index] = new Waiter(index, waiter[0]);
                index++;
            }
        }
        
        return waiters;

    }   // end of makeArrayOfWaiters

    public static Customer [] makeArrayOfCustomers(int numCustomers, int maxNumberCustomerPerGroup, int [] duration_before, int [] duration_ordering) {
        
        Customer [] customers = new Customer[numCustomers];
        for(int i = 0; i < numCustomers; i++) {
            customers[i] = new Customer();
            customers[i].id = i;
            customers[i].number_of_people = rand(1, maxNumberCustomerPerGroup);
            customers[i].duration_before = rand(duration_before[0], duration_before[1]);
            customers[i].duration_ordering = rand(duration_ordering[0], duration_ordering[1]);
        }

        return customers;

    }   // end of makeArrayOfCustomers
    
}
