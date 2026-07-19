package com.crm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CalendarControllerTest {
    @Test
    void miniCalendarDaysUseAllAvailableWidthAtNormalSizes() {
        assertEquals(29.0, CalendarController.miniCalendarCellSize(221, 3), 0.0001);
        assertEquals(34.0, CalendarController.miniCalendarCellSize(256, 3), 0.0001);
    }

    @Test
    void miniCalendarDaysStayInsideNarrowGridsAndStopGrowingAtTheVisualMaximum() {
        assertEquals(10.0, CalendarController.miniCalendarCellSize(88, 3), 0.0001);
        assertEquals(40.0, CalendarController.miniCalendarCellSize(400, 3), 0.0001);
    }
}
