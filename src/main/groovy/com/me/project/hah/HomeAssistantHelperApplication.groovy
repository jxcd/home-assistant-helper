package com.me.project.hah


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class HomeAssistantHelperApplication {

    static void main(String[] args) {
        SpringApplication.run(HomeAssistantHelperApplication, args)
    }

}
