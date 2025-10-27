package com.kartikay.sales_order_management_api;

import org.springframework.boot.SpringApplication;

public class TestSalesOrderManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SalesOrderManagementApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
