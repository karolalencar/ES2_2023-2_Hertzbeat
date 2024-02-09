package org.dromara.hertzbeat.alert.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

import org.dromara.hertzbeat.alert.service.AlertConvergeService;
import org.dromara.hertzbeat.common.entity.alerter.AlertConverge;
import org.dromara.hertzbeat.common.entity.dto.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AlertConvergeControllerTest {

    @Mock
    private AlertConvergeService alertConvergeService;

    @InjectMocks
    private AlertConvergeController alertConvergeController;

    private AlertConverge alertConverge;

    // Define a constant for the test
    private static final int MONITOR_NOT_EXIST_CODE = 3;

    @BeforeEach
    void setUp() {
        alertConverge = new AlertConverge();
        // Initialize alertConverge with test data
    }

    @Test
    void addNewAlertConverge_Success() {
        doNothing().when(alertConvergeService).validate(any(AlertConverge.class), any(Boolean.class));
        doNothing().when(alertConvergeService).addAlertConverge(any(AlertConverge.class));

        ResponseEntity<Message<Void>> response = alertConvergeController.addNewAlertConverge(alertConverge);

        assertEquals(OK, response.getStatusCode());
        verify(alertConvergeService).addAlertConverge(any(AlertConverge.class));
    }

    @Test
    void modifyAlertConverge_Success() {
        doNothing().when(alertConvergeService).validate(any(AlertConverge.class), any(Boolean.class));
        doNothing().when(alertConvergeService).modifyAlertConverge(any(AlertConverge.class));

        ResponseEntity<Message<Void>> response = alertConvergeController.modifyAlertConverge(alertConverge);

        assertEquals(OK, response.getStatusCode());
        verify(alertConvergeService).modifyAlertConverge(any(AlertConverge.class));
    }

    @Test
    void getAlertConverge_Success() {
        long id = 1L; // Example ID
        doReturn(alertConverge).when(alertConvergeService).getAlertConverge(id);

        ResponseEntity<Message<AlertConverge>> response = alertConvergeController.getAlertConverge(id);

        assertEquals(OK, response.getStatusCode());
        verify(alertConvergeService).getAlertConverge(id);
    }

    @Test
    void getAlertConverge_NotFound() {
        long id = 1L; // Example ID not found
        doReturn(null).when(alertConvergeService).getAlertConverge(id);

        ResponseEntity<Message<AlertConverge>> response = alertConvergeController.getAlertConverge(id);

        assertEquals(OK, response.getStatusCode());
        assertEquals(MONITOR_NOT_EXIST_CODE, response.getBody().getCode());
        verify(alertConvergeService).getAlertConverge(id);
    }
}
