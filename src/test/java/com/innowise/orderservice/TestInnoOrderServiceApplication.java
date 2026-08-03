package com.innowise.orderservice;

import org.springframework.boot.SpringApplication;

public class TestInnoOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(InnoOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
