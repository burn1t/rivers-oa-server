package com.ocean.oaworkflowservice;

import com.ocean.enums.BusinessStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnumTest {

    @Test
    void outputEnum() {
        System.out.println(BusinessStatusEnum.PROCESS.getCode());
    }
}
