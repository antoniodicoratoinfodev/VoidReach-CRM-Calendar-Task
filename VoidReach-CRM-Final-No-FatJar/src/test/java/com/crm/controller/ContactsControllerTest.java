package com.crm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactsControllerTest {
    @Test
    void clipboardValueIsTabularAndNullSafe() {
        assertEquals("", ContactsController.clipboardValue(null));
        assertEquals("first second third", ContactsController.clipboardValue("first\tsecond\nthird"));
    }
}
