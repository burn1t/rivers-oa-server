package com.ocean.oaworkflowservice;

import com.ocean.dao.SystemUserDao;
import com.ocean.security.TokenManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class OaWorkflowServiceApplicationTests {

//    @Autowired
//    private SystemUserDao systemUserDao;
//
//    @Test
//    void contextLoads() {
//        System.out.println(systemUserDao.selectUserByUsername("admin"));
//    }

    @Autowired
    private TokenManager tokenManager;

    @Test
    void password() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // $2a$10$.758pfnk63n7fUNPLovJS.ydgs0gpSqH0nFrlxVTj/vLozzIk01NS
        System.out.println(encoder.encode("123456"));

    }

    @Test
    void text() {
        String demo = "1.2.3";
        System.out.println(demo.substring(0, demo.indexOf(".")));
    }

}
