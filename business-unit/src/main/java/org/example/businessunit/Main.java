package org.example.businessunit;

import org.example.businessunit.broker.BusinessUnitConsumer;
import org.example.businessunit.datamart.RecommenderDatamart;
import org.example.businessunit.view.ConsoleDashboard;

public class Main {
    public static void main(String[] args) {
        RecommenderDatamart datamart = new RecommenderDatamart();
        BusinessUnitConsumer consumer = new BusinessUnitConsumer(datamart);
        consumer.start();
        ConsoleDashboard dashboard = new ConsoleDashboard(datamart);
        dashboard.start();

    }
}
