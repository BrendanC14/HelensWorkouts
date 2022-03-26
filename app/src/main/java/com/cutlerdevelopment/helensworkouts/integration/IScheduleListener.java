package com.cutlerdevelopment.helensworkouts.integration;

import java.util.Date;

public interface IScheduleListener {

    void templateSavedOnDate(Date date, String templateID);
    void templateRetrievedForDate(Date date,String templateID);
}
